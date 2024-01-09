package com.example.xpos

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.xpos.databinding.ActivityMainBinding
import com.example.xpos.ui.dataBaseManager.ProductDataBaseHelper
import com.example.xpos.ui.dataBaseManager.UserDataBaseHelper

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var dbrw: SQLiteDatabase //預設資料庫

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_sql_searcging
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

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
                dbrw.execSQL("INSERT INTO ProductTable(Pname, Pprice, Pnumber,Pphoto) VALUES('Apple', 50, 100, '0');")
                dbrw.execSQL("INSERT INTO ProductTable(Pname, Pprice, Pnumber,Pphoto) VALUES('Pineapple', 100, 80, '0');")
                dbrw.execSQL("INSERT INTO ProductTable(Pname, Pprice, Pnumber,Pphoto) VALUES('Snapple', 200, 60, '0');")

                Log.d("成功新增", "3組預設商品")
            } else {    //顯示用戶內容
                Log.d("ProductTable不為空", "他一共有 $count 組rows.") }
        } else {
            Log.e("ProductTable有其他問題", "Error in counting rows.")
        }

        countCursor.close()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}