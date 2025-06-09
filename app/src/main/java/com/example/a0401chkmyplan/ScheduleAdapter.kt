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
        val desc: TextView = itemView.findViewById(R.id.rv_task_main)
        val time: TextView = itemView.findViewById(R.id.rv_task_dayNtime)
        val checkBox: CheckBox = itemView.findViewById(R.id.rv_task_chk1)
        val deleteBtn: ImageView = itemView.findViewById(R.id.rv_task_delete)

        init {
            itemView.setOnClickListener {
                val task = taskList[adapterPosition]
                if (it.id != R.id.rv_task_chk1 && it.id != R.id.rv_task_delete) {
                    onItemClick(task)
                }
            }

            deleteBtn.setOnClickListener {
                val task = taskList[adapterPosition]
                onDeleteClick(task)
            }

            checkBox.setOnClickListener {
                it.parent?.requestDisallowInterceptTouchEvent(true)
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
        holder.desc.text = task.desc
        holder.time.text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(task.timeMillis)
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = task.isComplete
    }

    override fun getItemCount(): Int = taskList.size

    fun submitList(newList: MutableList<ScheduleEntity>) {
        taskList = newList
        notifyDataSetChanged()
    }
}
