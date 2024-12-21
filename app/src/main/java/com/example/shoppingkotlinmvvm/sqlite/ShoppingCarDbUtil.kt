package com.example.shoppingkotlinmvvm.sqlite

import android.content.ContentValues

class ShoppingCarDbUtil {
    companion object{
        //新增資料
        @JvmStatic fun addShoppingCart(
            dbHelper: ShoppingCartDatabaseHelper,
            goodsCode: String?,
            goodsName: String?,
            goodsSubName: String?,
            goodsPrice: String?,
            salePrice: String?,
            goodsStock: String?,
            goodsFeatureUrl: String?,
            warranty: String?,
            brand: String?,
            discount: String?,
            imgUrl: String?,
            count: String?,
            isChecked: String?
        ): Long {
            val database = dbHelper.writableDatabase //取得連線
            //組資料送給資料庫
            val values = ContentValues()
            values.put(ShoopingCartEntry.GOODS_CODE, goodsCode) //商品編號
            values.put(ShoopingCartEntry.GOODS_NAME, goodsName) //商品名稱
            values.put(ShoopingCartEntry.GOODS_SUB_NAME, goodsSubName) //折扣
            values.put(ShoopingCartEntry.GOODS_PRICE, goodsPrice) //商品價格
            values.put(ShoopingCartEntry.SALE_PRICE, salePrice)
            values.put(ShoopingCartEntry.GOODS_STOCK, goodsStock) //商品數量
            values.put(ShoopingCartEntry.GOODS_FEATURE_URL, goodsFeatureUrl) //商品介紹
            values.put(ShoopingCartEntry.WARRANTY, warranty) //保固日期
            values.put(ShoopingCartEntry.BRAND, brand) //品牌
            values.put(ShoopingCartEntry.DISCOUNT, discount) //特惠商品
            values.put(ShoopingCartEntry.IMG_URL, imgUrl) //商品圖片
            values.put(ShoopingCartEntry.COUNT, count) //商品加入購物車後的數量
            values.put(ShoopingCartEntry.IS_CHECKED, isChecked)  //商品是否被選中
            //傳送組好的資料給DB,回傳結果(newRowId)
            return database.insert(ShoopingCartEntry.TABLE_NAME, null, values) //回傳的結果
        }

        //讀取全部資料
        @JvmStatic fun getShoppingCartAll(dbHelper: ShoppingCartDatabaseHelper): List<MutableMap<String, String>>? {
            val database = dbHelper.readableDatabase
            val projection = arrayOf(
                ShoopingCartEntry.GOODS_CODE,
                ShoopingCartEntry.GOODS_NAME,
                ShoopingCartEntry.GOODS_SUB_NAME,
                ShoopingCartEntry.GOODS_PRICE,
                ShoopingCartEntry.SALE_PRICE,
                ShoopingCartEntry.GOODS_STOCK,
                ShoopingCartEntry.GOODS_FEATURE_URL,
                ShoopingCartEntry.WARRANTY,
                ShoopingCartEntry.BRAND,
                ShoopingCartEntry.DISCOUNT,
                ShoopingCartEntry.IMG_URL,
                ShoopingCartEntry.COUNT,
                ShoopingCartEntry.IS_CHECKED
            )

            //查詢
            val cursor = database.query(
                ShoopingCartEntry.TABLE_NAME,  //要查詢的資料表
                projection,  //要查詢的欄位(使用null表示全部欄位)
                null,  //selection,                                   //過濾欄位(相當於WHERE後面的條件)
                null,  //selectionArgs,                               //過濾欄位的資料(相當於WHERE後面條件的資料)
                null,  //分組(相當於SQL中GROUP BY後面的語法)
                null,  //(相當於SQL中HAVING後面的語法)
                null //sortOrder                                    //排序(相當於ORDER BY後面的語法)
            )
            //將資料放入List
            val itemList: MutableList<MutableMap<String, String>> = ArrayList()
            while (cursor.moveToNext()) {
                val goodsCode =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.GOODS_CODE))
                val goodsName =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.GOODS_NAME))
                val goodsSubName =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.GOODS_SUB_NAME))
                val goodsPrice =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.GOODS_PRICE))
                val salePrice =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.SALE_PRICE))
                val goodsStock =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.GOODS_STOCK))
                val goodsFeatureUrl =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.GOODS_FEATURE_URL))
                val warranty =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.WARRANTY))
                val brand =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.BRAND))
                val discount =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.DISCOUNT))
                val imgUrl =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.IMG_URL))
                val count =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.COUNT))
                val isChecked =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.IS_CHECKED))

                //加入至List
                val row: MutableMap<String, String> = HashMap()
                row[ShoopingCartEntry.GOODS_CODE] = goodsCode
                row[ShoopingCartEntry.GOODS_NAME] = goodsName
                row[ShoopingCartEntry.GOODS_SUB_NAME] = goodsSubName
                row[ShoopingCartEntry.GOODS_PRICE] = goodsPrice
                row[ShoopingCartEntry.SALE_PRICE] = salePrice
                row[ShoopingCartEntry.GOODS_STOCK] = goodsStock
                row[ShoopingCartEntry.GOODS_FEATURE_URL] = goodsFeatureUrl
                row[ShoopingCartEntry.WARRANTY] = warranty
                row[ShoopingCartEntry.BRAND] = brand
                row[ShoopingCartEntry.DISCOUNT] = discount
                row[ShoopingCartEntry.IMG_URL] = imgUrl
                row[ShoopingCartEntry.COUNT] = count
                row[ShoopingCartEntry.IS_CHECKED] =isChecked
                itemList.add(row)
            }
            cursor.close()
            return itemList
        }

        //讀取資料
        @JvmStatic fun getShoppingCart(
            dbHelper: ShoppingCartDatabaseHelper,
            gCode: String
        ): List<MutableMap<String, String>>? {
            val database = dbHelper.readableDatabase
            val projection = arrayOf(
                ShoopingCartEntry.GOODS_CODE,
                ShoopingCartEntry.GOODS_NAME,
                ShoopingCartEntry.GOODS_SUB_NAME,
                ShoopingCartEntry.GOODS_PRICE,
                ShoopingCartEntry.SALE_PRICE,
                ShoopingCartEntry.GOODS_STOCK,
                ShoopingCartEntry.GOODS_FEATURE_URL,
                ShoopingCartEntry.WARRANTY,
                ShoopingCartEntry.BRAND,
                ShoopingCartEntry.DISCOUNT,
                ShoopingCartEntry.IMG_URL,
                ShoopingCartEntry.COUNT,
                ShoopingCartEntry.IS_CHECKED
            )
            val selection = ShoopingCartEntry.GOODS_CODE + " = ? " //查詢的WHERE條件
            val selectionArgs = arrayOf(gCode) //查詢的WHERE內容

            //查詢
            val cursor = database.query(
                ShoopingCartEntry.TABLE_NAME,  //要查詢的資料表
                projection,  //要查詢的欄位(使用null表示全部欄位)
                selection,  //過濾欄位(相當於WHERE後面的條件)
                selectionArgs,  //過濾欄位的資料(相當於WHERE後面條件的資料)
                null,  //分組(相當於SQL中GROUP BY後面的語法)
                null,  //(相當於SQL中HAVING後面的語法)
                null //sortOrder                                    //排序(相當於ORDER BY後面的語法)
            )
            //將資料放入List
            val itemList: MutableList<MutableMap<String, String>> = ArrayList()
            while (cursor.moveToNext()) {
                val goodsCode =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.GOODS_CODE))
                val goodsName =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.GOODS_NAME))
                val goodsSubName =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.GOODS_SUB_NAME))
                val goodsPrice =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.GOODS_PRICE))
                val salePrice =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.SALE_PRICE))
                val goodsStock =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.GOODS_STOCK))
                val goodsFeatureUrl =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.GOODS_FEATURE_URL))
                val warranty =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.WARRANTY))
                val brand =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.BRAND))
                val discount =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.DISCOUNT))
                val imgUrl =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.IMG_URL))
                val count =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.COUNT))
                val isChecked =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoopingCartEntry.IS_CHECKED))

                //加入至List
                val row: MutableMap<String, String> = HashMap()
                row[ShoopingCartEntry.GOODS_CODE] = goodsCode
                row[ShoopingCartEntry.GOODS_NAME] = goodsName
                row[ShoopingCartEntry.GOODS_SUB_NAME] = goodsSubName
                row[ShoopingCartEntry.GOODS_PRICE] = goodsPrice
                row[ShoopingCartEntry.SALE_PRICE] = salePrice
                row[ShoopingCartEntry.GOODS_STOCK] = goodsStock
                row[ShoopingCartEntry.GOODS_FEATURE_URL] = goodsFeatureUrl
                row[ShoopingCartEntry.WARRANTY] = warranty
                row[ShoopingCartEntry.BRAND] = brand
                row[ShoopingCartEntry.DISCOUNT] = discount
                row[ShoopingCartEntry.IMG_URL] = imgUrl
                row[ShoopingCartEntry.COUNT] = count
                row[ShoopingCartEntry.IS_CHECKED] = isChecked
                itemList.add(row)
            }
            cursor.close()
            return itemList
        }


        //刪除資料
        @JvmStatic fun deleteShoppingCartItem(dbHelper: ShoppingCartDatabaseHelper, goodsCode: String) {
            val database = dbHelper.writableDatabase
            val selection = ShoopingCartEntry.GOODS_CODE + " = ? "
            val selectionArgs = arrayOf(goodsCode)
            //回傳刪除的資料筆數
            val count = database.delete(
                ShoopingCartEntry.TABLE_NAME,  //要刪除資料所在的資料表
                selection,  //篩選欄位(相當於WHERE後面的條件)
                selectionArgs //篩選欄位的資料(相當於WHERE後面條件的資料)
            )
        }

        // 更新資料
        @JvmStatic fun updateShoppingCar(
            dbHelper: ShoppingCartDatabaseHelper,
            goodsCode: String?,
            goodsName: String?,
            goodsSubName: String?,
            goodsPrice: String?,
            salePrice: String?,
            goodsStock: String?,
            goodsFeatureUrl: String?,
            warranty: String?,
            brand: String?,
            discount: String?,
            imgUrl: String?,
            count: String?,
            isChecked: String?
        ): Int {
            val database = dbHelper.writableDatabase
            val values = ContentValues()
            values.put(ShoopingCartEntry.GOODS_CODE, goodsCode) //商品編號
            values.put(ShoopingCartEntry.GOODS_NAME, goodsName) //商品名稱
            values.put(ShoopingCartEntry.GOODS_SUB_NAME, goodsSubName) //折扣
            values.put(ShoopingCartEntry.GOODS_PRICE, goodsPrice)  //商品價格
            values.put(ShoopingCartEntry.SALE_PRICE, salePrice)
            values.put(ShoopingCartEntry.GOODS_STOCK, goodsStock) //商品數量
            values.put(ShoopingCartEntry.GOODS_FEATURE_URL, goodsFeatureUrl) //商品介紹
            values.put(ShoopingCartEntry.WARRANTY, warranty) //保固日期
            values.put(ShoopingCartEntry.BRAND, brand) //品牌
            values.put(ShoopingCartEntry.DISCOUNT, discount) //特惠商品
            values.put(ShoopingCartEntry.IMG_URL, imgUrl) //商品圖片
            values.put(ShoopingCartEntry.COUNT, count) //商品加入購物車後的數量
            values.put(ShoopingCartEntry.IS_CHECKED, isChecked)  //商品是否被選中

            // 篩選欄位
            val selection = ShoopingCartEntry.GOODS_CODE + " = ?"
            val selectionArgs = arrayOf(goodsCode)

            // 回傳更新的資料筆數
            return database.update(
                ShoopingCartEntry.TABLE_NAME,  // 要更新的資料表
                values,  // 更新資料
                selection,  // 篩選欄位 (相當於 WHERE 後面的條件)
                selectionArgs // 篩選欄位的資料 (相當於 WHERE 後面條件的資料)
            )
        }
    }
}