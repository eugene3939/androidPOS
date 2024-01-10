package com.example.xpos.ui.dataBaseManager

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TransactionDataBaseHelper(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION){
    companion object {
        private const val DATABASE_NAME = "TransactionDB.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 在這裡建立資料庫表格等相關邏輯
//        db.execSQL("CREATE TABLE IF NOT EXISTS TransactionTable (" //建立ProductTable
//                + "Pid INTEGER PRIMARY KEY AUTOINCREMENT,"  //商品id
//                + "Pname TEXT,"     //商品名稱
//                + "Pprice INTEGER," //商品價錢
//                + "Pnumber INTEGER," //商品數量
//                + "Pphoto TEXT);")  //商品圖片
//
////        Log.d("進入table", "link start")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS TransactionTable;")
        onCreate(db)
    }
}