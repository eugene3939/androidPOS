package com.example.xpos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.xpos.databinding.ActivityLoginBinding

class login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 使用 View Binding 初始化綁定
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 直接使用綁定對象取得按鈕
        binding.btnLogin.setOnClickListener {

            //取得帳號、密碼
            var account = binding.edtAcc.text.toString()
            var password = binding.edtPas.text.toString()

            //確認身分1
            if (account == "1" && password == "1") {
                val intent = Intent(this, MainActivity::class.java)
                Toast.makeText(this, "Welcome: $account", Toast.LENGTH_SHORT).show()
                startActivity(intent)
            }else {
                Toast.makeText(this, "Login fail.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}