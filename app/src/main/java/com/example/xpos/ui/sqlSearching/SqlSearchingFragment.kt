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
import android.widget.Toast
import androidx.fragment.app.Fragment
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
    private var nowColums: MutableList<String> = mutableListOf()

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

        //預設資料庫索引為User (0)
        var nowDBid = 0

        //下拉式選單顯示全部的table種類
        val tablesArray = resources.getStringArray(R.array.database_type) //全部的table種類
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,tablesArray)
        binding.sqlDbSpinner.adapter = spinnerAdapter

        //下拉式選單變更選擇的資料庫
        binding.sqlDbSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //更新所在資料庫索引
                nowDBid = position

                val selectedItem = parent?.getItemAtPosition(position)    //取得選擇的資料
                //更新GridView顯示所在資料庫內容
                updateGridView(nowDBid,null)

                Log.d("目前所在的Table索引是", "索引: $selectedItem")
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

                //更新顯示欄位
                nowColums.clear()
                nowColums.add(edtColumn)
                updateColumnNameShow()
            }
        }

        return root
    }

    //顯示目前columns名稱
    private fun updateColumnNameShow(){
        val rowNameAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,nowColums)
        binding.gsCheckbox.adapter = rowNameAdapter
        binding.gsCheckbox.numColumns = nowColums.size
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
                nowColums = resources.getStringArray(R.array.UserDBitems).toMutableList()

                // 根據需要執行對 User 資料庫的查詢並處理資料
                selectionData("UserTable",nowColums,searchColumns)
            }
            1 -> {
                // 初始化 Product 資料庫
                val dbHelper = ProductDataBaseHelper(requireContext())
                dbrw = dbHelper.writableDatabase
                nowColums = resources.getStringArray(R.array.ProductDBitems).toMutableList()

                // 根據需要執行對 Product 資料庫的查詢並處理資料
                selectionData("ProductTable",nowColums,searchColumns)
            }
            else -> {
                // 初始化 Transaction 資料庫
                val dbHelper = TransactionDataBaseHelper(requireContext())
                dbrw = dbHelper.writableDatabase
                nowColums = resources.getStringArray(R.array.TransactionDBitems).toMutableList()

                // 根據需要執行對 TransactionTable 的查詢並處理資料
                selectionData("TransactionTable",nowColums,searchColumns)
            }
        }

        updateColumnNameShow()   //更新rowName
        edtAutoFilling(nowColums)  //editText自動填詞
    }


    //editText自動填詞
    private fun edtAutoFilling(dbColumns: MutableList<String>) {
        val autoCompleteAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, dbColumns)
        binding.edtQuery.setAdapter(autoCompleteAdapter)
    }

    //依照欄位數、欄位名稱搜尋資料庫Select SQLite
    @SuppressLint("Range")
    private fun selectionData(tableName: String, columns: MutableList<String>, columnName: String?){
        Log.d("目前欄位", "$columns")

        // 清空先前的資料(欄位名稱+欄位資料)
        data.clear()

        // 將欄位名稱轉換成字串，用於構建SQL查詢語句
        val columnString = // 如果有指定 columnName，就只查詢單一欄位
            columnName ?: // 否則查詢所有欄位
            columns.joinToString(", ") // 將欄位名稱以逗號分隔

        // 執行查詢
        val query = "SELECT $columnString FROM $tableName;"
        val cursor = dbrw.rawQuery(query, null)

        // 檢查是否有查詢結果
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // 讀取每一列的資料
                if (columnName != null) {
                    val columnIndex = cursor.getColumnIndex(columnName)
                    data.add(cursor.getString(columnIndex))
                } else {
                    for (column in columns) {
                        val columnIndex = cursor.getColumnIndex(column)
                        data.add(cursor.getString(columnIndex))
                    }
                }
            } while (cursor.moveToNext())

            // 如果是搜尋部分欄位，更新 nowColums
            if (columnName != null) {
                nowColums.clear()
                nowColums.add(columnName)
            }

            // 限制column
            binding.grDBShow.numColumns = nowColums.size
        } else {
            Toast.makeText(requireContext(), "Table還沒建立喔", Toast.LENGTH_SHORT).show()
        }

        cursor.close()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}