package com.example.a0401chkmyplan

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.a0401chkmyplan.databinding.ActivityDetailSetBinding
import com.example.a0401chkmyplan.databinding.ActivityMainBinding

class ActivityDetailSet : AppCompatActivity() {
    private val binding: ActivityDetailSetBinding by lazy {
        ActivityDetailSetBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}