package com.example.budgettrackerapp.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.budgettrackerapp.R
import com.example.budgettrackerapp.databinding.FragmentDashboardBinding
import com.example.budgettrackerapp.utils.SessionManager
import com.example.budgettrackerapp.viewmodels.ExpenseViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExpenseViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        setupCharts()
        loadData()
    }

    private fun setupCharts() {
        // Setup bar chart
        with(binding.barChart) {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            isHighlightFullBarEnabled = false

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f
            xAxis.labelCount = 7

            axisLeft.setDrawGridLines(false)
            axisRight.isEnabled = false

            legend.isEnabled = false
            animateY(1000)
        }
    }

    private fun loadData() {
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.timeInMillis

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.calculateTotalAmountByCategories(
                sessionManager.getUserId(),
                listOf(1, 2, 3, 4), // Example category IDs
                startDate,
                endDate
            )
        }

        viewModel.categoryTotalAmount.observe(viewLifecycleOwner) { totalsMap ->
            val entries = ArrayList<BarEntry>()
            val labels = ArrayList<String>()

            var index = 0f
            totalsMap.forEach { (categoryId, total) ->
                entries.add(BarEntry(index++, total.toFloat()))
                labels.add("Category $categoryId") // Replace with actual category names
            }

            val dataSet = BarDataSet(entries, "Expenses by Category")
            dataSet.color = requireContext().getColor(R.color.colorPrimary) // Use requireContext()
            val data = BarData(dataSet)
            data.barWidth = 0.5f

            binding.barChart.data = data
            binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            binding.barChart.invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
