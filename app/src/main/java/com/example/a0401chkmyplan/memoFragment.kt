package com.example.a0401chkmyplan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
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

    //update 관련 이슈를 방지하기 위한 코드
    private val editMemoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            loadMemo() // 수정 후 리스트 새로고침
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMemoBinding.inflate(inflater, container, false)

        // 룸 db빌더를 통해 변수에 사용할 데이터베이스를 db변수에 저장
        db = Room.databaseBuilder(
            requireContext(), //프래그먼트에서 사용을 위해 requireContext() 원래는 this
            MemoDatabase::class.java, //우리가 참조할 데이터 베이스 파일 이걸 토대로 만듬
            "memo_appDatabase" //우리가 사용할 db를 memo_appDatabase 이름으로 생성
        ).build()
        dao = MemoDatabase.getDatabase(requireContext()).MemoDao() //싱글톤 dao객체 불러오기 -> 모든 프래그먼트, 액티비티에서 같은 dao를 사용하기 위함




        recyclerView = binding.memoRV
        //우리가 만든 메모어댑터()사용, 클릭 이벤트 포함됨
        adapter = MemoAdapter(mutableListOf()) { selectedMemo ->

            val intent = Intent(requireContext(), MemoDetailActivity::class.java).apply {
                putExtra("id", selectedMemo.id)
                putExtra("title", selectedMemo.title)
                putExtra("desc", selectedMemo.desc)
            }
            editMemoLauncher.launch(intent)  // 여기서 DetailActivity 실행
        }
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