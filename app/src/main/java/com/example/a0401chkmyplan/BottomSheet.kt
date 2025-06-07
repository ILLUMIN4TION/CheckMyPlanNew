package com.example.a0401chkmyplan

import android.app.*
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import com.example.a0401chkmyplan.LocationSetActivity.Companion.AUTOCOMPLETE_REQUEST_CODE
import com.example.a0401chkmyplan.databinding.FragmentBottomSheetBinding
import com.example.a0401chkmyplan.notification.scheduleAlarm
import com.example.a0401chkmyplan.scheduleDB.ScheduleDatabase
import com.example.a0401chkmyplan.scheduleDB.ScheduleEntity
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class BottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val calendar = Calendar.getInstance()
    private var selectedTimeMillis: Long = 0L

    private var scheduleId: Int? = null
    private var isAlarmConfigure = false
    private var alarmType: String = "status"
    private var alarmMinutesBefore: Int = 30

    private var selectedLatitude: Double? = null
    private var selectedLongitude: Double? = null

    private var savedLatitude: Double? = null
    private var savedLongitude: Double? = null
    private var savedDesc: String? = null
    private var savedTimeMillis: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            scheduleId = it.getInt("id", -1).takeIf { id -> id != -1 }
            savedDesc = it.getString("desc")
            savedTimeMillis = it.getLong("timeMillis", 0L).takeIf { it != 0L }
            selectedTimeMillis = savedTimeMillis ?: 0L
            savedLatitude = it.getDouble("latitude", Double.NaN).takeIf { !it.isNaN() }
            savedLongitude = it.getDouble("longitude", Double.NaN).takeIf { !it.isNaN() }
            alarmType = it.getString("alarmType") ?: "status"
            alarmMinutesBefore = it.getInt("alarmMinutesBefore", 30)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        restoreSavedData()
        setupViews()
    }

    private fun restoreSavedData() {
        binding.mainBsEt.setText(savedDesc ?: "")

        savedTimeMillis?.let {
            val formatted = formatTime(it)
            binding.mainBsTimeTV.text = formatted
        }

        savedLatitude?.let { lat ->
            savedLongitude?.let { lng ->
                selectedLatitude = lat
                selectedLongitude = lng
                binding.mainBsLocTV.text = "위치: ${getAddressFromLocation(lat, lng)}"
            }
        }

        binding.mainBsAlarmTV.text = "${alarmMinutesBefore}분 전, 유형: $alarmType"
    }

    private fun setupViews() {
        binding.mainBsTimeSet.setOnClickListener { showAlarmSettingsDialog() }
        binding.mainBsTimeSet.setOnClickListener { showDateTimePicker() }
        binding.mainBsLocSet.setOnClickListener { launchLocationSetActivity() }
        binding.bsImgOk.setOnClickListener { saveEvent() }
        binding.bsImgCancel.setOnClickListener { dismiss() }
    }

    private fun showDateTimePicker() {
        val now = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, y, m, d ->
            calendar.set(Calendar.YEAR, y)
            calendar.set(Calendar.MONTH, m)
            calendar.set(Calendar.DAY_OF_MONTH, d)

            TimePickerDialog(requireContext(), { _, h, min ->
                calendar.set(Calendar.HOUR_OF_DAY, h)
                calendar.set(Calendar.MINUTE, min)
                selectedTimeMillis = calendar.timeInMillis
                binding.mainBsTimeTV.text = formatTime(calendar.timeInMillis)
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()

        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showAlarmSettingsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_alarm_settings, null)
        val alertTypeGroup = dialogView.findViewById<RadioGroup>(R.id.alertTypeGroup)
        val etMinutesBefore = dialogView.findViewById<EditText>(R.id.etMinutesBefore)

        AlertDialog.Builder(requireContext())
            .setTitle("알림 설정")
            .setView(dialogView)
            .setPositiveButton("확인") { _, _ ->
                val selectedType = when (alertTypeGroup.checkedRadioButtonId) {
                    R.id.rb_popup -> "popup"
                    R.id.rb_fullscreen -> "fullscreen"
                    else -> "status"
                }
                alarmType = selectedType
                alarmMinutesBefore = etMinutesBefore.text.toString().toIntOrNull() ?: 30
                isAlarmConfigure = true
                binding.mainBsAlarmTV.text = "${alarmMinutesBefore}분 전, 유형: $alarmType"
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun launchLocationSetActivity() {
        // 👉 프로그레스바 표시
        binding.progressBar.visibility = View.VISIBLE

        val intent = Intent(context, LocationSetActivity::class.java)
        selectedLatitude?.let { lat -> selectedLongitude?.let { lng ->
            intent.putExtra("latitude", lat)
            intent.putExtra("longitude", lng)
        }}
        startActivityForResult(intent, LOCATION_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val progressBar = binding.progressBar
        progressBar.visibility = View.VISIBLE

        when {
            requestCode == LOCATION_REQUEST_CODE && resultCode == Activity.RESULT_OK -> {
                selectedLatitude = data?.getDoubleExtra("latitude", 0.0)
                selectedLongitude = data?.getDoubleExtra("longitude", 0.0)
                selectedLatitude?.let { lat ->
                    selectedLongitude?.let { lng ->
                        binding.mainBsLocTV.text = "위치: ${getAddressFromLocation(lat, lng)}"
                    }
                }
            }

            requestCode == AUTOCOMPLETE_REQUEST_CODE && resultCode == Activity.RESULT_OK -> {
                Autocomplete.getPlaceFromIntent(data!!).let { place ->
                    place.latLng?.let {
                        selectedLatitude = it.latitude
                        selectedLongitude = it.longitude
                        binding.mainBsLocTV.text = "위치: ${place.address ?: getAddressFromLocation(it.latitude, it.longitude)}"
                    }
                }
            }
        }

        progressBar.visibility = View.GONE
    }

    private fun getAddressFromLocation(lat: Double, lng: Double): String {
        return try {
            val address = Geocoder(requireContext(), Locale.getDefault())
                .getFromLocation(lat, lng, 1)
                ?.firstOrNull()?.getAddressLine(0)
            address ?: "주소 정보 없음"
        } catch (e: Exception) {
            "주소 변환 실패"
        }
    }

    private fun saveEvent() {
        val desc = binding.mainBsEt.text.toString().trim()
        if (desc.isEmpty()) {
            Toast.makeText(requireContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val dao = ScheduleDatabase.getDatabase(requireContext()).scheduleDao()
            val time = if (selectedTimeMillis > 0L) selectedTimeMillis else System.currentTimeMillis()

            val entity = ScheduleEntity(
                id = scheduleId ?: 0,
                desc = desc,
                timeMillis = time,
                isComplete = false,
                latitude = selectedLatitude,
                longitude = selectedLongitude,
                alarmType = alarmType,
                alarmOffsetMinutes = alarmMinutesBefore
            )

            if (scheduleId != null) dao.update(entity) else dao.insert(entity).also {
                entity.id = it.toInt()
            }

            if (isAlarmConfigure) {
                scheduleAlarm(requireContext(), entity, alarmType, alarmMinutesBefore)
            }

            withContext(Dispatchers.Main) {
                parentFragmentManager.setFragmentResult(
                    if (scheduleId != null) "schedule_updated" else "schedule_added",
                    bundleOf()
                )
                onScheduleSavedListener?.onScheduleSaved()
                dismiss()
            }
        }
    }

    private fun formatTime(timeMillis: Long): String {
        return SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분", Locale.KOREA).format(Date(timeMillis))
    }

    companion object {
        private const val LOCATION_REQUEST_CODE = 1001

        fun newInstance(schedule: ScheduleEntity): BottomSheet {
            return BottomSheet().apply {
                arguments = Bundle().apply {
                    putInt("id", schedule.id)
                    putString("desc", schedule.desc)
                    putLong("timeMillis", schedule.timeMillis)
                    schedule.latitude?.let { putDouble("latitude", it) }
                    schedule.longitude?.let { putDouble("longitude", it) }
                    schedule.alarmType?.let { putString("alarmType", it) }
                    putInt("alarmMinutesBefore", schedule.alarmOffsetMinutes)
                }
            }
        }
    }

    interface OnScheduleSavedListener {
        fun onScheduleSaved()
    }

    var onScheduleSavedListener: OnScheduleSavedListener? = null


}
