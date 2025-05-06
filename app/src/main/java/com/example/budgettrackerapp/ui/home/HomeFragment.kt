package com.example.budgettrackerapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.budgettrackerapp.R
import com.example.budgettrackerapp.databinding.FragmentHomeBinding
import com.example.budgettrackerapp.utils.SessionManager

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        binding.tvWelcome.text = "Welcome, ${sessionManager.getUsername()}"

        setupClickListeners()
        loadSummaryData()
    }

    private fun setupClickListeners() {
        binding.cardAddExpense.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addExpenseFragment)
        }

        binding.cardCategories.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_categoriesFragment)
        }

        binding.cardTransactions.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_transactionsFragment)
        }

        binding.cardBudgetGoal.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_budgetGoalFragment)
        }

        binding.cardDashboard.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_dashboardFragment)
        }

        binding.cardLogout.setOnClickListener {
            sessionManager.clearSession()
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }
    }

    private fun loadSummaryData() {
        // Load current month's summary
        // Update progress bar and text views
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
