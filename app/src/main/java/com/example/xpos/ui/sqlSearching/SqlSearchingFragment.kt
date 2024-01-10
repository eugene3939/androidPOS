package com.example.xpos.ui.sqlSearching

import android.annotation.SuppressLint
import android.content.res.Resources
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.xpos.R
import com.example.xpos.databinding.FragmentSlideshowBinding
import com.example.xpos.databinding.FragmentSqlSearchingBinding
import com.example.xpos.ui.dataBaseManager.ProductDataBaseHelper
import com.example.xpos.ui.dataBaseManager.UserDataBaseHelper

class SqlSearchingFragment : Fragment() {

    private var _binding: FragmentSqlSearchingBinding? = null

    private val binding get() = _binding!!
    private lateinit var dbrw: SQLiteDatabase

    // 在類別內部宣告一個空的List<String>用來存放欄位名稱
    private val data: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSqlSearchingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 初始化商品資料庫
        val dbHelper = UserDataBaseHelper(requireContext())
        dbrw = dbHelper.writableDatabase

        // 初始化 ViewModel
        val sqlSearchingViewModel =
            ViewModelProvider(this).get(SqlSearchingViewModel::class.java)

        //預設資料庫索引為User (0)
        var nowDBid = 0

        Log.d("現在所在的資料庫索引在", "${nowDBid}號的資料庫")

        //標題1
        val textView: TextView = binding.textSqlSearchShow
        sqlSearchingViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        //下拉式選單顯示全部的table種類
        val tablesArray = resources.getStringArray(R.array.database_type) //全部的table種類
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,tablesArray)
        binding.sqlDbSpinner.adapter = spinnerAdapter

        //下拉式選單變更選擇的資料庫
        binding.sqlDbSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                //更新所在資料庫索引
                nowDBid = position

//              val selectedItem = parent?.getItemAtPosition(position)    //取得選擇的資料
                //更新GridView顯示所在資料庫內容
                updateGridView(nowDBid)
                Log.d("目前所在的Table索引是", "索引: $nowDBid")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.grDBShow.onItemClickListener = object : AdapterView.OnItemClickListener {

            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d("目前所在的GridView索引是", "位置: $id")
            }
        }

        //更新GridView顯示所在資料庫內容
        updateGridView(nowDBid)

        return root
    }


    // 更新 GridView 中的資料
    private fun updateGridView(nowDBid: Int) {
        // 清空先前的資料
        data.clear()
        // 獲取指定資料庫的資料
        getTableData(nowDBid)
        // 顯示在 GridView
        binding.grDBShow.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, data)
    }

    // 獲取指定資料庫的資料
    @SuppressLint("Range")
    private fun getTableData(nowDBid: Int) {
        // 清空先前的資料
        data.clear()
        // Table陣列
        val dbArrays = resources.getStringArray(R.array.database_type)
        val dbName = dbArrays[nowDBid].toString()

        // 根據位置初始化資料庫
        when (nowDBid) {
            0 -> {
                // 初始化 User 資料庫
                val dbHelper = UserDataBaseHelper(requireContext())
                dbrw = dbHelper.writableDatabase
                // 根據需要執行對 User 資料庫的查詢並處理資料
                fetchAllData("UserTable", arrayOf("Uid", "Uname", "account", "password"))
            }
            1 -> {
                // 初始化 Product 資料庫
                val dbHelper = ProductDataBaseHelper(requireContext())
                dbrw = dbHelper.writableDatabase
                // 根據需要執行對 Product 資料庫的查詢並處理資料
                fetchAllData("ProductTable", arrayOf("Pid", "Pname", "Pprice", "Pnumber", "Pphoto"))
            }
            else -> {
                // 其他情況，可以添加更多的條件分支
                data.add(" - Item 1")
                data.add(" - Item 2")
                data.add(" - Item 3")
            }
        }
    }

    // 抽取出的共用函數
    @SuppressLint("Range")
    private fun fetchAllData(tableName: String, columns: Array<String>) {
        // 執行查詢
        val query = "SELECT * FROM $tableName;"
        val cursor = dbrw.rawQuery(query, null)

        // 檢查是否有查詢結果
        if (cursor.moveToFirst()) {
            // 取得資料表的欄位名稱陣列
            val columnNames: Array<String> = cursor.columnNames
            // 加入欄位名稱到回傳項目
            data.addAll(columnNames.toList())

            do {
                // 讀取每一列的資料
                for (columnName in columns) {
                    data.add(cursor.getString(cursor.getColumnIndex(columnName)))
                }

                // 限制colum
                binding.grDBShow.numColumns = columns.size
            } while (cursor.moveToNext())
        }

        cursor.close()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
