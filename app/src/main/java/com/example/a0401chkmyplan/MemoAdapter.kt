package com.example.a0401chkmyplan

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.a0401chkmyplan.databinding.FragmentMemoBinding
import com.example.a0401chkmyplan.databinding.MemoRvItemListBinding

class MemoAdapter(
    private val memoData: ArrayList<MemoDataClass>,
    private val itemClickListener: onItemClickListener
) : RecyclerView.Adapter<MemoAdapter.MemoViewHolder>() {

    interface onItemClickListener {
        fun onItemClick(memo: MemoDataClass)
    }

    inner class MemoViewHolder(val binding: MemoRvItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(memo: MemoDataClass) {
            binding.rvMonthTV.text = memo.month
            binding.rvDayTV.text = memo.day
            binding.rvDetailTV.text = memo.innerText

            binding.root.setOnClickListener {
                Log.d("MemoAdapter", "아이템 클릭됨: ${memo.innerText}")
                itemClickListener.onItemClick(memo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val binding =
            MemoRvItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        holder.bind(memoData[position])
    }

    override fun getItemCount(): Int = memoData.size
}