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
        FaqItem("앱은 업데이트 되나요?", "앞으로 다양한 기능을 업데이트할 예정입니다."),
        FaqItem("앱에 대한 의견을 남기고 싶어요", "skawogus3358@gmail.com로 메일 남겨주세요"),
        FaqItem("일정 동기화는 안 되나요?", "현재X 구글 원격 저장소와의 동기화를 업데이트 할 예정입니다."),
        FaqItem("이 앱을 다른 운영체제에서도 사용하고 싶어요", "현재는 안드로이드 단일 플랫폼만 지원합니다."),

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
