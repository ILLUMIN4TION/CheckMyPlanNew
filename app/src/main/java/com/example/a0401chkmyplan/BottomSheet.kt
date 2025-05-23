package com.example.a0401chkmyplan


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.a0401chkmyplan.databinding.FragmentBottomSheetBinding
import com.example.a0401chkmyplan.scheduleDB.ScheduleDatabase
import com.example.a0401chkmyplan.scheduleDB.ScheduleEntity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar


class BottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val selectedTimeMillis = 0L



    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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

        // 시간 설정 버튼 클릭 시 날짜 → 시간 순서로 선택
        binding.mainBsTimeSet.setOnClickListener {
            showDateTimePicker()
        }

        // 저장 버튼 클릭 시 → DB에 일정 저장
        binding.bsImgOk.setOnClickListener{
            val desc = binding.mainBsEt.text.toString().trim()

            if (desc.isNotBlank()) {
                val schedule = ScheduleEntity(
                    desc = desc,
                    timeMillis = selectedTimeMillis
                )

                CoroutineScope(Dispatchers.IO).launch {
                    val dao = ScheduleDatabase.getDatabase(requireContext()).scheduleDao()
                    dao.insert(schedule)
                    dismiss() // 바텀시트 닫기
                }
            } else {
                Toast.makeText(requireContext(), "제목과 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun showDateTimePicker() {
        val now = Calendar.getInstance()

        // 날짜 선택 다이얼로그
        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // 시간 선택 다이얼로그
            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                // 최종 선택된 날짜 및 시간
                val selectedTime = calendar.time
                Log.d("BottomSheet", "선택된 일정 시간: $selectedTime")

                // 이후 DB에 저장하거나 TextView에 표시할 수 있음, 현재는 영어와 미국식 시각표기법,
                binding.mainBsTimeTV.text = selectedTime.toString()

            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()

        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
    }
}