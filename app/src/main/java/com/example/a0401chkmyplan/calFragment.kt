package com.example.a0401chkmyplan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a0401chkmyplan.databinding.FragmentCalendarBinding
import com.example.a0401chkmyplan.scheduleDB.ScheduleDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class calFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ScheduleAdapter
    private lateinit var calendarView: CalendarView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ScheduleAdapter(
            mutableListOf(),
            onItemClick = { /* TODO */ },
            onCheckChanged = { /* TODO */ },
            onDeleteClick = { /* TODO */ }
        )
        binding.calRv.adapter = adapter
        binding.calRv.layoutManager = LinearLayoutManager(requireContext())

        binding.calCalView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val selectedDateMillis = calendar.timeInMillis

            Log.d(
                "CalendarLog",
                "📅 선택된 날짜: $year-${month + 1}-$dayOfMonth (millis: $selectedDateMillis)"
            )

            loadSchedulesForDate(selectedDateMillis)
        }
    }

    private fun loadSchedulesForDate(selectedMillis: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = ScheduleDatabase.getDatabase(requireContext()).scheduleDao()

            val startOfDay = Calendar.getInstance().apply {
                timeInMillis = selectedMillis
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val endOfDay = Calendar.getInstance().apply {
                timeInMillis = selectedMillis
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            Log.d("CalendarLog", "🔍 조회 범위: $startOfDay ~ $endOfDay")

            val list = dao.getSchedulesByDateRange(startOfDay, endOfDay)

            Log.d("CalendarLog", "📋 조회된 일정 개수: ${list.size}")
            list.forEach {
                Log.d("CalendarLog", "📝 일정: id=${it.id}, desc=${it.desc}, time=${it.timeMillis}")
            }

            // ✅ 반복문 밖에서 UI 갱신!
            withContext(Dispatchers.Main) {
                adapter.submitList(list.toMutableList())

                val sdf = SimpleDateFormat("MM월 dd일", Locale.getDefault())
                binding.calTxtView.text = sdf.format(Date(selectedMillis))
            }
        }
    }
}