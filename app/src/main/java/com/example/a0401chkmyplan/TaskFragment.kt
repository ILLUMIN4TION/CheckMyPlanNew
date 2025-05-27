package com.example.a0401chkmyplan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.a0401chkmyplan.databinding.FragmentTaskBinding
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

    private val editScheduleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            loadSchedules()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        binding = FragmentTaskBinding.inflate(inflater, container, false)

        db = Room.databaseBuilder(
            requireContext(),
            ScheduleDatabase::class.java,
            "schedule_appDatabase"
        ).build()

        dao = ScheduleDatabase.getDatabase(requireContext()).scheduleDao()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // ✅ 현재 날짜 + 요일 (예: 22목)
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "일"
            Calendar.MONDAY -> "월"
            Calendar.TUESDAY -> "화"
            Calendar.WEDNESDAY -> "수"
            Calendar.THURSDAY -> "목"
            Calendar.FRIDAY -> "금"
            Calendar.SATURDAY -> "토"
            else -> ""
        }
        val dateText = "$day$dayOfWeek"
        binding.WeekandDate.text = dateText

        // ✅ 현재 연도 + 월 (예: 2025.05)
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val yearMonthText = String.format("%d.%02d", year, month)
        binding.Yearmanth.text = yearMonthText


        // 어댑터 2개 초기화
        incompleteAdapter = ScheduleAdapter(mutableListOf(),
            onItemClick = { schedule ->
                // 아이템 클릭 시 동작 (예: 상세 화면 이동)
                val bottomSheet = BottomSheet()
                bottomSheet.arguments = Bundle().apply {
                    putInt("id", schedule.id)
                    putLong("timeMillis", schedule.timeMillis)
                    putString("desc", schedule.desc)
                }
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            },
            onCheckChanged = { schedule ->
                // 체크박스 클릭 시 isComplete 변경 및 DB 업데이트 후 새로고침
                CoroutineScope(Dispatchers.IO).launch {
                    dao.update(schedule.copy(isComplete = !schedule.isComplete))
                    loadSchedules()
                }
            },
            onDeleteClick = { schedule ->
                CoroutineScope(Dispatchers.IO).launch {
                    dao.delete(schedule)
                    loadSchedules()
                }
            }
        )

        completeAdapter = ScheduleAdapter(mutableListOf(),
            onItemClick = { schedule ->
                // 아이템 클릭 시 동작 (예: 상세 화면 이동)
                val bottomSheet = BottomSheet()
                bottomSheet.arguments = Bundle().apply {
                    putInt("id", schedule.id)
                    putLong("timeMillis", schedule.timeMillis)
                    putString("desc", schedule.desc)
                }
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            },
            onCheckChanged = { schedule ->
                // 체크박스 클릭 시 isComplete 변경 및 DB 업데이트 후 새로고침
                CoroutineScope(Dispatchers.IO).launch {
                    dao.update(schedule.copy(isComplete = !schedule.isComplete))
                    loadSchedules()
                }
            },
            onDeleteClick = { schedule ->
                CoroutineScope(Dispatchers.IO).launch {
                    dao.delete(schedule)
                    loadSchedules()
                }
            }
        )

        // RecyclerView 각각 연결
        binding.taskRv.apply {
            adapter = incompleteAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.rvTaskDone.apply {
            adapter = completeAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // 일정 추가 버튼
        binding.taskBtnAdd.setOnClickListener {
            val simpleSet = BottomSheet()
            simpleSet.show(parentFragmentManager, simpleSet.tag)
        }



        parentFragmentManager.setFragmentResultListener("schedule_added", viewLifecycleOwner) { _, _ ->
            loadSchedules()
        }

        loadSchedules()

        parentFragmentManager.setFragmentResultListener("schedule_updated", viewLifecycleOwner) { _, _ ->
            loadSchedules()
        }
    }

    override fun onResume() {
        super.onResume()
        loadSchedules()
    }

    // 체크 상태 변경 로직
    private fun toggleComplete(schedule: ScheduleEntity) {
        val updated = schedule.copy(isComplete = !schedule.isComplete)
        CoroutineScope(Dispatchers.IO).launch {
            dao.update(updated)
            loadSchedules()
        }
    }

    private fun loadSchedules() {
        CoroutineScope(Dispatchers.IO).launch {
            val incomplete = dao.getIncompleteSchedules()
            val complete = dao.getCompleteSchedules()

            withContext(Dispatchers.Main) {
                incompleteAdapter.submitList(incomplete)
                completeAdapter.submitList(complete)
            }
        }
    }

    private fun deleteSchedule(schedule: ScheduleEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.delete(schedule)
            loadSchedules()  // 삭제 후 리스트 다시 불러오기
        }
    }
}
