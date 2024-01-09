package com.example.xpos

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.xpos.databinding.ActivityLoginBinding
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
                dbrw.execSQL("INSERT INTO UserTable(UName, account, password) VALUES('Eugene', 1, 1);")
                dbrw.execSQL("INSERT INTO UserTable(UName, account, password) VALUES('Oscar', 3, 3);")

                Log.d("成功新增", "預設用戶")
            } else {    //顯示用戶內容
                Log.d("UserTable不為空", "他一共有 $count 組rows.") }
        } else {
            Log.e("UserTable有其他問題", "Error in counting rows.")
        }

        countCursor.close()

        // 登入按鈕
        binding.btnLogin.setOnClickListener {
            // 取得帳號、密碼
            val acc = binding.edtAcc.text.toString()
            val pas = binding.edtPas.text.toString()

            val loginQuery = "SELECT * FROM UserTable WHERE account = ${acc} AND password = ${pas};"
            val loginCursor = dbrw.rawQuery(loginQuery, null)

            // 檢查是否有查詢結果
            if (loginCursor.moveToFirst()) {
                val userName = loginCursor.getString(loginCursor.getColumnIndex("Uname")) //取得用戶名稱
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

    override fun onDestroy() {
        // 在 Activity 銷毀時關閉資料庫連接
        dbrw.close()
        super.onDestroy()
    }
}
