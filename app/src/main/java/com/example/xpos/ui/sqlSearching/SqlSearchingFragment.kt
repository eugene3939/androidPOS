package com.example.xpos.ui.sqlSearching

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.xpos.R
import com.example.xpos.databinding.FragmentSqlSearchingBinding
import com.example.xpos.ui.dataBaseManager.ProductDataBaseHelper
import com.example.xpos.ui.dataBaseManager.TransactionDataBaseHelper
import com.example.xpos.ui.dataBaseManager.UserDataBaseHelper


class SqlSearchingFragment : Fragment() {

    private var _binding: FragmentSqlSearchingBinding? = null

    private val binding get() = _binding!!
    private lateinit var dbrw: SQLiteDatabase

    //table的欄位名稱
    private var userColumns: MutableList<String> = mutableListOf()
    private var productColumns: MutableList<String> = mutableListOf()
    private var transactionColumns: MutableList<String> = mutableListOf()

    // 在類別內部宣告一個空的List<String>用來存放欄位名稱
    private val data: MutableList<String> = mutableListOf()

    //現在的欄位個數
    private var currentColumnsNum: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSqlSearchingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //初始化table的欄位名稱
        userColumns = resources.getStringArray(R.array.UserDBitems).toMutableList()
        productColumns = resources.getStringArray(R.array.ProductDBitems).toMutableList()
        transactionColumns = resources.getStringArray(R.array.TransactionDBitems).toMutableList()

            // 初始化商品資料庫
        val dbHelper = UserDataBaseHelper(requireContext())
        dbrw = dbHelper.writableDatabase

        // 初始化 ViewModel
        val sqlSearchingViewModel =
            ViewModelProvider(this)[SqlSearchingViewModel::class.java]

        //預設資料庫索引為User (0)
        var nowDBid = 0

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
                updateGridView(nowDBid,null)
                Log.d("目前所在的Table索引是", "索引: $nowDBid")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        //Gridview點擊處理
        binding.grDBShow.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->

                //顯示點擊id
                Toast.makeText(requireContext(), "$position", Toast.LENGTH_SHORT).show()
            }

        //更新GridView顯示所在資料庫內容
        updateGridView(nowDBid,null)

        //查找欄位
        binding.btnSearch.setOnClickListener {
            val edtColumn: String = binding.edtQuery.text.toString()
            if (edtColumn != ""){   //有輸入才開始找
                updateGridView(nowDBid, edtColumn)
            }
        }

        return root
    }

    // 更新 GridView 中的資料
    private fun updateGridView(nowDBid: Int, searchColumns: String?) {
        // 清空先前的資料
        data.clear()
        // 獲取指定資料庫的資料
        getTableData(nowDBid,searchColumns)
        // 顯示在 GridView
        binding.grDBShow.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, data)
    }

    // 獲取指定資料庫的資料
    @SuppressLint("Range")
    private fun getTableData(nowDBid: Int,searchColumns: String?) {
        // 清空先前的資料
        data.clear()

        // 根據位置初始化資料庫
        when (nowDBid) {
            0 -> {
                // 初始化 User 資料庫
                val dbHelper = UserDataBaseHelper(requireContext())
                dbrw = dbHelper.writableDatabase
                // 根據需要執行對 User 資料庫的查詢並處理資料
                if (searchColumns==null){   //沒有單一查詢
                    selectionAllColumnData("UserTable", userColumns)
                }else{      //有單一查詢
                    selectionNameColumnData("UserTable", searchColumns)
                }

                edtAutoFilling(userColumns)    //editText自動填詞
            }
            1 -> {
                // 初始化 Product 資料庫
                val dbHelper = ProductDataBaseHelper(requireContext())
                dbrw = dbHelper.writableDatabase
                // 根據需要執行對 Product 資料庫的查詢並處理資料
                if (searchColumns==null){   //沒有單一查詢
                    selectionAllColumnData("ProductTable", productColumns)
                }else{      //有單一查詢
                    selectionNameColumnData("ProductTable",searchColumns)
                }

                edtAutoFilling(productColumns)  //editText自動填詞
            }
            else -> {
                // 初始化 Transaction 資料庫
                val dbHelper = TransactionDataBaseHelper(requireContext())
                dbrw = dbHelper.writableDatabase
                // 根據需要執行對 TransactionTable 的查詢並處理資料
                if (searchColumns==null){   //沒有單一查詢
                    selectionAllColumnData("TransactionTable", transactionColumns)
                }else{      //有單一查詢
                    selectionNameColumnData("TransactionTable",searchColumns)
                }

                edtAutoFilling(transactionColumns)  //editText自動填詞
            }
        }
    }

    //editText自動填詞
    private fun edtAutoFilling(dbColumns: MutableList<String>) {
        val autoCompleteAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, dbColumns)
        binding.edtQuery.setAdapter(autoCompleteAdapter)
    }

    // 搜尋部分欄位
    @SuppressLint("Range")
    private fun selectionNameColumnData(tableName: String, columnName: String?) {
        Log.d("目前欄位", "$columnName")

        // 執行查詢
        val query = "SELECT $columnName FROM $tableName;"
        Log.d("目前query 安安", query)

        val cursor = dbrw.rawQuery(query, null)

        Log.d("目前query", query)

        // 檢查是否有查詢結果
        if (cursor != null && cursor.moveToFirst()) {
            // 取得資料表的欄位名稱陣列
            val columnNames: Array<String> = cursor.columnNames
            // 加入欄位名稱到回傳項目
            data.addAll(columnNames.toList())

            do {
                // 讀取每一列的資料
                val columnIndex = cursor.getColumnIndex(columnName)
                data.add(cursor.getString(columnIndex))

            } while (cursor.moveToNext())

            // 限制column
            binding.grDBShow.numColumns = 1
        } else {
            Toast.makeText(requireContext(), "Table還沒建立喔", Toast.LENGTH_SHORT).show()
        }

        cursor.close()
    }

    // 搜尋全部欄位
    @SuppressLint("Range")
    private fun selectionAllColumnData(tableName: String, columns: MutableList<String>) {
        Log.d("目前欄位","$columns")

        // 將欄位名稱轉換成字串，用於構建SQL查詢語句
        val columnString = columns.joinToString(", ") // 將欄位名稱以逗號分隔

        // 執行查詢
        val query = "SELECT $columnString FROM $tableName;"
        val cursor = dbrw.rawQuery(query, null)

        // 檢查是否有查詢結果
        if (cursor!= null && cursor.moveToFirst()) {

            // 取得資料表的欄位名稱陣列
            val columnNames: Array<String> = cursor.columnNames
            // 加入欄位名稱到回傳項目
            data.addAll(columnNames.toList())

            // 更新全域變數 currentColumnsSize
            currentColumnsNum = columnNames.size

            do {
                // 讀取每一列的資料
                for (columnName in columns) {
                    val columnIndex = cursor.getColumnIndex(columnName)
                    data.add(cursor.getString(columnIndex))
                }
            } while (cursor.moveToNext())

            // 限制column
            binding.grDBShow.numColumns = currentColumnsNum
        }else{
            Toast.makeText(requireContext(), "Table還沒建立喔",Toast.LENGTH_SHORT).show()
        }

        cursor.close()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}