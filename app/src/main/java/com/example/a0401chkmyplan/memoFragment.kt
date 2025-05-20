package com.example.a0401chkmyplan

import android.content.Intent
import android.os.Bundle
import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

import com.example.a0401chkmyplan.databinding.FragmentMemoBinding

import com.example.a0401chkmyplan.memoDB.MemoDao
import com.example.a0401chkmyplan.memoDB.MemoDatabase
import com.example.a0401chkmyplan.memoDB.MemoEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.jvm.java


class memoFragment : Fragment() {
    private lateinit var binding: FragmentMemoBinding

    private lateinit var db: MemoDatabase
    private lateinit var  dao: MemoDao
    private lateinit var  recyclerView: RecyclerView
    private lateinit var  adapter: MemoAdapter




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMemoBinding.inflate(inflater, container, false)

        db = Room.databaseBuilder(
            requireContext(), //프래그먼트에서 사용을 위해 requireCOntext()
            MemoDatabase::class.java,
            "memo_appDatabase"
        ).build()
        dao = db.MemoDao()

//        adapter = MemoAdapter(memoList) { selectedMemo ->
//            val intent = Intent(requireContext(), EditMemoActivity::class.java).apply {
//                putExtra("id", selectedMemo.id)
//                putExtra("title", selectedMemo.title)
//                putExtra("desc", selectedMemo.desc)
//            }
//            startActivity(intent)
//        }
//        recyclerView.adapter = adapter


        recyclerView = binding.memoRV
        adapter = MemoAdapter(mutableListOf())
        binding.memoRV.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        CoroutineScope(Dispatchers.IO).launch{
            val memo = dao.getAllMemo()
            withContext(Dispatchers.Main){
                recyclerView.adapter = adapter
            }
        }

        return binding.root



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.memoFloatingBtnAdd.setOnClickListener {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_memo, null)

            val titleEdit = dialogView.findViewById<EditText>(R.id.editTitle)
            val descEdit = dialogView.findViewById<EditText>(R.id.editDesc)

            AlertDialog.Builder(requireContext())
                .setTitle("새 메모 추가")
                .setView(dialogView)
                .setPositiveButton("추가") { _, _ ->
                    val title = titleEdit.text.toString()
                    val desc = descEdit.text.toString()

                    if (title.isNotBlank() && desc.isNotBlank()) {
                        CoroutineScope(Dispatchers.IO).launch {
                            dao.insert(MemoEntity(title = title, desc = desc))

                            // UI 갱신
                            val newList = dao.getAllMemo()
                            withContext(Dispatchers.Main) {
                                (binding.memoRV.adapter as MemoAdapter).updateData(newList)
                            }
                        }
                    }
                }
                .setNegativeButton("취소", null)
                .show()
        }

        binding.memoFloatingBtnDel.setOnClickListener {
            val selectedMemo = adapter.getSelectedItems()
            Log.d("DeleteMemo", "선택된 메모 수: ${selectedMemo.size}")
            if (selectedMemo.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    selectedMemo.forEach { dao.delete(it) }
                    loadMemo()
                }
            }
        }

        loadMemo()

    }
    private fun loadMemo() {
        CoroutineScope(Dispatchers.IO).launch {
            val memoList = dao.getAllMemo()
            withContext(Dispatchers.Main) {
                adapter.updateData(memoList)
            }
        }
    }


}