package com.example.myapplication.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.FragmentAdminComplaintsBinding
import com.example.myapplication.model.GrievanceStatus
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class AdminComplaintsFragment : Fragment() {
    private var _binding: FragmentAdminComplaintsBinding? = null
    private val binding get() = _binding!!
    private val database = FirebaseDatabase.getInstance()
    private val complaintsRef = database.getReference("complaints")
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

    data class Complaint(
        val id: String = "",
        val title: String = "",
        val description: String = "",
        val category: String = "",
        val status: String = "",
        val timestamp: Long = 0,
        val studentId: String = ""
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminComplaintsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.visibility = View.VISIBLE
        binding.complaintsText.text = "Loading complaints..."
        loadComplaints()
    }

    private fun loadComplaints() {
        complaintsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.progressBar.visibility = View.GONE
                
                if (!snapshot.exists()) {
                    binding.complaintsText.text = "No complaints found"
                    return
                }

                val complaintsText = StringBuilder()
                var unresolvedCount = 0

                for (complaintSnapshot in snapshot.children) {
                    try {
                        val complaint = complaintSnapshot.getValue(Complaint::class.java)
                        complaint?.let {
                            // Only show complaints that are not in RESOLVED state
                            if (it.status.uppercase() != "RESOLVED") {
                                unresolvedCount++
                                complaintsText.append("Complaint #$unresolvedCount\n")
                                complaintsText.append("Category: ${it.category}\n")
                                complaintsText.append("Title: ${it.title}\n")
                                complaintsText.append("Description: ${it.description}\n")
                                complaintsText.append("Status: ${it.status}\n")
                                complaintsText.append("Student ID: ${it.studentId}\n")
                                complaintsText.append("Time: ${dateFormat.format(Date(it.timestamp))}\n")
                                complaintsText.append("------------------------\n\n")
                            }
                        }
                    } catch (e: Exception) {
                        complaintsText.append("Error loading complaint: ${e.message}\n\n")
                    }
                }

                if (unresolvedCount == 0) {
                    binding.complaintsText.text = "No unresolved complaints found"
                } else {
                    binding.complaintsText.text = complaintsText.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
                binding.complaintsText.text = "Failed to load complaints: ${error.message}"
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}