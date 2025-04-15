package com.example.a0401chkmyplan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.a0401chkmyplan.databinding.MemoRvItemListBinding

class MemoAdapter(val memoData: ArrayList<MemoDataClass>): RecyclerView.Adapter<MemoAdapter.Holder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MemoAdapter.Holder {
        val binding = MemoRvItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: MemoAdapter.Holder, position: Int) {
        holder.month.text = memoData[position].month
        holder.day.text =  memoData[position].day
        holder.detail.text = memoData[position].innerText
    }

    override fun getItemCount(): Int {
        return memoData.size
        //메모데이터의 사이즈만큼 화면에 몇 개가 출력되는지 결정
    }
    //어댑터로 가져올 데이터를 바인딩을 통해 전달, 뷰홀더로 사용하기 위해 뷰 홀더로 상속받고, 생성자로 binding.root
    inner class Holder(val binding: MemoRvItemListBinding) : RecyclerView.ViewHolder(binding.root){
        val day = binding.rvDayTV
        val month = binding.rvMonthTV
        val detail = binding.rvDetailTV
    }

}