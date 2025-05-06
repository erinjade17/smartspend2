package com.example.budgettrackerapp.ui.expenses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.budgettrackerapp.R
import com.example.budgettrackerapp.databinding.FragmentExpenseDetailBinding
import com.example.budgettrackerapp.utils.DateUtils
import com.example.budgettrackerapp.viewmodels.ExpenseViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ExpenseDetailFragment : Fragment() {

    private var _binding: FragmentExpenseDetailBinding? = null
    private val binding get() = _binding!!
    private val args: ExpenseDetailFragmentArgs by navArgs()
    private val viewModel: ExpenseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        loadExpenseDetails()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    // Navigate to edit screen
                    true
                }
                R.id.action_delete -> {
                    showDeleteConfirmationDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun loadExpenseDetails() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            val result = viewModel.getExpenseById(args.expenseId)
            result.onSuccess { expense ->
                binding.tvAmount.text = getString(R.string.price_format, expense.amount)
                binding.tvDescription.text = expense.description
                binding.tvDate.text = DateUtils.formatDate(expense.date)

                expense.startTime?.let { startTime ->
                    binding.tvTime.text = getString(
                        R.string.time_range_format,
                        DateUtils.formatTime(startTime),
                        expense.endTime?.let { DateUtils.formatTime(it) } ?: "N/A"
                    )
                } ?: run {
                    binding.tvTime.text = "No time specified"
                }

                expense.photoPath?.let { photoPath ->
                    binding.ivExpensePhoto.visibility = View.VISIBLE
                    Glide.with(this@ExpenseDetailFragment)
                        .load(File(photoPath))
                        .into(binding.ivExpensePhoto)
                } ?: run {
                    binding.ivExpensePhoto.visibility = View.GONE
                }
            }.onFailure {
                // Handle error
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Expense")
            .setMessage("Are you sure you want to delete this expense?")
            .setPositiveButton("Delete") { dialog, _ ->
                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    val result = viewModel.getExpenseById(args.expenseId)
                    result.onSuccess { expense ->
                        viewModel.deleteExpense(expense)
                        findNavController().navigateUp()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}