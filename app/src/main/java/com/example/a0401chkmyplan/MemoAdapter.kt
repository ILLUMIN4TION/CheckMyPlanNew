package com.example.a0401chkmyplan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.a0401chkmyplan.memoDB.MemoEntity


class MemoAdapter(
    private var memoList: MutableList<MemoEntity>,
    private val onItemClick: (MemoEntity) -> Unit  // 클릭 리스너 필요 없으면 생략 가능
) : RecyclerView.Adapter<MemoAdapter.MemoViewHolder>() {

    private val selectedItems = mutableSetOf<MemoEntity>()

    fun getSelectedItems(): List<MemoEntity> = selectedItems.toList()

    // 뷰홀더 정의
    inner class MemoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.rv_memo_title)
        val descTextView: TextView = itemView.findViewById(R.id.rv_memo_desc)
        val deleteChkBox : CheckBox = itemView.findViewById(R.id.rv_memo_chkbox)

        init {
            itemView.setOnClickListener {
                val memo = memoList[adapterPosition]
                onItemClick(memo)  // 여기서 클릭 시 프래그먼트 쪽 콜백 호출
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.memo_rv_item_list, parent, false)  // XML 레이아웃 이름에 맞게 수정
        return MemoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val memo = memoList[position]
        holder.titleTextView.text = memo.title
        holder.descTextView.text = memo.desc

        holder.deleteChkBox.setOnCheckedChangeListener(null)
        holder.deleteChkBox.isChecked = selectedItems.contains(memo)


        holder.deleteChkBox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                selectedItems.add(memo)
            }else{
                selectedItems.remove(memo)
            }

        }
    }

    override fun getItemCount(): Int = memoList.size

    // 🔁 외부에서 리스트를 갱신할 수 있도록 해주는 함수
    fun updateData(newList: MutableList<MemoEntity>) {
        memoList = newList
        selectedItems.clear()
        notifyDataSetChanged()
    }
}
