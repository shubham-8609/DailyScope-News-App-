package com.codeleg.dailyscope.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.codeleg.dailyscope.DailyScope
import com.codeleg.dailyscope.R
import com.codeleg.dailyscope.database.network.RetrofitInstance
import com.codeleg.dailyscope.database.repository.NewsRepository
import com.codeleg.dailyscope.databinding.FragmentFilterBinding
import com.codeleg.dailyscope.ui.viewmodel.MainViewModel
import com.codeleg.dailyscope.ui.viewmodel.MainViewModelFactory
import com.codeleg.dailyscope.utils.FilterState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.getValue

class FilterFragment : BottomSheetDialogFragment() {

    private val newsRepo by lazy {
        NewsRepository(
            RetrofitInstance.newsApi,
            (requireActivity().application as DailyScope).newsDao
        )
    }
    private val mainVM: MainViewModel by activityViewModels { MainViewModelFactory(newsRepo) }
    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private lateinit var datePicker: MaterialDatePicker<Long>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        lifecycleScope.launch {

            val todayUtc = MaterialDatePicker.todayInUtcMilliseconds()

            val startCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            startCalendar.timeInMillis = todayUtc
            startCalendar.add(Calendar.MONTH, -1)

            val constraints = CalendarConstraints.Builder()
                .setStart(startCalendar.timeInMillis)
                .setEnd(todayUtc)
                .build()

            datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setCalendarConstraints(constraints)
                .build()

            datePicker.addOnPositiveButtonClickListener { timestamp ->
                binding.etDate.setText(sdf.format(Date(timestamp)))
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            val categories = mainVM.getCategories()
            categories.forEach { category ->
                category?.let {
                val chip = Chip(requireContext()).apply {
                    text = category
                    isCheckable = true
                }
                binding.chipGroupCategory.addView(chip)
                }

            }
        }
        binding.sentimentSlider.setValues(-1f, 1f)
        if(mainVM.isFilterApplied) applyPreviousFilters()
        binding.etDate.setOnClickListener {
            datePicker.show(parentFragmentManager, "datePicker")
        }
        binding.btnApply.setOnClickListener {
            val selectedDateText = binding.etDate.text.toString()
            if (selectedDateText.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val selectedDate = sdf.parse(selectedDateText)
            val todayUtc = MaterialDatePicker.todayInUtcMilliseconds()
            if (selectedDate != null && selectedDate.time > todayUtc) {
                Toast.makeText(
                    requireContext(),
                    "Future dates are not allowed. Selectd date till today.",
                    Toast.LENGTH_SHORT
                ).show()
                binding.etDate.performClick()
                return@setOnClickListener
            }
            val selectedCategory =
                binding.chipGroupCategory.checkedChipId.takeIf { it != View.NO_ID }?.let { id ->
                    (binding.chipGroupCategory.findViewById<Chip>(id)).text.toString()
                }
            if (selectedCategory == null) {
                Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val value = binding.sentimentSlider.values
            val sentimentMin = value[0]
            val sentimentMax = value[1]
            val filterState = FilterState(date = selectedDate?.time, category = selectedCategory, sentimentMin = sentimentMin, sentimentMax = sentimentMax)
            mainVM.applyFilters(filterState)
            Toast.makeText(requireContext() , "Filters applied", Toast.LENGTH_SHORT).show()
            Log.d("codeleg" , "Applied filters: $filterState --filterFragment")
            dismiss()

        }
        binding.btnReset.setOnClickListener {
            binding.etDate.setText("")
            binding.sentimentSlider.setValues(-0.5f, 0.5f)
            for (i in 0 until binding.chipGroupCategory.childCount) {
                val child = binding.chipGroupCategory.getChildAt(i)
                if (child is Chip) {
                    child.isChecked = false
                }
            }
        }
    }

    private fun applyPreviousFilters() {
        binding.tvFilterTitle.text = "Update Filters"
        val filterState = mainVM.filterState.value
        filterState.date?.let {
            binding.etDate.setText(sdf.format(Date(it)))
        }
        filterState.category?.let { category ->
            for (i in 0 until binding.chipGroupCategory.childCount) {
                val child = binding.chipGroupCategory.getChildAt(i)
                if (child is Chip && child.text == category) {
                    child.isChecked = true
                    break
                }
            }
        }
        binding.sentimentSlider.setValues(filterState.sentimentMin, filterState.sentimentMax)
    }

}