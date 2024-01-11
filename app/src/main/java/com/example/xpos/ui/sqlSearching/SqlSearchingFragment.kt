package com.example.xpos.ui.sqlSearching

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.xpos.R
import com.example.xpos.databinding.FragmentSqlSearchingBinding
import com.example.xpos.ui.dataBaseManager.ProductDataBaseHelper
import com.example.xpos.ui.dataBaseManager.UserDataBaseHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SqlSearchingFragment : Fragment() {

    private var _binding: FragmentSqlSearchingBinding? = null

    private val binding get() = _binding!!
    private lateinit var dbrw: SQLiteDatabase

    //table的欄位名稱
    private var userColumns: MutableList<String> = mutableListOf()
    private var productColumns: MutableList<String> = mutableListOf()

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
                updateGridView(nowDBid)
                Log.d("目前所在的Table索引是", "索引: $nowDBid")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        //Gridview點擊處理
        binding.grDBShow.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                // 創建 Bundle，將GridView點擊位置資訊放入
                val bundle = Bundle()
                bundle.putInt("clickedGridViewPosition", position)

                // 創建 BottomSheetFragment 並將 Bundle 放入
                val bottomSheetFragment = BottomSheetFragment()
                bottomSheetFragment.arguments = bundle

                // 顯示 BottomSheetFragment
                bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
                //Toast.makeText(requireContext(), data[position], Toast.LENGTH_SHORT).show()
            }

        //更新GridView顯示所在資料庫內容
        updateGridView(nowDBid)

        return root
    }

    //AlertDialoge 連接編輯頁面
    class EditDataDialog : DialogFragment() {
        // EditDataDialog的接口
        fun showEditDataDialog() {
            val editDataDialog = EditDataDialog()
            editDataDialog.show(parentFragmentManager, "EditDataDialog")
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
            val builder = AlertDialog.Builder(requireActivity())
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.sql_modify_items, null)

            // 獲取佈局中的控件
            val edtItems = view.findViewById<EditText>(R.id.edtItems)
            val btnPositive = view.findViewById<Button>(R.id.btnPositive)
            val btnNegative = view.findViewById<Button>(R.id.btnNegative)

            // 設置按鈕點擊事件
            btnPositive.setOnClickListener {
                Log.d("你好","安安")
                //edtItems.setText("你好安安")

                // 調用外部類別的方法，更新 TextView 的內容
                showEditDataDialog()
//              dismiss()
            }

            btnNegative.setOnClickListener {
                dismiss()
            }

            // 設置自訂佈局到對話框
            builder.setView(view)

            return builder.create()
        }
    }


    //bottom view sheet
    class BottomSheetFragment : BottomSheetDialogFragment() {

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.sql_search_items, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val listView: ListView = view.findViewById(R.id.ls_searchItems)

            // 資料操作選項
            val items = arrayOf("新增","修改", "刪除")

            // 創建 ArrayAdapter 並設置到 ListView 中
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
            listView.adapter = adapter

            // 設置 ListView 的點擊事件
            listView.setOnItemClickListener { _, _, position, _ ->
//                val selectedItem = items[position]

                // 從 arguments 中取得 Bundle
                val bundle = arguments

                //有bundle才進行讀取
                if (bundle != null) {
                    // 從 Bundle 中取得點擊的位置
                    val clickedPosition = bundle.getInt("clickedGridViewPosition", -1)

                    // 在這裡使用點擊的位置進行相應的處理
                    if (clickedPosition == -1) {
                        //Log.d("不正規的點擊","早安")
                    }else{
                        Log.d("Bundle回傳","午安 $clickedPosition")
                        //前往對應的服務項目

                        when (position) {
                            0 -> {
                                //連接到alertDialog
                                Toast.makeText(requireContext(), "新增 $clickedPosition", Toast.LENGTH_SHORT).show()
                            }
                            1 ->{
                                val editDialog = EditDataDialog()
                                editDialog.show(requireActivity().supportFragmentManager, "CustomDialog")
                                Toast.makeText(requireContext(), "修改 $clickedPosition", Toast.LENGTH_SHORT).show()
                            }
                            else -> Toast.makeText(requireContext(), "刪除 $clickedPosition", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // 在這裡處理選擇的項目
                dismiss()  // 關閉 BottomSheetDialog
            }
        }
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
        //val dbArrays = resources.getStringArray(R.array.database_type)

        // 根據位置初始化資料庫
        when (nowDBid) {
            0 -> {
                // 初始化 User 資料庫
                val dbHelper = UserDataBaseHelper(requireContext())
                dbrw = dbHelper.writableDatabase
                // 根據需要執行對 User 資料庫的查詢並處理資料
                fetchAllData("UserTable", userColumns)
            }
            1 -> {
                // 初始化 Product 資料庫
                val dbHelper = ProductDataBaseHelper(requireContext())
                dbrw = dbHelper.writableDatabase
                // 根據需要執行對 Product 資料庫的查詢並處理資料
                fetchAllData("ProductTable", productColumns)
            }
            else -> {
                // 其他情況，可以添加更多的條件分支
                data.add(" - Item 1")
                data.add(" - Item 2")
                data.add(" - Item 3")
            }
        }
    }

    // 搜尋全部欄位
    @SuppressLint("Range")
    private fun fetchAllData(tableName: String, columns: MutableList<String>) {
        // 執行查詢
        val query = "SELECT * FROM $tableName;"
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

                // 限制column
                binding.grDBShow.numColumns = currentColumnsNum
            } while (cursor.moveToNext())
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