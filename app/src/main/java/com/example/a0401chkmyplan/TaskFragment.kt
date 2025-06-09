// TaskFragment.kt
package com.example.a0401chkmyplan

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.a0401chkmyplan.databinding.FragmentTaskBinding
import com.example.a0401chkmyplan.notification.NotificationHelper
import com.example.a0401chkmyplan.scheduleDB.ScheduleDao
import com.example.a0401chkmyplan.scheduleDB.ScheduleDatabase
import com.example.a0401chkmyplan.scheduleDB.ScheduleEntity
import kotlinx.coroutines.*
import java.util.*

class TaskFragment : Fragment() {

    private lateinit var binding: FragmentTaskBinding
    private lateinit var dao: ScheduleDao
    private lateinit var db: ScheduleDatabase

    private lateinit var incompleteAdapter: ScheduleAdapter
    private lateinit var completeAdapter: ScheduleAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTaskBinding.inflate(inflater, container, false)
        db = Room.databaseBuilder(requireContext(), ScheduleDatabase::class.java, "schedule_appDatabase").build()
        dao = ScheduleDatabase.getDatabase(requireContext()).scheduleDao()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")[calendar.get(Calendar.DAY_OF_WEEK) - 1]
        binding.WeekandDate.text = "$day$dayOfWeek"
        binding.Yearmanth.text = String.format("%d.%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)

        incompleteAdapter = ScheduleAdapter(
            mutableListOf(),
            onItemClick = { schedule -> openBottomSheet(schedule) },
            onCheckChanged = { schedule -> updateScheduleCheck(schedule) },
            onDeleteClick = { schedule ->
                // 삭제 전 확인 다이얼로그
                val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_delete, null)

                AlertDialog.Builder(requireContext())
                    .setView(dialogView)
                    .setPositiveButton("삭제") { _, _ ->
                        deleteSchedule(schedule)  // 실제 삭제 함수 호출
                    }
                    .setNegativeButton("취소", null)
                    .show()
            }
        )

        completeAdapter = ScheduleAdapter(
            mutableListOf(),
            onItemClick = { schedule -> openBottomSheet(schedule) },
            onCheckChanged = { schedule -> updateScheduleCheck(schedule) },
            onDeleteClick = { schedule -> deleteSchedule(schedule) }
        )

        binding.taskRv.adapter = incompleteAdapter
        binding.taskRv.layoutManager = LinearLayoutManager(requireContext())

        binding.rvTaskDone.adapter = completeAdapter
        binding.rvTaskDone.layoutManager = LinearLayoutManager(requireContext())

        binding.taskBtnAdd.setOnClickListener {
            BottomSheet().show(parentFragmentManager, "AddSchedule")
        }

        parentFragmentManager.setFragmentResultListener("schedule_added", viewLifecycleOwner) { _, _ -> loadSchedules() }
        parentFragmentManager.setFragmentResultListener("schedule_updated", viewLifecycleOwner) { _, _ -> loadSchedules() }

        loadSchedules()
    }

    override fun onResume() {
        super.onResume()
        loadSchedules()
    }

    private fun openBottomSheet(schedule: ScheduleEntity) {
        val bottomSheet = BottomSheet().apply {
            arguments = Bundle().apply {
                Log.d("larmTest",schedule.alarmOffsetMinutes.toString())
                putInt("id", schedule.id)
                putLong("timeMillis", schedule.timeMillis)
                putString("desc", schedule.desc)
                putString("alarmType", schedule.alarmType)
                putInt("alarmMinutesBefore", schedule.alarmOffsetMinutes)
                putDouble("latitude", schedule.latitude ?: Double.NaN)        // ✅ 이 두 줄이 중요
                putDouble("longitude", schedule.longitude ?: Double.NaN)
            }
        }
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    private fun updateScheduleCheck(schedule: ScheduleEntity) {
        val updated = schedule.copy(isComplete = !schedule.isComplete)
        CoroutineScope(Dispatchers.IO).launch {
            dao.update(updated)

            withContext(Dispatchers.Main) {
                if (updated.isComplete) {
                    NotificationHelper.cancelAlarm(requireContext(), updated.id.toLong())
                } else {
                    // 알람 재등록하려면 저장된 알람 데이터(시간, 타입 등)를 가져와야 함
                    // 필요 시 DB에 해당 정보 추가 필요
                }
                loadSchedules()
            }
        }
    }

    private fun deleteSchedule(schedule: ScheduleEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.delete(schedule)
            withContext(Dispatchers.Main) {
                NotificationHelper.cancelAlarm(requireContext(), schedule.id.toLong())
                loadSchedules()
            }
        }
    }

    private fun loadSchedules() {
        CoroutineScope(Dispatchers.IO).launch {
            val incomplete = dao.getIncompleteSchedules()
            val complete = dao.getCompleteSchedules()
            val incompleteCount = dao.getIncompleteScheduleCount() // 추가

            withContext(Dispatchers.Main) {
                incompleteAdapter.submitList(incomplete)
                completeAdapter.submitList(complete)
            }
            //일정 갯수 표시
            binding.inCompleteTaskCount.text = "${incompleteCount}개"
        }
    }
}
