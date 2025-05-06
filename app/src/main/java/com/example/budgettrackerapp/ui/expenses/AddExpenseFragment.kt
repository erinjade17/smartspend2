package com.example.budgettrackerapp.ui.expenses

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.budgettrackerapp.databinding.FragmentAddExpenseBinding
import com.example.budgettrackerapp.utils.DateUtils
import com.example.budgettrackerapp.utils.FileUtils
import com.example.budgettrackerapp.utils.SessionManager
import com.example.budgettrackerapp.viewmodels.ExpenseViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

@AndroidEntryPoint
class AddExpenseFragment : Fragment() {

    private var _binding: FragmentAddExpenseBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExpenseViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private var selectedDate: Long = System.currentTimeMillis()
    private var selectedStartTime: Long? = null
    private var selectedEndTime: Long? = null
    private var photoFile: File? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            takePhoto()
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            photoFile?.let { file ->
                binding.ivExpensePhoto.visibility = View.VISIBLE
                binding.ivExpensePhoto.setImageURI(Uri.fromFile(file))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        setupDatePickers()
        setupTimePickers()
        setupClickListeners()
    }

    private fun setupDatePickers() {
        binding.btnDatePicker.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(selectedDate)
                .build()

            datePicker.addOnPositiveButtonClickListener { selectedDateMillis ->
                selectedDate = selectedDateMillis
                binding.btnDatePicker.text = DateUtils.formatDate(selectedDate)
            }

            datePicker.show(parentFragmentManager, "DATE_PICKER")
        }

        // Set initial date
        binding.btnDatePicker.text = DateUtils.formatDate(selectedDate)
    }

    private fun setupTimePickers() {
        binding.btnStartTimePicker.setOnClickListener {
            showTimePicker(true)
        }

        binding.btnEndTimePicker.setOnClickListener {
            showTimePicker(false)
        }
    }

    private fun showTimePicker(isStartTime: Boolean) {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setTitleText("Select ${if (isStartTime) "start" else "end"} time")
            .build()

        picker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = selectedDate
                set(Calendar.HOUR_OF_DAY, picker.hour)
                set(Calendar.MINUTE, picker.minute)
            }

            if (isStartTime) {
                selectedStartTime = calendar.timeInMillis
                binding.btnStartTimePicker.text = DateUtils.formatTime(calendar.timeInMillis)
            } else {
                selectedEndTime = calendar.timeInMillis
                binding.btnEndTimePicker.text = DateUtils.formatTime(calendar.timeInMillis)
            }
        }

        picker.show(parentFragmentManager, "TIME_PICKER")
    }

    private fun setupClickListeners() {
        binding.btnAddPhoto.setOnClickListener {
            checkCameraPermission()
        }

        binding.btnSaveExpense.setOnClickListener {
            saveExpense()
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                takePhoto()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun takePhoto() {
        lifecycleScope.launch {
            try {
                photoFile = FileUtils.createImageFile(requireContext())
                val photoUri = FileUtils.getUriForFile(requireContext(), photoFile!!)

                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }

                takePictureLauncher.launch(takePictureIntent)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun saveExpense() {
        val amount = binding.etAmount.text.toString().toDoubleOrNull()
        val description = binding.etDescription.text.toString().trim()
        val categoryId = 1L // Get selected category ID from spinner

        if (amount == null || amount <= 0) {
            binding.tilAmount.error = "Enter valid amount"
            return
        }

        if (description.isEmpty()) {
            binding.tilDescription.error = "Enter description"
            return
        }

        viewModel.addExpense(
            amount = amount,
            description = description,
            date = selectedDate,
            startTime = selectedStartTime,
            endTime = selectedEndTime,
            photoPath = photoFile?.absolutePath,
            categoryId = categoryId,
            userId = sessionManager.getUserId()
        )

        viewModel.expenseOperationResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                findNavController().navigateUp()
            }.onFailure {
                // Show error
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}