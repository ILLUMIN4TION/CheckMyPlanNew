package com.example.a0401chkmyplan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.a0401chkmyplan.databinding.ItemScheduleBinding
import com.example.a0401chkmyplan.scheduleDB.ScheduleEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScheduleAdapter : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    private var schedules: List<ScheduleEntity> = emptyList()

    fun submitList(newList: List<ScheduleEntity>) {
        schedules = newList
        notifyDataSetChanged()
    }

    class ScheduleViewHolder(val binding: ItemScheduleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ItemScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = schedules[position]
        holder.binding.rvTaskMain.text = schedule.desc
        //테이블의 값을 바인딩을통해 각 아이템에 연결
        holder.binding.rvTaskDayNtime.text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(schedule.timeMillis))
    }

    override fun getItemCount() = schedules.size
}