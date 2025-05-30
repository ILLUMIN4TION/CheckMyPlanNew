    package com.example.a0401chkmyplan


    import android.content.pm.PackageManager
    import android.os.Build
    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.Manifest
    import android.app.AlarmManager
    import android.app.AlertDialog
    import android.content.Context
    import android.content.Intent
    import android.net.Uri
    import android.os.PowerManager
    import android.provider.Settings

    import androidx.activity.enableEdgeToEdge
    import androidx.core.app.ActivityCompat
    import androidx.core.content.ContextCompat
    import androidx.core.view.GravityCompat
    import androidx.drawerlayout.widget.DrawerLayout
    import androidx.fragment.app.Fragment

    import com.example.a0401chkmyplan.databinding.ActivityMainBinding
    import com.google.android.material.bottomnavigation.BottomNavigationView
    import com.google.android.material.navigation.NavigationView


    class MainActivity : AppCompatActivity() {

        //바텀 네비게이션뷰 선언
        private lateinit var bottomNavigationView: BottomNavigationView
        private lateinit var drawerLayout: DrawerLayout //
        private lateinit var navigationView: NavigationView //

        private val binding: ActivityMainBinding by lazy {
            ActivityMainBinding.inflate(layoutInflater)
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            checkBatteryOptimization()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (!alarmManager.canScheduleExactAlarms()) {
                    // 사용자에게 알림 설정 허용 요청 다이얼로그
                    AlertDialog.Builder(this)
                        .setTitle("정확한 알람 권한 필요")
                        .setMessage("정확한 일정 알림을 위해 설정에서 허용해 주세요.")
                        .setPositiveButton("설정으로 이동") { _, _ ->
                            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                            startActivity(intent)
                        }
                        .setNegativeButton("나중에", null)
                        .show()
                }
            }


            enableEdgeToEdge()
            setContentView(binding.root)

            //DrawerLayout과 NavigationView 초기화
            drawerLayout = binding.drawerLayout
            navigationView = binding.navView

            //바텀 네비게이션 뷰를 xml의 네비게이션뷰로 초기화
            bottomNavigationView = binding.bottomNavView

            navigationView.setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.nav_faq -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, FaqFragment())
                            .commit()
                        drawerLayout.closeDrawer(GravityCompat.END)
                        true
                    }
                    else -> false
                }
            }
            //바텀 네비게이션 기본 설정, task(할일) 화면으로 설정
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, TaskFragment())
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

                    else -> TaskFragment()
                }

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit()

                true
            }
//            requestNotificationPermission()
            checkNotificationPermission()

        }
//        private fun requestNotificationPermission() {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
//                    != PackageManager.PERMISSION_GRANTED
//                ) {
//                    ActivityCompat.requestPermissions(
//                        this,
//                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
//                        1001
//                    )
//                }
//            }
//        }

        private fun checkBatteryOptimization() {
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            val packageName = packageName

            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                // 배터리 최적화 예외 요청을 위한 인텐트
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }

        private fun checkNotificationPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // ▶ 권한 요청
                    requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
                }
            }
        }

        // ▶ 권한 요청 결과 처리 함수 추가
        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == 1001) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한 승인됨
                    // (필요하면 추가 행동 가능)
                } else {
                    // 권한 거부됨, 사용자에게 안내 및 설정 이동 유도
                    AlertDialog.Builder(this)
                        .setTitle("알림 권한 필요")
                        .setMessage("알림을 받으려면 권한이 필요합니다. 설정에서 권한을 허용해 주세요.")
                        .setPositiveButton("설정으로 이동") { _, _ ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.data = Uri.parse("package:$packageName")
                            startActivity(intent)
                        }
                        .setNegativeButton("취소", null)
                        .show()
                }
            }
        }

    }

