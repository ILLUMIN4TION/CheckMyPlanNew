package com.example.a0401chkmyplan

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.a0401chkmyplan.databinding.FragmentCalendarBinding
import java.util.Calendar

class calFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

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
    // 오늘 날짜를 TextView에 기본으로 표시
    private fun setupTodayDate() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) // 0부터 시작
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val todayText = "${month + 1}월 ${day}일"
        binding.calendarTxtView.text = todayText
    }

    // 캘린더 날짜 클릭 시 TextView 변경
    private fun setupCalendarClickListener() {
        binding.calView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val dateText = "${month + 1}월 ${dayOfMonth}일"
            binding.calendarTxtView.text = dateText
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTodayDate()               // 오늘 날짜 표시
        setupCalendarClickListener()   // 클릭 시 날짜 변경 기능 설정

        binding.calTaskBox1.setOnClickListener {
            val intent = Intent(requireActivity(), ActivityDetailSet::class.java)
            startActivity(intent)
        }
    }
}