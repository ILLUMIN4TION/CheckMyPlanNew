package com.example.a0401chkmyplan

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class TimePickerDialogFragment(
    private val onTimeSelected: (hour: Int, minute: Int) -> Unit
) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // 스타일 적용된 다이얼로그 생성
        return TimePickerDialog(
            requireContext(),
            R.style.TimePickerTheme_Digital, // 커스텀 테마 사용 R.value.style 수정
            this,
            hour,
            minute,
            true
        )
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        onTimeSelected(hourOfDay, minute)
    }
}