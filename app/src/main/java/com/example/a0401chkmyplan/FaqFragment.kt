package com.example.a0401chkmyplan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a0401chkmyplan.databinding.FragmentFaqBinding

class FaqFragment : Fragment() {
    private val faqList = listOf(
        FaqItem("앱은 무료인가요?", "네, 앱은 모든 기능이 무료로 제공됩니다."),
        FaqItem("데이터는 어디에 저장되나요?", "로컬 기기에 안전하게 저장됩니다."),
        FaqItem("다크모드는 지원하나요?", "아직 구현되지않았습니다."),
        FaqItem("새로운 질문 추가하기", "질문을 추가하려면 여기에 텍스트를 적으세요."),
        FaqItem("또 다른 질문", "답변 내용 작성."),
        FaqItem("새로운 질문 추가하기", "질문을 추가하려면 여기에 텍스트를 적으세요."),
        FaqItem("또 다른 질문", "답변 내용 작성."),
        FaqItem("새로운 질문 추가하기", "질문을 추가하려면 여기에 텍스트를 적으세요."),
        FaqItem("또 다른 질문", "답변 내용 작성."),
        FaqItem("새로운 질문 추가하기", "질문을 추가하려면 여기에 텍스트를 적으세요."),
        FaqItem("또 다른 질문", "답변 내용 작성."),

    )
    private var _binding: FragmentFaqBinding? = null

    private val binding get() = _binding!!

    private lateinit var faqAdapter: FaqAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFaqBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        faqAdapter = FaqAdapter(faqList)

        binding.faqRecyclerView.apply {
            adapter = faqAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
