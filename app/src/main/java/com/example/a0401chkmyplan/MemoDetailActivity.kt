package com.example.a0401chkmyplan

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.a0401chkmyplan.databinding.ActivityMemoDetailBinding
import com.example.a0401chkmyplan.memoDB.MemoDao
import com.example.a0401chkmyplan.memoDB.MemoDatabase
import com.example.a0401chkmyplan.memoDB.MemoEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MemoDetailActivity : AppCompatActivity() {

    private lateinit var dao: MemoDao
    private lateinit var binding: ActivityMemoDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getIntExtra("id", -1)
        val title = intent.getStringExtra("title") ?: ""
        val desc = intent.getStringExtra("desc") ?: ""


        dao = MemoDatabase.getDatabase(this).MemoDao()

        binding.etMemoTitle.setText(title)
        binding.etMemoDesc.setText(desc)

//        binding.tvMemoDetailMonth.text = month
//        binding.tvMemoDetailDay.text = day


        binding.memodetailBtnSave.setOnClickListener {
            val newTitle = binding.etMemoTitle.text.toString()
            val newDesc = binding.etMemoDesc.text.toString()

            if (newTitle.isNotBlank() && newDesc.isNotBlank()) {

                CoroutineScope(Dispatchers.IO).launch {
                    Log.d("memoDetail","현재아이디는 :${id}입니다, 바꿀 타이틀:${newTitle}, 바꿀내용:${newDesc}")
                    dao.update(MemoEntity(id = id, title = newTitle, desc = newDesc))
                    Log.d("MemoUpdate", "Updated Memo: $newTitle / $newDesc (id=$id)")
                    val updatedList = dao.getAllMemo()
                    Log.d("MemoUpdate", "Updated DB List: $updatedList")

                    val intent = Intent()
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }

        }
    }
}