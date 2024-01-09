package com.example.xpos.ui.sqlSearching

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.xpos.R
import com.example.xpos.databinding.FragmentSlideshowBinding
import com.example.xpos.databinding.FragmentSqlSearchingBinding
import com.example.xpos.ui.dataBaseManager.UserDataBaseHelper

class SqlSearchingFragment : Fragment() {

    private var _binding: FragmentSqlSearchingBinding? = null

    private val binding get() = _binding!!
    private lateinit var dbrw: SQLiteDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSqlSearchingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 初始化 ViewModel
        val sqlSearchingViewModel =
            ViewModelProvider(this).get(SqlSearchingViewModel::class.java)

        // 初始化資料庫
        val dbHelper = UserDataBaseHelper(requireContext())
        dbrw = dbHelper.writableDatabase

        //標題
        val textView: TextView = binding.textSqlSearchShow
        sqlSearchingViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        //由下拉是選單顯示全部的table種類
        val tablesArray = resources.getStringArray(R.array.database_type) //全部的table種類
        val spinerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,tablesArray)
        binding.sqlDbSpinner.adapter = spinerAdapter

        //下拉是選單選擇資料庫
        binding.sqlDbSpinner

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
