package com.example.shoppingkotlinmvvm

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppingkotlinmvvm.sqlite.ShoopingCartEntry
import com.example.shoppingkotlinmvvm.sqlite.ShoppingCarDbUtil
import com.example.shoppingkotlinmvvm.sqlite.ShoppingCartDatabaseHelper

class ContentViewModel : ViewModel(){
    var dbHelper: ShoppingCartDatabaseHelper? = null
    var totalCountLiveData = MutableLiveData<Int>()
    var goodsItemsLiveData = MutableLiveData<Int>()
    var resultLiveData = MutableLiveData<Long?>()
    var isChecked = "0"

    var carCount: String? = null  //商品加入購物數量

    fun start(context: Context){
        dbHelper = ShoppingCartDatabaseHelper(context)
        calculateShoppingCartQuantity()
    }

    //加入購物車
    fun addGoodsCount(goodsCode: String, goodsName: String, goodsSubName: String, goodsPrice: String, salePrice: String,
                      goodsStock: String, goodsFeatureUrl: String, warranty: String?, brand: String?, discount: String?,
                      imgUrl: String){

        addGoodsToCart(goodsCode, goodsName, goodsSubName, goodsPrice, salePrice, goodsStock,
            goodsFeatureUrl, warranty, brand, discount, imgUrl)
        calculateShoppingCartQuantity()

        val list: List<Map<String, String>>? = dbHelper?.let {
            ShoppingCarDbUtil.getShoppingCartAll(it)
        }
        var totalCount = 0
        if (list != null) {
            for (i in list.indices) {
                totalCount = totalCount + list[i]["count"]!!.toInt()
            }
        }
        goodsItemsLiveData.postValue(totalCount)
    }

    //計算購物車總數量
    private fun calculateShoppingCartQuantity() {
        val list: List<Map<String, String>>? = ShoppingCarDbUtil.getShoppingCartAll(dbHelper!!)
        var totalCount = 0
        for (i in list!!.indices) {
            totalCount = totalCount + list[i]["count"]!!.toInt()
        }
        totalCountLiveData.postValue(totalCount)
    }

    //商品加入購物車,寫入DB
    fun addGoodsToCart(goodsCode: String, goodsName: String, goodsSubName: String, goodsPrice: String, salePrice: String,
                       goodsStock: String, goodsFeatureUrl: String, warranty: String?, brand: String?, discount: String?,
                       imgUrl: String) {
        val list: List<Map<String, String>>? = dbHelper?.let { ShoppingCarDbUtil.getShoppingCart(it, goodsCode) }
        if (list!!.size > 0) {
            carCount = (list!![0][ShoopingCartEntry.COUNT]!!.toInt() + 1).toString()
            val _isChecked = list!![0][ShoopingCartEntry.IS_CHECKED].toString() //存放已在購物車內的商品

            carCount = (list[0]["count"]!!.toInt() + 1).toString()

            //商品在購物車存在就用update
            dbHelper?.let {
                ShoppingCarDbUtil.updateShoppingCar(
                    it,
                    goodsCode,
                    goodsName,
                    goodsSubName,
                    goodsPrice,
                    salePrice,
                    goodsStock,
                    goodsFeatureUrl,
                    warranty,
                    brand,
                    discount,
                    imgUrl,
                    carCount,
                    _isChecked,
                )
            }
        }
        else {
            //商品在購物車不存在就add
            val result: Long? = dbHelper?.let {
                ShoppingCarDbUtil.addShoppingCart(
                    it, goodsCode,
                    goodsName, goodsSubName, goodsPrice, salePrice, goodsStock,
                    goodsFeatureUrl, warranty, brand, discount, imgUrl, "1", isChecked)  //isChecked剛選入購物車的商品
            }
            resultLiveData.postValue(result)
        }
    }
}