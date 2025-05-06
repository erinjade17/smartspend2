package com.example.budgettrackerapp.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.budgettrackerapp.R
import com.example.budgettrackerapp.data.AppDatabase
import com.example.budgettrackerapp.databinding.FragmentLoginBinding
import com.example.budgettrackerapp.repository.BudgetGoalRepository
import com.example.budgettrackerapp.repository.CategoryRepository
import com.example.budgettrackerapp.repository.ExpenseRepository
import com.example.budgettrackerapp.repository.UserRepository
import com.example.budgettrackerapp.utils.SessionManager
import com.example.budgettrackerapp.viewmodels.AuthViewModel
import com.example.budgettrackerapp.viewmodels.ViewModelFactory
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            // Initialize session manager
            sessionManager = SessionManager(requireContext())

            // Check if user is already logged in
            if (sessionManager.isLoggedIn()) {
                navigateToHome()
                return
            }

            // Initialize ViewModel
            val database = AppDatabase.getDatabase(requireContext())
            val userRepository = UserRepository(database.userDao())
            val categoryRepository = CategoryRepository(database.categoryDao())
            val expenseRepository = ExpenseRepository(database.expenseDao())
            val budgetGoalRepository = BudgetGoalRepository(database.budgetGoalDao())
            val factory = ViewModelFactory(
                userRepository = userRepository,
                categoryRepository = categoryRepository,
                expenseRepository = expenseRepository,
                budgetGoalRepository = budgetGoalRepository
            )
            authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

            // Setup login button
            binding.btnLogin.setOnClickListener {
                login()
            }

            // Setup register link
            binding.tvRegisterLink.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            // Observe login result
            viewLifecycleOwner.lifecycleScope.launch {
                authViewModel.loginUiState.collectLatest { state ->
                    when (state) {
                        is AuthViewModel.LoginUiState.Success -> {
                            sessionManager.saveUserSession(state.user.userId, state.user.username)
                            navigateToHome()
                        }

                        is AuthViewModel.LoginUiState.Error -> {
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT)
                                .show()
                        }

                        is AuthViewModel.LoginUiState.Loading -> {
                            // Optionally show a loading indicator
                        }

                        AuthViewModel.LoginUiState.Initial -> {
                            // Initial state
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("LoginFragment", "Error in onViewCreated: ${e.message}", e)
            Toast.makeText(requireContext(), "Error initializing LoginFragment", Toast.LENGTH_SHORT).show()
        }
    }

    private fun login() {
        try {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return
            }

            authViewModel.login(username, password)
        } catch (e: Exception) {
            Log.e("LoginFragment", "Error in login function: ${e.message}", e)
            Toast.makeText(requireContext(), "Error during login", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToHome() {
        try {
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        } catch (e: Exception) {
            Log.e("LoginFragment", "Error in navigateToHome: ${e.message}", e)
            Toast.makeText(requireContext(), "Navigation error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
