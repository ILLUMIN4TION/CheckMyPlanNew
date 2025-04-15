package com.example.a0401chkmyplan


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.activity.enableEdgeToEdge
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment

import com.example.a0401chkmyplan.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {

    //바텀 네비게이션뷰 선언
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var drawerLayout: DrawerLayout // ✅ DrawerLayout 선언
    private lateinit var navigationView: NavigationView // ✅ NavigationView 선언

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        //DrawerLayout과 NavigationView 초기화
        drawerLayout = binding.drawerLayout
        navigationView = binding.navView

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
                R.id.fragment_menu -> {
                    drawerLayout.openDrawer(GravityCompat.END)
                    return@setOnItemSelectedListener true
                }

                else -> mainFragment()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commit()

            true
        }
    }

}

