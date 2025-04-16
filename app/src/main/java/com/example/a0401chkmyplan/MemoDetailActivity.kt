package com.example.a0401chkmyplan

import android.R
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.a0401chkmyplan.databinding.ActivityMemoDetailBinding


class MemoDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMemoDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val month = intent.getStringExtra("month")
        val day = intent.getStringExtra("day")
        val innerText = intent.getStringExtra("innerText")


        binding.etMemoDetail.setText(innerText)

        binding.tvMemoDetailMonth.text = month
        binding.tvMemoDetailDay.text = day


    }
}