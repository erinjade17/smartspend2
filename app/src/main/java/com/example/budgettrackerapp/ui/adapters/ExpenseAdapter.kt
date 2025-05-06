package com.example.budgettrackerapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettrackerapp.data.entities.Expense
import com.example.budgettrackerapp.databinding.ItemExpenseBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpenseAdapter(
    private val categoryMap: Map<Long, String>,
    private val onExpenseClick: (Expense) -> Unit
) : ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder>(ExpenseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = getItem(position)
        holder.bind(expense)
    }

    inner class ExpenseViewHolder(private val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition // Changed to adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onExpenseClick(getItem(position))
                }
            }
        }

        fun bind(expense: Expense) {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) // Corrected date format.
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

            binding.tvExpenseAmount.text = "$${String.format("%.2f", expense.amount)}"
            binding.tvExpenseDescription.text = expense.description
            binding.tvExpenseDate.text = dateFormat.format(Date(expense.date))
            binding.tvExpenseCategory.text = categoryMap[expense.categoryId] ?: "Unknown"

            if (expense.startTime != null && expense.endTime != null) {
                binding.tvExpenseTime.text =
                    "${timeFormat.format(Date(expense.startTime))} - ${timeFormat.format(Date(expense.endTime))}"
                binding.tvExpenseTime.visibility = View.VISIBLE
            } else {
                binding.tvExpenseTime.visibility = View.GONE
            }

            if (expense.photoPath != null) {
                binding.ivExpensePhotoIndicator.visibility = View.VISIBLE
            } else {
                binding.ivExpensePhotoIndicator.visibility = View.GONE
            }
        }
    }

    private class ExpenseDiffCallback : DiffUtil.ItemCallback<Expense>() {
        override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem.expenseId == newItem.expenseId
        }

        override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem == newItem
        }
    }
}
