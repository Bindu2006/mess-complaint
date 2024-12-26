package com.example.myapplication.ui.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentAdminLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class AdminLoginFragment : Fragment() {
    private var _binding: FragmentAdminLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminLoginBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (validateInput(email, password)) {
                binding.progressBar.visibility = View.VISIBLE
                binding.loginButton.isEnabled = false

                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        binding.progressBar.visibility = View.GONE
                        binding.loginButton.isEnabled = true

                        when (email) {
                            "admin2@gmail.com" -> {
                                Log.d("AdminLogin", "Admin2 login successful")
                                Snackbar.make(binding.root, "Admin-02 Login Successful", Snackbar.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.nav_admin2_complaints)
                            }
                            "admin3@gmail.com" -> {
                                Log.d("AdminLogin", "Admin3 login successful")
                                Snackbar.make(binding.root, "Admin-03 Login Successful", Snackbar.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.nav_admin3_complaints)
                            }
                            else -> {
                                Log.d("AdminLogin", "Unauthorized email: $email")
                                Snackbar.make(binding.root, "Unauthorized admin access", Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        binding.progressBar.visibility = View.GONE
                        binding.loginButton.isEnabled = true
                        
                        val errorMessage = when (e) {
                            is FirebaseAuthInvalidUserException -> "Account not found. Please check your email."
                            is FirebaseAuthInvalidCredentialsException -> "Invalid password. Please try again."
                            else -> "Login failed: ${e.message}"
                        }
                        Log.e("AdminLogin", "Login error", e)
                        Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
                    }
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.emailInput.error = "Email is required"
            return false
        }
        if (password.isEmpty()) {
            binding.passwordInput.error = "Password is required"
            return false
        }
        if (email != "admin2@gmail.com" && email != "admin3@gmail.com") {
            binding.emailInput.error = "Invalid admin email"
            return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 