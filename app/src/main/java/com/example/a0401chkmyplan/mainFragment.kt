package com.example.a0401chkmyplan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.a0401chkmyplan.databinding.FragmentTaskBinding



class mainFragment : Fragment() {
    //프래그먼트에서 바인딩 사용을 위한 선언
    private lateinit var binding: FragmentTaskBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //바인딩 초기화
        binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // todayBox1: 첫 번째 할 일 박스의 뷰를 변수에 저장
        val todayBox1 = binding.todayBox1


        // todayCheck1: 해당 박스 안에 있는 체크박스 뷰를 변수에 저장
        val todayCheck1 = binding.todayCheck1

        // todayList: '오늘 할 일' 리스트(LinearLayout)를 변수에 저장
        val todayList = binding.todayListLayout

        val goSimpleSet = binding.todayBox1

        // doneList: '완료된 일' 리스트(LinearLayout)를 변수에 저장
        val doneList = binding.doneListLayout

        val addBtn = binding.addListButton





        // 체크박스의 체크 상태가 바뀔 때 실행되는 리스너 등록
        todayCheck1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 체크되면(todayBox1을) '오늘 할 일' 리스트에서 제거하고,
                // '완료된 일' 리스트에 추가
                todayList.removeView(todayBox1)
                doneList.addView(todayBox1)
            } else {
                // 체크 해제되면(todayBox1을) '완료된 일' 리스트에서 제거하고,
                // '오늘 할 일' 리스트 맨 위에 다시 추가
                doneList.removeView(todayBox1)
                todayList.addView(todayBox1, 0)  // 0: 리스트 제일 위에 추가
            }
        }

        goSimpleSet.setOnClickListener{
            val simpleSet = BottomSheet()
            simpleSet.show(parentFragmentManager, simpleSet.tag)
        }










    }
}