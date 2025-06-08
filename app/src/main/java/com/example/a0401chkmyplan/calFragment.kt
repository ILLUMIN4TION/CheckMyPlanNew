package com.example.a0401chkmyplan

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a0401chkmyplan.databinding.FragmentCalendarBinding
import com.example.a0401chkmyplan.notification.NotificationHelper
import com.example.a0401chkmyplan.scheduleDB.ScheduleDao
import com.example.a0401chkmyplan.scheduleDB.ScheduleDatabase
import com.example.a0401chkmyplan.scheduleDB.ScheduleEntity
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class calFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ScheduleAdapter
    private var selectedDate: Long = System.currentTimeMillis()
    private lateinit var dao: ScheduleDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        dao = ScheduleDatabase.getDatabase(requireContext()).scheduleDao() // ✅ 초기화
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
            onItemClick = { schedule -> openBottomSheet(schedule) },
            onCheckChanged = { schedule -> updateScheduleCheck(schedule) },  // ✅ 체크 처리
            onDeleteClick = { schedule ->
                AlertDialog.Builder(requireContext())
                    .setTitle("일정 삭제")
                    .setMessage("정말 이 일정을 삭제하시겠습니까?")
                    .setPositiveButton("예") { _, _ ->
                        CoroutineScope(Dispatchers.IO).launch {
                            dao.delete(schedule)
                            withContext(Dispatchers.Main) {
                                loadSchedulesForDate(selectedDate)
                            }
                        }
                    }
                    .setNegativeButton("아니오", null)
                    .show()
            }
        )

        binding.calRv.adapter = adapter
        binding.calRv.layoutManager = LinearLayoutManager(requireContext())

        binding.calCalView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            selectedDate = calendar.timeInMillis
            Log.d("CalendarLog", "📅 선택된 날짜: $year-${month + 1}-$dayOfMonth (millis: $selectedDate)")
            loadSchedulesForDate(selectedDate)
        }

        binding.calendarAddFloatingBtn.setOnClickListener {
            val bottomSheet = BottomSheet().apply {
                arguments = Bundle().apply {
                    putLong("timeMillis", selectedDate)
                }
                onScheduleSavedListener = object : BottomSheet.OnScheduleSavedListener {
                    override fun onScheduleSaved() {
                        loadSchedulesForDate(selectedDate)
                    }
                }
            }
            bottomSheet.show(parentFragmentManager, "AddSchedule")
        }

        loadSchedulesForDate(selectedDate)
    }

    private fun openBottomSheet(schedule: ScheduleEntity) {
        val bottomSheet = BottomSheet().apply {
            arguments = Bundle().apply {
                putInt("id", schedule.id)
                putLong("timeMillis", schedule.timeMillis)
                putString("desc", schedule.desc)
                putString("alarmType", schedule.alarmType)
                putInt("alarmOffsetMinutes", schedule.alarmOffsetMinutes)
                putDouble("latitude", schedule.latitude ?: Double.NaN)
                putDouble("longitude", schedule.longitude ?: Double.NaN)
            }
            onScheduleSavedListener = object : BottomSheet.OnScheduleSavedListener {
                override fun onScheduleSaved() {
                    loadSchedulesForDate(selectedDate)
                }
            }
        }
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    private fun loadSchedulesForDate(selectedMillis: Long) {
        CoroutineScope(Dispatchers.IO).launch {
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

            val list = dao.getSchedulesByDateRange(startOfDay, endOfDay)

            withContext(Dispatchers.Main) {
                adapter.submitList(list.toMutableList())
                val sdf = SimpleDateFormat("MM월 dd일", Locale.getDefault())
                binding.calTxtView.text = sdf.format(Date(selectedMillis))
            }
        }
    }

    private fun updateScheduleCheck(schedule: ScheduleEntity) {
        val updated = schedule.copy(isComplete = !schedule.isComplete)
        CoroutineScope(Dispatchers.IO).launch {
            dao.update(updated)

            withContext(Dispatchers.Main) {
                if (updated.isComplete) {
                    NotificationHelper.cancelAlarm(requireContext(), updated.id.toLong())
                } else {
                    // 필요 시 알람 재등록
                }
                loadSchedulesForDate(selectedDate)  // ✅ 바로 리스트 갱신
            }
        }
    }
}
