package com.example.budgettrackerapp.ui.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.budgettrackerapp.databinding.FragmentBudgetGoalBinding
import com.example.budgettrackerapp.utils.SessionManager
import com.example.budgettrackerapp.viewmodels.BudgetGoalViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class BudgetGoalFragment : Fragment() {

    private var _binding: FragmentBudgetGoalBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BudgetGoalViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetGoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        setupMonthYearPicker()
        setupForm()
        loadCurrentBudgetGoal()
    }

    private fun setupMonthYearPicker() {
        binding.btnSelectMonthYear.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select month and year")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .build()

            datePicker.addOnPositiveButtonClickListener { selectedDate ->
                calendar.timeInMillis = selectedDate
                binding.tvSelectedMonthYear.text = dateFormat.format(calendar.time)
                loadBudgetGoalForSelectedMonth()
            }

            datePicker.show(parentFragmentManager, "DATE_PICKER")
        }
    }

    private fun setupForm() {
        binding.btnSaveGoal.setOnClickListener {
            val minGoal = binding.etMinGoal.text.toString().toDoubleOrNull() ?: 0.0
            val maxGoal = binding.etMaxGoal.text.toString().toDoubleOrNull() ?: 0.0

            if (minGoal <= 0 || maxGoal <= 0) {
                binding.tilMinGoal.error = "Enter valid amount"
                binding.tilMaxGoal.error = "Enter valid amount"
                return@setOnClickListener
            }

            if (minGoal > maxGoal) {
                binding.tilMinGoal.error = "Min must be less than max"
                return@setOnClickListener
            }

            viewModel.setBudgetGoal(
                minGoal,
                maxGoal,
                calendar.get(Calendar.MONTH) + 1, // Month is 0-based in Calendar
                calendar.get(Calendar.YEAR),
                sessionManager.getUserId()
            )
        }
    }

    private fun loadCurrentBudgetGoal() {
        // Set current month/year as default
        binding.tvSelectedMonthYear.text = dateFormat.format(calendar.time)
        loadBudgetGoalForSelectedMonth()
    }

    private fun loadBudgetGoalForSelectedMonth() {
        viewModel.getBudgetGoalForMonth(
            sessionManager.getUserId(),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR)
        )

        viewModel.currentBudgetGoal.observe(viewLifecycleOwner) { budgetGoal ->
            budgetGoal?.let {
                binding.etMinGoal.setText(it.minimumGoal.toString())
                binding.etMaxGoal.setText(it.maximumGoal.toString())
            } ?: run {
                binding.etMinGoal.setText("")
                binding.etMaxGoal.setText("")
            }
        }

        viewModel.budgetGoalOperationResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                // Show success message
            }.onFailure {
                // Show error message
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}