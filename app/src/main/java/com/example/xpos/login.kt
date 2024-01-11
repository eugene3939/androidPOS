package com.example.xpos

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.xpos.databinding.ActivityLoginBinding
import com.example.xpos.ui.dataBaseManager.ProductDataBaseHelper
import com.example.xpos.ui.dataBaseManager.TransactionDataBaseHelper
import com.example.xpos.ui.dataBaseManager.UserDataBaseHelper

class login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var dbrw: SQLiteDatabase

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 使用 View Binding 初始化綁定
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createTransactionDB() //創建TransactionDB
        createProductDB() //創建ProductDB
        createUserDB()  //創建UserDB

        // 登入按鈕
        binding.btnLogin.setOnClickListener {
            // 取得帳號、密碼
            val acc = binding.edtAcc.text.toString()
            val pas = binding.edtPas.text.toString()

            val loginQuery = "SELECT * FROM UserTable WHERE account = '$acc' AND password = '$pas';"
            val loginCursor = dbrw.rawQuery(loginQuery, null)

            // 檢查是否有查詢結果
            if (loginCursor.moveToFirst()) {
                val userName = loginCursor.getString(loginCursor.getColumnIndex("uName")) //取得用戶名稱
                val intent = Intent(this, MainActivity::class.java)
                Toast.makeText(this, "Welcome: $userName", Toast.LENGTH_SHORT).show()
                Log.d("用戶登入成功", "用戶名稱: $userName")
                startActivity(intent)
            } else {
                // 資料庫中未包含 User 的資料
                Toast.makeText(this, "登入失敗", Toast.LENGTH_SHORT).show()
                Log.d("登入失敗提示: ", loginQuery)
            }

            loginCursor?.close() // 確保在使用完畢後關閉 Cursor
        }
    }

    private fun createUserDB(){
        // 初始化資料庫
        val dbHelper = UserDataBaseHelper(this)
        dbrw = dbHelper.writableDatabase

        // 檢查 UserTable 是否為空
        val isEmptyQuery = "SELECT COUNT(*) FROM UserTable;"
        val countCursor = dbrw.rawQuery(isEmptyQuery, null)

        if (countCursor.moveToFirst()) {
            val count = countCursor.getInt(0)
            //UserTable為空，新增預設資料進table
            if (count == 0) {
                Log.d("UserTable為空", "還沒有放資料")
                // 新增預設用戶 Eugene
                dbrw.execSQL("INSERT INTO UserTable(uName, account, password) VALUES('Eugene', 1, 1);")
                dbrw.execSQL("INSERT INTO UserTable(uName, account, password) VALUES('Oscar', 3, 3);")

                Log.d("成功新增", "預設用戶")
            } else {    //顯示用戶內容
                Log.d("UserTable不為空", "他一共有 $count 組rows.") }
        } else {
            Log.e("UserTable有其他問題", "Error in counting rows.")
        }

        countCursor.close()
    }

    private fun createProductDB() {
        // 初始化商品資料庫
        val dbHelper = ProductDataBaseHelper(this)
        dbrw = dbHelper.writableDatabase

        // 檢查 ProductTable 是否為空
        val isEmptyQuery = "SELECT COUNT(*) FROM ProductTable;"
        val countCursor = dbrw.rawQuery(isEmptyQuery, null)

        if (countCursor.moveToFirst()) {
            val count = countCursor.getInt(0)
            //UserTable為空，新增預設資料進table
            if (count == 0) {
                Log.d("ProductTable為空", "還沒有放資料")
                // 新增預設商品Apple、Pineapple、Snapple
                dbrw.execSQL(/* sql = */ "INSERT INTO ProductTable(pName, pPrice, pNumber,pPhoto) VALUES('Apple', 50, 100, '0');")
                dbrw.execSQL(/* sql = */ "INSERT INTO ProductTable(pName, pPrice, pNumber,pPhoto) VALUES('Pineapple', 100, 80, '0');")
                dbrw.execSQL(/* sql = */ "INSERT INTO ProductTable(pName, pPrice, pNumber,pPhoto) VALUES('Snapple', 200, 60, '0');")

                Log.d("成功新增", "3組預設商品")
            } else {    //顯示用戶內容
                Log.d("ProductTable不為空", "他一共有 $count 組rows.") }
        } else {
            Log.e("ProductTable有其他問題", "Error in counting rows.")
        }

        countCursor.close()

    }

    private fun createTransactionDB() {
        // 初始化商品資料庫
        val dbHelper = TransactionDataBaseHelper(this)
        dbrw = dbHelper.writableDatabase

        // 檢查 ProductTable 是否為空
        val isEmptyQuery = "SELECT COUNT(*) FROM TransactionTable;"
        val countCursor = dbrw.rawQuery(isEmptyQuery, null)

        if (countCursor.moveToFirst()) {
            val count = countCursor.getInt(0)
            //UserTable為空，新增預設資料進table
            if (count == 0) {
                Log.d("ProductTable為空", "還沒有放資料")
                // 新增預設商品Apple、Pineapple、Snapple
                dbrw.execSQL("INSERT INTO TransactionTable(tDate, tDescription) VALUES('2018-12-10','0');")
                dbrw.execSQL("INSERT INTO TransactionTable(tDate, tDescription) VALUES('2018-12-11','0');")
                dbrw.execSQL("INSERT INTO TransactionTable(tDate, tDescription) VALUES('2018-12-12','0');")

                Log.d("成功新增", "3組預設商品")
            } else {    //顯示用戶內容
                Log.d("TransactionTable不為空", "他一共有 $count 組rows.") }
        } else {
            Log.e("TransactionTable有其他問題", "Error in counting rows.")
        }

        countCursor.close()

    }

    override fun onDestroy() {
        // 在 Activity 銷毀時關閉資料庫連接
        dbrw.close()
        super.onDestroy()
    }
}
