package com.example.myapplication.ui.menu

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.MessMenuAdapter
import com.example.myapplication.databinding.FragmentMessMenuBinding
import com.example.myapplication.model.MessMenuItem
import com.example.myapplication.model.MessMenuData
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*

class MessMenuFragment : Fragment() {
    private var _binding: FragmentMessMenuBinding? = null
    private val binding get() = _binding!!
    private var isEditing = false
    private val database = FirebaseDatabase.getInstance()
    private val menuRef = database.getReference("messMenu")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupEditingControls()
        loadMenuFromFirebase()
    }

    private fun setupRecyclerView() {
        binding.menuRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MessMenuAdapter(
                menuItems = MessMenuData.menuItems,
                isEditing = isEditing
            ) { updatedItem ->
                updateMenuItem(updatedItem)
            }
        }
    }

    private fun setupEditingControls() {
        binding.editButton.setOnClickListener {
            isEditing = true
            binding.editButton.visibility = View.GONE
            binding.saveButton.visibility = View.VISIBLE
            (binding.menuRecyclerView.adapter as? MessMenuAdapter)?.setEditMode(true)
        }

        binding.saveButton.setOnClickListener {
            isEditing = false
            binding.editButton.visibility = View.VISIBLE
            binding.saveButton.visibility = View.GONE
            (binding.menuRecyclerView.adapter as? MessMenuAdapter)?.setEditMode(false)
            saveFullMenuToFirebase()
        }
    }

    private fun loadMenuFromFirebase() {
        binding.connectionStatus.apply {
            visibility = View.VISIBLE
            setTextColor(Color.BLUE)
            text = "Loading menu from database..."
        }
        binding.progressBar.visibility = View.VISIBLE

        // Hide loading message after 5 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            binding.connectionStatus.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
        }, 5000) // 5 seconds

        menuRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    MessMenuData.menuItems.clear()
                    
                    if (!snapshot.exists()) {
                        Log.d("MessMenu", "No data in Firebase, initializing with default menu")
                        saveFullMenuToFirebase()
                        return
                    }

                    for (daySnapshot in snapshot.children) {
                        val day = daySnapshot.key ?: continue
                        
                        val breakfastList = (daySnapshot.child("breakfast").value as? List<*>)?.mapNotNull { it.toString() } ?: emptyList()
                        val lunchList = (daySnapshot.child("lunch").value as? List<*>)?.mapNotNull { it.toString() } ?: emptyList()
                        val snacksList = (daySnapshot.child("snacks").value as? List<*>)?.mapNotNull { it.toString() } ?: emptyList()
                        val dinnerList = (daySnapshot.child("dinner").value as? List<*>)?.mapNotNull { it.toString() } ?: emptyList()

                        MessMenuData.menuItems.add(
                            MessMenuItem(
                                day = day,
                                breakfast = breakfastList,
                                lunch = lunchList,
                                snacks = snacksList,
                                dinner = dinnerList
                            )
                        )
                    }

                    binding.menuRecyclerView.adapter?.notifyDataSetChanged()

                } catch (e: Exception) {
                    Log.e("MessMenu", "Error loading menu", e)
                    Snackbar.make(binding.root, "Error loading menu: ${e.message}", Snackbar.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Snackbar.make(binding.root, "Database error: ${error.message}", Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun updateMenuItem(updatedItem: MessMenuItem) {
        binding.progressBar.visibility = View.VISIBLE
        binding.connectionStatus.apply {
            setTextColor(Color.BLUE)
            text = "Updating menu item..."
        }

        // Update local data
        val index = MessMenuData.menuItems.indexOfFirst { it.day == updatedItem.day }
        if (index != -1) {
            MessMenuData.menuItems[index] = updatedItem
        }

        // Update Firebase
        val updates = mapOf(
            updatedItem.day to mapOf(
                "breakfast" to updatedItem.breakfast,
                "lunch" to updatedItem.lunch,
                "snacks" to updatedItem.snacks,
                "dinner" to updatedItem.dinner
            )
        )

        menuRef.updateChildren(updates)
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                binding.connectionStatus.apply {
                    setTextColor(Color.GREEN)
                    text = "Menu updated successfully"
                }
                binding.menuRecyclerView.adapter?.notifyItemChanged(index)
                
                // Show success message
                Snackbar.make(binding.root, "Menu updated successfully", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.GREEN)
                    .setTextColor(Color.BLACK)
                    .show()
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = View.GONE
                binding.connectionStatus.apply {
                    setTextColor(Color.RED)
                    text = "Failed to update menu: ${e.message}"
                }
                
                // Show error message
                Snackbar.make(binding.root, "Failed to update menu: ${e.message}", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(Color.RED)
                    .show()
            }
    }

    private fun saveFullMenuToFirebase() {
        binding.progressBar.visibility = View.VISIBLE
        binding.connectionStatus.apply {
            setTextColor(Color.BLUE)
            text = "Saving menu to database..."
        }

        val menuMap = MessMenuData.menuItems.associate { menuItem ->
            menuItem.day to mapOf(
                "breakfast" to menuItem.breakfast,
                "lunch" to menuItem.lunch,
                "snacks" to menuItem.snacks,
                "dinner" to menuItem.dinner
            )
        }

        menuRef.setValue(menuMap)
            .addOnSuccessListener {
                binding.connectionStatus.apply {
                    setTextColor(Color.GREEN)
                    text = "Full menu saved successfully"
                }
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                binding.connectionStatus.apply {
                    setTextColor(Color.RED)
                    text = "Failed to save menu: ${e.message}"
                }
                binding.progressBar.visibility = View.GONE
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 