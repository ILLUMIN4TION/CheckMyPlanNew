package com.example.a0401chkmyplan

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat
import com.example.a0401chkmyplan.databinding.ActivityLocationSetBinding
import com.example.a0401chkmyplan.databinding.FragmentBottomSheetBinding
import com.example.a0401chkmyplan.notification.scheduleAlarm
import com.example.a0401chkmyplan.scheduleDB.ScheduleDatabase
import com.example.a0401chkmyplan.scheduleDB.ScheduleEntity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.collections.isNotEmpty


class BottomSheet : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var scheduleId: Int? = null
    private var selectedTimeMillis: Long = 0L
    private val calendar = Calendar.getInstance()

    private lateinit var selectedAlertType: String
    private var selectedMinutesBefore = 0

    // ✅ 알람 관련 값 기본값 설정
    private var alarmType: String = "status"
    private var alarmMinutesBefore: Int = 30

    private val LOCATION_REQUEST_CODE = 1001
    private var selectedLatitude: Double? = null
    private var selectedLongitude: Double? = null
    private var savedLatitude: Double? = null
    private var savedLongitude: Double? = null

    // ✅ 수정 시 기존 데이터 저장
    private var savedDesc: String? = null
    private var savedTimeMillis: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            scheduleId = it.getInt("id", -1).takeIf { id -> id != -1 }
            if (scheduleId != null) {
                savedDesc = it.getString("desc")
                savedTimeMillis = it.getLong("timeMillis", 0L)
                selectedTimeMillis = savedTimeMillis!!

                savedLatitude = it.getDouble("latitude", Double.NaN).takeIf { !it.isNaN() }
                savedLongitude = it.getDouble("longitude", Double.NaN).takeIf { !it.isNaN() }

                // ✅ 알람 정보도 arguments에서 받아오기
                alarmType = it.getString("alarmType") ?: "status"
                alarmMinutesBefore = it.getInt("alarmMinutesBefore", 30)
            }
            alarmType = it.getString("alarmType", "status")  // 기본값 status
            alarmMinutesBefore = it.getInt("alarmOffsetMinutes", 30)
        }

    }

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

        // ✅ 기존 데이터 UI에 반영
        if (scheduleId != null) {
            binding.mainBsEt.setText(savedDesc ?: "")
            if (savedTimeMillis != null && savedTimeMillis != 0L) {
                val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    .format(Date(savedTimeMillis!!))
                binding.mainBsTimeTV.text = dateStr
            }

            if (savedLatitude != null && savedLongitude != null) {
                val addressStr = getAddressFromLocation(savedLatitude!!, savedLongitude!!)
                binding.mainBsLocTV.text = "위치: $addressStr"
                selectedLatitude = savedLatitude
                selectedLongitude = savedLongitude
            }

            // ✅ 알림 텍스트뷰에도 기존 값 반영
            binding.mainBsAlarmTV.text = "${alarmMinutesBefore}분 전, 유형: $alarmType"

        }


        binding.bsTimeLayout.setOnClickListener {
            showAlarmSettingsDialog()
        }

        binding.mainBsTimeSet.setOnClickListener {
            showDateTimePicker()
        }

        binding.mainBsLocSet.setOnClickListener {
            val intent = Intent(requireContext(), LocationSetActivity::class.java)
            startActivityForResult(intent, LOCATION_REQUEST_CODE)
        }

        binding.bsImgOk.setOnClickListener {
            val desc = binding.mainBsEt.text.toString().trim()
            if (desc.isNotBlank()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = ScheduleDatabase.getDatabase(requireContext()).scheduleDao()

                    if (scheduleId != null) {
                        val updated = ScheduleEntity(
                            id = scheduleId!!,
                            desc = desc,
                            timeMillis = selectedTimeMillis,
                            isComplete = false,
                            latitude = selectedLatitude,
                            longitude = selectedLongitude,
                            alarmType = alarmType,                          // ✅ 알림 정보 포함
                            alarmOffsetMinutes = alarmMinutesBefore

                        )
                        dao.update(updated)
                        scheduleAlarm(requireContext(), updated, alarmType, alarmMinutesBefore)
                    } else {
                        val newSchedule = ScheduleEntity(
                            desc = desc,
                            timeMillis = selectedTimeMillis,
                            isComplete = false,
                            latitude = selectedLatitude,
                            longitude = selectedLongitude,
                            alarmType = alarmType,                          // ✅ 알림 정보 포함
                            alarmOffsetMinutes = alarmMinutesBefore
                        )
                        val newId = dao.insert(newSchedule)
                        val full = newSchedule.copy(id = newId.toInt())
                        scheduleAlarm(requireContext(), full, alarmType, alarmMinutesBefore)
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

                selectedTimeMillis = calendar.timeInMillis
                binding.mainBsTimeTV.text = calendar.time.toString()

            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()

        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
    }

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
                    else -> "status"
                }
                val minutesBefore = etMinutesBefore.text.toString().toIntOrNull() ?: 30
                alarmType = selectedType
                alarmMinutesBefore = minutesBefore
                binding.mainBsAlarmTV.text = "${minutesBefore}분 전, 유형: $selectedType"
            }
            .setNegativeButton("취소", null)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_REQUEST_CODE && resultCode == RESULT_OK) {
            selectedLatitude = data?.getDoubleExtra("latitude", 0.0)
            selectedLongitude = data?.getDoubleExtra("longitude", 0.0)

            if (selectedLatitude != null && selectedLongitude != null) {
                val addressStr = getAddressFromLocation(selectedLatitude!!, selectedLongitude!!)
                binding.mainBsLocTV.text = "위치: $addressStr"
            }
        }
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                address.getAddressLine(0) ?: "주소 정보 없음"
            } else {
                "주소를 찾을 수 없음"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "주소 변환 실패"
        }
    }
}
