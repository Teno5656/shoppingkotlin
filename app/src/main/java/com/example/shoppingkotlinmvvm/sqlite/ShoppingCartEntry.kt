package com.example.shoppingkotlinmvvm.sqlite

import android.provider.BaseColumns

object ShoopingCartEntry : BaseColumns {
    //定義資料表結構
    const val TABLE_NAME = "ShoopingCar" //表單名稱
    const val GOODS_CODE = "goodsCode" //商品編號
    const val GOODS_NAME = "goodsName" //商品名稱
    const val GOODS_SUB_NAME = "goodsSubName" //折扣
    const val GOODS_PRICE = "goodsPrice" //商品價格(顯示用)
    const val SALE_PRICE = "salePrice" //商品價格(計算用)
    const val GOODS_STOCK = "goodsStock" //商品數量
    const val GOODS_FEATURE_URL = "goodsFeatureUrl" //商品介紹
    const val WARRANTY = "warranty"
    const val BRAND = "brand"
    const val DISCOUNT = "discount"
    const val IMG_URL = "imgurl"

    //商品加入購物車後的數量
    const val COUNT = "count"

    //商品是否被選中
    const val IS_CHECKED = "isChecked" //商品是否被選中:  0:未選中  1:已選中
}