package com.example.a0401chkmyplan


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.activity.enableEdgeToEdge

import androidx.fragment.app.Fragment

import com.example.a0401chkmyplan.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    //바텀 네비게이션뷰 선언
    private lateinit var bottomNavigationView: BottomNavigationView


    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        //바텀 네비게이션 뷰를 xml의 네비게이션뷰로 초기화
        bottomNavigationView = binding.bottomNavView


        //바텀 네비게이션 기본 설정, task(할일) 화면으로 설정
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mainFragment())
                .commit()
            bottomNavigationView.selectedItemId= R.id.fragment_task
        }

        //바텀 네비게이션뷰의 선택된 값이 바뀔 경우 해당 프래그먼트로 전환
        bottomNavigationView.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment = when (item.itemId) {
                R.id.fragment_memo -> memoFragment()
                R.id.fragment_cal -> calFragment()
                R.id.fragment_menu -> menuFragment()

                else -> mainFragment()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commit()

            true
        }
    }

}

