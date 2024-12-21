package com.example.shoppingkotlinmvvm.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

//資料庫建立連線,放資料庫名稱跟版本,執行SQLiteOpenHelper(繼承)的super
class ShoppingCartDatabaseHelper(context: Context): SQLiteOpenHelper(
                                    context,
                                    DATABASE_NAME,
                                    null,
                                    DATABASE_VERSION
) {

    companion object {
        // 資料庫名稱
        val DATABASE_NAME = "goods.sqlite" //資料庫名稱
        // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
        val DATABASE_VERSION = 2
    }

    //資料表
    private val SQL_CREATE_ENTRIES = ("CREATE TABLE "
            + ShoopingCartEntry.TABLE_NAME + " ( "
            + ShoopingCartEntry.GOODS_CODE + " STRING PRIMARY KEY,"
            + ShoopingCartEntry.GOODS_NAME + " TEXT,"
            + ShoopingCartEntry.GOODS_SUB_NAME + " TEXT,"
            + ShoopingCartEntry.GOODS_PRICE + " TEXT,"
            + ShoopingCartEntry.SALE_PRICE + " TEXT,"
            + ShoopingCartEntry.GOODS_STOCK + " TEXT,"
            + ShoopingCartEntry.GOODS_FEATURE_URL + " TEXT,"
            + ShoopingCartEntry.WARRANTY + " TEXT,"
            + ShoopingCartEntry.BRAND + " TEXT,"
            + ShoopingCartEntry.DISCOUNT + " TEXT,"
            + ShoopingCartEntry.IMG_URL + " TEXT,"
            + ShoopingCartEntry.COUNT + " TEXT,"
            + ShoopingCartEntry.IS_CHECKED + " TEXT"+ ")")

    private val SQL_DELETE_ENTRIES =
        "DROP TABLE IF EXISTS " + ShoopingCartEntry.TABLE_NAME

    //產生資料表的 SQL 寫在這 onCreate
    //如果 Android 載入時找不到生成的資料庫檔案，就會觸發 onCreate
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }
    //onUpgrade則是如果資料庫結構有改變了就會觸發 onUpgrade
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }
}