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
        binding.sqlDbSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
        // 在這裡根據 databaseName 更新 GridView，你需要使用適當的邏輯和資料
        // 以下僅為示例，你應該根據實際需求處理資料
        val gridView: GridView = binding.grDBShow
        val gridAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, getTableData(nowDBid))
        gridView.adapter = gridAdapter
    }

    // 獲取指定資料庫的資料，這裡需要根據實際情況實現
    @SuppressLint("Range")
    private fun getTableData(nowDBid: Int): List<String> {
        //顯示資料清單
        val data: MutableList<String> = mutableListOf()
        //Table陣列
        val dbArrays = resources.getStringArray(R.array.database_type)
        val dbName = dbArrays[nowDBid].toString()

        //根據位置初始化資料庫
        when (nowDBid) {
            0 -> {
                // 初始化 User 資料庫
                val dbHelper = UserDataBaseHelper(requireContext())
                dbrw = dbHelper.writableDatabase
                // 根據需要執行對 User 資料庫的查詢並處理資料

                // 執行查詢
                val query = "SELECT * FROM UserTable;"
                val cursor = dbrw.rawQuery(query, null)

                // 檢查是否有查詢結果
                if (cursor.moveToFirst()) {

                    // 取得資料表的欄位名稱陣列
                    val columnNames: Array<String> = cursor.columnNames
                    // 加入欄位名稱到回傳項目
                    for (columnName in columnNames) {
                        data.add(columnName)
                    }

                    do {
                        // 讀取每一列的資料
                        val uid = cursor.getString(cursor.getColumnIndex("Uid"))
                        val userName = cursor.getString(cursor.getColumnIndex("Uname"))
                        val account = cursor.getInt(cursor.getColumnIndex("account"))
                        val password = cursor.getInt(cursor.getColumnIndex("password"))

                        // 合併欄位資料，這裡以字串形式呈現，你可以根據需要調整格式
//                        val rowData = "用戶名稱: $userName, 用戶帳號: $account, 用戶密碼: $password" //合併整欄

                        // 將合併後的資料添加到顯示清單
                        data.add("$uid")
                        data.add("$userName")
                        data.add("$account")
                        data.add("$password")

                        //限制colum
                        binding.grDBShow.numColumns=4

                    } while (cursor.moveToNext())
                }

                cursor.close()
            }
            1 -> {
                // 初始化 Product 資料庫
                val dbHelper = ProductDataBaseHelper(requireContext())
                dbrw = dbHelper.writableDatabase
                // 根據需要執行對 Product 資料庫的查詢並處理資料
                // 執行查詢
                val query = "SELECT * FROM ProductTable;"
                val cursor = dbrw.rawQuery(query, null)

                // 檢查是否有查詢結果
                if (cursor.moveToFirst()) {
                    // 取得資料表的欄位名稱陣列
                    val columnNames: Array<String> = cursor.columnNames

                    // 加入欄位名稱到回傳項目
                    for (columnName in columnNames) {
                        data.add(columnName)
                    }
                    do {1
                        // 讀取每一列的資料
                        val Pid = cursor.getString(cursor.getColumnIndex("Pid"))
                        val Pname = cursor.getString(cursor.getColumnIndex("Pname"))
                        val Pprice = cursor.getInt(cursor.getColumnIndex("Pprice"))
                        val Pnumber = cursor.getInt(cursor.getColumnIndex("Pnumber"))
                        val Pphoto = cursor.getInt(cursor.getColumnIndex("Pphoto"))

                        // 合併欄位資料，這裡以字串形式呈現，你可以根據需要調整格式
//                        val rowData = "商品名稱: $Pname, 商品價格: $Pprice, 商品數量: $Pnumber, 商品照片: $Pphoto"

                        // 將合併後的資料添加到顯示清單
                        data.add("$Pid")
                        data.add("$Pname")
                        data.add("$Pprice")
                        data.add("$Pnumber")
                        data.add("$Pphoto")

                        //限制colum
                        binding.grDBShow.numColumns=5

                    } while (cursor.moveToNext())
                }

                cursor.close()
            }
            else -> {
                // 其他情況，可以添加更多的條件分支
                data.add(" - Item 1")
                data.add(" - Item 2")
                data.add(" - Item 3")
            }
        }

        return data
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
