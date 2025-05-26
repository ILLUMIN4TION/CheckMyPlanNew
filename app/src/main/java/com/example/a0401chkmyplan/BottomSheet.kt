package com.example.a0401chkmyplan

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import com.example.a0401chkmyplan.databinding.FragmentBottomSheetBinding
import com.example.a0401chkmyplan.scheduleDB.ScheduleDatabase
import com.example.a0401chkmyplan.scheduleDB.ScheduleEntity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale


class BottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!

    // 수정 시 기존 데이터 id 저장 (null이면 새로 작성)
    private var scheduleId: Int? = null
    private var selectedTimeMillis: Long = 0L  // var로 변경, 기본값 0

    private val calendar = Calendar.getInstance()

    private lateinit var selectedAlertType: String
    private var selectedMinutesBefore = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            scheduleId = it.getInt("id", -1).takeIf { id -> id != -1 }
            if (scheduleId != null) {
                // 기존 데이터가 있으면 desc, timeMillis도 받아오기
                val desc = it.getString("desc") ?: ""
                val timeMillis = it.getLong("timeMillis", 0L)
                selectedTimeMillis = timeMillis

                // onViewCreated에서 UI 초기화용으로 저장해두기
                savedDesc = desc
                savedTimeMillis = timeMillis
            }
        }
    }

    private var savedDesc: String? = null
    private var savedTimeMillis: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // 수정 모드면 기존 데이터 UI에 세팅
        if (scheduleId != null) {
            binding.mainBsEt.setText(savedDesc ?: "")
            if (savedTimeMillis != null && savedTimeMillis != 0L) {
                val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    .format(java.util.Date(savedTimeMillis!!))
                binding.mainBsTimeTV.text = dateStr
            }
        }

        binding.bsTimeLayout.setOnClickListener {
            showAlarmSettingsDialog()
        }

        binding.mainBsTimeSet.setOnClickListener {
            showDateTimePicker()
        }

        binding.bsImgOk.setOnClickListener {
            val desc = binding.mainBsEt.text.toString().trim()

            if (desc.isNotBlank()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = ScheduleDatabase.getDatabase(requireContext()).scheduleDao()

                    if (scheduleId != null) {
                        // 수정 모드: 업데이트
                        val updatedSchedule = ScheduleEntity(
                            id = scheduleId!!,
                            desc = desc,
                            timeMillis = selectedTimeMillis,
                            isComplete = false // 필요하면 전달받거나 수정 가능
                        )
                        dao.update(updatedSchedule)
                    } else {
                        // 새로 작성 모드: 삽입
                        val newSchedule = ScheduleEntity(
                            desc = desc,
                            timeMillis = selectedTimeMillis,
                            isComplete = false
                        )
                        dao.insert(newSchedule)
                    }

                    withContext(Dispatchers.Main) {
                        val resultKey = if (scheduleId != null) "schedule_updated" else "schedule_added"
                        parentFragmentManager.setFragmentResult(resultKey, Bundle())
                        dismiss()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDateTimePicker() {
        val now = Calendar.getInstance()

        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                // ✅ 선택된 시간 저장!
                selectedTimeMillis = calendar.timeInMillis

                val selectedTime = calendar.time
                Log.d("BottomSheet", "선택된 시간 저장: $selectedTime ($selectedTimeMillis)")

                binding.mainBsTimeTV.text = selectedTime.toString()

            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()

        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
    }


    //알람 설정을 위한 다이얼로그 세팅
    private fun showAlarmSettingsDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_alarm_settings, null)
        val alertTypeGroup = dialogView.findViewById<RadioGroup>(R.id.alertTypeGroup)
        val etMinutesBefore = dialogView.findViewById<EditText>(R.id.etMinutesBefore)

        AlertDialog.Builder(requireContext())
            .setTitle("알림 설정")
            .setView(dialogView)
            .setPositiveButton("확인") { _, _ ->
                val selectedType = when (alertTypeGroup.checkedRadioButtonId) {
                    R.id.rb_status -> "status"
                    R.id.rb_popup -> "popup"
                    R.id.rb_fullscreen -> "fullscreen"
                    else -> "status" // 기본값
                }

                val minutesBefore = etMinutesBefore.text.toString().toIntOrNull() ?: 30

                // 값 저장 또는 전달
                saveAlarmSettings(selectedType, minutesBefore)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun saveAlarmSettings(alertType: String, minutesBefore: Int) {
        // 나중에 일정 저장 시 ScheduleEntity 또는 알림 예약에 함께 사용
        Log.d("AlarmSettings", "🔔 알림 타입: $alertType, $minutesBefore 분 전")
        selectedAlertType = alertType
        selectedMinutesBefore = minutesBefore
    }
}
