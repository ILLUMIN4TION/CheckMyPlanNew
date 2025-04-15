package com.example.a0401chkmyplan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a0401chkmyplan.databinding.FragmentMemoBinding
import com.example.a0401chkmyplan.databinding.FragmentTaskBinding


class memoFragment : Fragment() {
    private lateinit var binding: FragmentMemoBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMemoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val memoData = ArrayList<MemoDataClass>()
        memoData.add(MemoDataClass("4월","15일","화요일 캡스톤디자인 수업듣기"))
        memoData.add(MemoDataClass("4월","16일","화요일 캡스톤디자인 수업듣기"))
        memoData.add(MemoDataClass("4월","17일","화요일 캡스톤디자인 수업듣기"))
        memoData.add(MemoDataClass("4월","18일","화요일 캡스톤디자인 수업듣기"))
        memoData.add(MemoDataClass("4월","19일","화요일 캡스톤디자인 수업듣기"))
        memoData.add(MemoDataClass("4월","20일","화요일 캡스톤디자인 수업듣기"))
        memoData.add(MemoDataClass("4월","15일","화요일 캡스톤디자인 수업듣기"))
        memoData.add(MemoDataClass("4월","15일","화요일 캡스톤디자인 수업듣기"))
        memoData.add(MemoDataClass("4월","15일","화요일 캡스톤디자인 수업듣기"))
        memoData.add(MemoDataClass("4월","15일","화요일 캡스톤디자인 수업듣기"))
        memoData.add(MemoDataClass("4월","15일","화요일 캡스톤디자인 수업듣기"))

        binding.memoRV.adapter = MemoAdapter(memoData)
        binding.memoRV.layoutManager = LinearLayoutManager(requireContext())



    }
}