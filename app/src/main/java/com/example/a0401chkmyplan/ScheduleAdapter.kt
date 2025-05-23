package com.example.a0401chkmyplan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.a0401chkmyplan.scheduleDB.ScheduleEntity
import java.text.SimpleDateFormat
import java.util.Locale

class ScheduleAdapter(
    private var taskList: MutableList<ScheduleEntity>,
    private val onItemClick: (ScheduleEntity) -> Unit,
    private val onCheckChanged: (ScheduleEntity) -> Unit,
    private val onDeleteClick: (ScheduleEntity) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    inner class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val scheduleDescTextView: TextView = itemView.findViewById(R.id.rv_task_main)
        val scheduleTimeTextView: TextView = itemView.findViewById(R.id.rv_task_dayNtime)
        val scheduleCheckBox: CheckBox = itemView.findViewById(R.id.rv_task_chk1)
        val scheduleDelete: ImageView = itemView.findViewById(R.id.rv_task_delete)

        init {
            // ✅ 전체 항목 클릭: 체크박스/삭제 버튼 아닌 경우만 반응
            itemView.setOnClickListener { v ->
                val task = taskList[adapterPosition]

                // 클릭한 뷰가 체크박스나 삭제 버튼이면 무시
                if (v.id != R.id.rv_task_chk1 && v.id != R.id.rv_task_delete) {
                    onItemClick(task)
                }
            }

            scheduleDelete.setOnClickListener {
                val task = taskList[adapterPosition]
                onDeleteClick(task)
            }

            scheduleCheckBox.setOnClickListener {
                it?.parent?.requestDisallowInterceptTouchEvent(true) // 부모 클릭 막기
                val task = taskList[adapterPosition]
                onCheckChanged(task)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val task = taskList[position]

        holder.scheduleDescTextView.text = task.desc
        holder.scheduleTimeTextView.text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            .format(task.timeMillis)

        holder.scheduleCheckBox.setOnCheckedChangeListener(null)
        holder.scheduleCheckBox.isChecked = task.isComplete
        // → 실제 동작은 ViewHolder init에서 설정한 setOnClickListener가 처리
    }

    override fun getItemCount(): Int = taskList.size

    fun submitList(newList: MutableList<ScheduleEntity>) {
        taskList = newList
        notifyDataSetChanged()
    }
}
