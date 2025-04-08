package com.example.a0401chkmyplan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.a0401chkmyplan.R
import com.example.a0401chkmyplan.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        val goDetailSet = binding.todayBox1
//        goDetailSet.setOnClickListener{
//            //임시로 상세페이지로 넘어가게 설정, 먼저 간단한 페이지를 보여주고 그 이후 상세페이지로
//            val intent = Intent(this, ActivityDetailSet::class.java)
//            startActivity(intent)
//        }
        val goSimpleSet = binding.todayBox1
        goSimpleSet.setOnClickListener{
            val simpleSet = BottomSheet()
            simpleSet.show(supportFragmentManager, simpleSet.tag)



        }



    }


}
