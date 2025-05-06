package com.example.budgettrackerapp.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgettrackerapp.R
import com.example.budgettrackerapp.data.entities.Category
import com.example.budgettrackerapp.databinding.FragmentCategoriesBinding
import com.example.budgettrackerapp.ui.adapters.CategoryAdapter
import com.example.budgettrackerapp.utils.SessionManager
import com.example.budgettrackerapp.viewmodels.CategoryViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var categoryAdapter: CategoryAdapter
    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()

        setupRecyclerView()
        setupObservers(userId)
        setupClickListeners()

        viewModel.getUserCategories(userId)
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter(
            onCategoryClick = { category ->
                // Handle category click if needed
            },
            onEditClick = { category ->
                showEditCategoryDialog(category)
            },
            onDeleteClick = { category ->
                showDeleteConfirmationDialog(category)
            }
        )

        binding.rvCategories.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun setupObservers(userId: Long) {
        viewModel.getUserCategories(userId).observe(viewLifecycleOwner) { categories ->
            categoryAdapter.submitList(categories)
            binding.tvEmptyState.visibility = if (categories.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.categoryOperationResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                // Refresh categories
                viewModel.getUserCategories(userId)
            }.onFailure { exception ->
                showError(exception.message ?: "An error occurred")
            }
        }
    }

    private fun setupClickListeners() {
        binding.fabAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    private fun showAddCategoryDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_category, null)
        val input = dialogView.findViewById<TextInputEditText>(R.id.et_category_name)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add New Category")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val name = input?.text.toString().trim()
                if (name.isNotEmpty()) {
                    viewModel.addCategory(name, sessionManager.getUserId())
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showEditCategoryDialog(category: Category) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_category, null)
        val input = dialogView.findViewById<TextInputEditText>(R.id.et_category_name)
        input?.setText(category.name)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Category")
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                val updatedName = input?.text.toString().trim()
                if (updatedName.isNotEmpty()) {
                    val updatedCategory = category.copy(name = updatedName)
                    viewModel.updateCategory(updatedCategory)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showDeleteConfirmationDialog(category: Category) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Category")
            .setMessage("Are you sure you want to delete this category?")
            .setPositiveButton("Delete") { dialog, _ ->
                viewModel.deleteCategory(category)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showError(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

