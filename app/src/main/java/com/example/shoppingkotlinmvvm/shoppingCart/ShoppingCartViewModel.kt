package com.example.shoppingkotlinmvvm.shoppingCart

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppingkotlinmvvm.sqlite.ShoopingCartEntry
import com.example.shoppingkotlinmvvm.sqlite.ShoppingCarDbUtil
import com.example.shoppingkotlinmvvm.sqlite.ShoppingCartDatabaseHelper

class ShoppingCartViewModel : ViewModel() {
    var dbHelper: ShoppingCartDatabaseHelper? = null
    //<brand(品牌), cartList(商品列表)>
    var map: MutableMap<String, MutableList<MutableMap<String, String>>>? = null
    var list: List<MutableMap<String, String>>? = null

    //<brand(品牌list) < cartList(商品列表)>>
    var allBrandGoodsList: MutableList<MutableList<MutableMap<String, String>>>? = null
    var allBrandGoodsListLiveData = MutableLiveData<MutableList<MutableList<MutableMap<String, String>>>>()

    var isAllChecked = false //是否全選
    var isAllCheckedLiveData = MutableLiveData<Boolean>()

    var listItemChangeLiveData = MutableLiveData<Int>()

    var isCheckedAllGoodsLiveData = MutableLiveData<Boolean>()

    var isHaveGoods = false //購物車是否有商品
    var totalPrice = 0 //商品總價
    var totalPriceLiveData = MutableLiveData<Int>()

    fun init(context: Context){
        dbHelper = ShoppingCartDatabaseHelper(context)
        //<brand(品牌), cartList(商品列表)>
        map = mutableMapOf()
        list = ShoppingCarDbUtil.getShoppingCartAll(dbHelper!!) //取DB資料丟給list
    }

    fun getAllBrand(){
        for (i in list!!.indices) {
            val brand = list!![i][ShoopingCartEntry.BRAND]
            if (map!!.containsKey(brand)) {
                val brandList = map!![brand]
                brandList!!.add(list!![i])
            } else {
                val brandList: MutableList<MutableMap<String, String>> = ArrayList()
                brandList.add(list!![i])
                map!![brand!!] = brandList
            }
        }
        allBrandGoodsList = map!!.values.toMutableList()
        allBrandGoodsListLiveData.postValue(allBrandGoodsList)
    }

    //控制頁面全選
    fun controlAllChecked() {
        isAllCheckedLiveData.postValue(isAllChecked)
        if (isAllChecked) {
            selectAllUpdate("0")  //不全選
            isAllChecked = false
        } else {
            selectAllUpdate("1")  //全選
            isAllChecked = true
        }
    }

    fun selectAllUpdate(clickState: String?) {
        for (i in allBrandGoodsList!!.indices) {
            for(j in allBrandGoodsList!!.get(i).indices){
                if (clickState != null) {  //判斷不等於null才做事，防呆
                    allBrandGoodsList!!.get(i).get(j).put(ShoopingCartEntry.IS_CHECKED, clickState)
                } //對List資料做更新
                countChange(allBrandGoodsList!!.get(i).get(j)) //對DB資料做更新
            }
            listItemChangeLiveData.postValue(i)
        }
    }

    //確認全選按鈕狀態
    //商品在購物車中全部選取,打勾
    //有商品未選取,取消打勾
    fun checkIvCheckedAllStatus() {
        var isCheckedAllGoods = true
        for (i in list!!.indices) {
            val checked = list!![i][ShoopingCartEntry.IS_CHECKED]
            if (checked == "0") {  //有一個產品沒選中就false
                isCheckedAllGoods = false
            }
        }
        isAllChecked = isCheckedAllGoods
        isCheckedAllGoodsLiveData.postValue(isAllChecked)
    }

    //商品價格
    fun calculationPrice() {
        totalPrice = 0
        for (i in list!!.indices) {
            val isChecked = list!![i][ShoopingCartEntry.IS_CHECKED]
            if (isChecked == "1") {
                //有選中商品時計算總價格
                val totalCount = list!![i][ShoopingCartEntry.COUNT]!!.toInt()
                val price = list!![i][ShoopingCartEntry.SALE_PRICE]!!.toInt()
                totalPrice = totalPrice + totalCount * price
            }
        }
        //只要選中一個商品就成立
        isHaveGoods = false

        for (i in list!!.indices) {
            val isChecked = list!![i][ShoopingCartEntry.IS_CHECKED]
            if (isChecked == "1") {
                //有選中商品時就顯示視窗計算總價格
                isHaveGoods = true
                break
            }
        }
        totalPriceLiveData.postValue(totalPrice)
    }

    fun goodsNotifyDataSetChanged(brand: String) {
        map?.remove(brand)
        val iter: MutableListIterator<MutableList<MutableMap<String, String>>>? = allBrandGoodsList?.listIterator()
        if (iter != null) {
            while (iter.hasNext()) {  //有沒有下一筆資料
                val list = iter.next() //抓下一筆資料
                if (list.size == 0) {
                    iter.remove()
                }
            }
        }
        allBrandGoodsListLiveData.postValue(allBrandGoodsList)
    }

    fun countChange(goodsMap: MutableMap<String, String>) {
        ShoppingCarDbUtil.updateShoppingCar(
            dbHelper!!,
            goodsMap[ShoopingCartEntry.GOODS_CODE],
            goodsMap[ShoopingCartEntry.GOODS_NAME],
            goodsMap[ShoopingCartEntry.GOODS_SUB_NAME],
            goodsMap[ShoopingCartEntry.GOODS_PRICE],
            goodsMap[ShoopingCartEntry.SALE_PRICE],
            goodsMap[ShoopingCartEntry.GOODS_STOCK],
            goodsMap[ShoopingCartEntry.GOODS_FEATURE_URL],
            goodsMap[ShoopingCartEntry.WARRANTY],
            goodsMap[ShoopingCartEntry.BRAND],
            goodsMap[ShoopingCartEntry.DISCOUNT],
            goodsMap[ShoopingCartEntry.IMG_URL],
            goodsMap[ShoopingCartEntry.COUNT],
            goodsMap[ShoopingCartEntry.IS_CHECKED]
        )
    }
}