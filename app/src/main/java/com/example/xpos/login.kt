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

        /// 檢查是否存在預設用戶 Eugene
        val checkQuery = "SELECT * FROM UserTable WHERE UName='Eugene' AND account = 1 AND password = 1;"
        val checkCursor = dbrw.rawQuery(checkQuery, null)

        if (checkCursor.count == 0) {
            // 新增預設用戶
            dbrw.execSQL("INSERT INTO UserTable(UName, account, password) VALUES('Eugene', 1, 1);")
            Log.d("成功新增","Eugene資訊")
        }

        checkCursor.close()


        // 登入按鈕
        binding.btnLogin.setOnClickListener {
            // 取得帳號、密碼
            val acc = binding.edtAcc.text.toString()
            val pas = binding.edtPas.text.toString()

            val loginQuery = "SELECT * FROM UserTable WHERE account = '$acc' AND password = '$pas';"
            val loginCursor = dbrw.rawQuery(loginQuery, null)

            if (loginCursor.moveToFirst()) {
                // 資料庫中包含 User 的資料
                val intent = Intent(this, MainActivity::class.java)
                Toast.makeText(this, "Welcome: $acc", Toast.LENGTH_SHORT).show()
                startActivity(intent)
            } else {
                // 資料庫中未包含 User 的資料
                Toast.makeText(this, "登入失敗", Toast.LENGTH_SHORT).show()
                Log.d("登入失敗提示: ", loginQuery)
            }
            loginCursor.close()
        }

        // 資料庫內容檢查
        val allQuery = "SELECT * FROM UserTable;"
        val allCursor = dbrw.rawQuery(allQuery, null)

        if (allCursor.moveToFirst()) {
            do {
                val uid = allCursor.getInt(allCursor.getColumnIndex("Uid"))
                val uname = allCursor.getString(allCursor.getColumnIndex("UName"))
                val account = allCursor.getInt(allCursor.getColumnIndex("account"))
                val password = allCursor.getInt(allCursor.getColumnIndex("password"))

                Log.d("用戶資料", "UID: $uid, UName: $uname, Account: $account, Password: $password")
            } while (allCursor.moveToNext())
        }

        allCursor.close()

    }

    override fun onDestroy() {
        // 在 Activity 銷毀時關閉資料庫連接
        dbrw.close()
        super.onDestroy()
    }
}
