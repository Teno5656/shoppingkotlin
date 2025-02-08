package com.example.shoppingkotlinmvvm

import android.content.Context
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppingkotlinmvvm.bean.RowData
import com.example.shoppingkotlinmvvm.httpClient.HttpUtil
import com.example.shoppingkotlinmvvm.httpClient.IHttpCallback
import com.example.shoppingkotlinmvvm.sqlite.ShoppingCarDbUtil
import com.example.shoppingkotlinmvvm.sqlite.ShoppingCartDatabaseHelper
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject

class MainViewModel : ViewModel() {

    var adapterChangeLiveData = MutableLiveData<Boolean>()
    var myPage = "1"
    var totalCnt = 100  //商品總筆數(預設100,之後會使用api回)
    var isAdapterChange: Boolean = true

    //產品列表
    var listLiveData = MutableLiveData<List<RowData.Data.Obj.GoodsInfo>>()
    var list = ArrayList<RowData.Data.Obj.GoodsInfo>()

    var loadStateLiveData = MutableLiveData<Int>()

    var dbHelper: ShoppingCartDatabaseHelper? = null
    var totalCountLiveData = MutableLiveData<Int>()

    fun start(context: Context){
        //撈API
        getData(myPage, null, false)
        dbHelper = ShoppingCartDatabaseHelper(context)
        calculateShoppingCartQuantity()
    }

    fun getData(page: String?, goodsName: String?, isClear: Boolean) {
        try {
            val jsonObject = JSONObject()
            jsonObject.put("page", page) //取的產品列表第幾頁
            if (goodsName != null) {
                jsonObject.put("goodsName", goodsName) //取得產品名稱
            }

            HttpUtil.httpPost(jsonObject, HttpUtil.GOODS_LIST_API,
                object : IHttpCallback {
                    override fun callback(data: String?) {
                        val gson = Gson()
                        val rowData = gson.fromJson(data, RowData::class.java)
                        println("bbb: $rowData")

                        totalCnt = rowData.data.obj.totalCnt
                        if (isClear) {
                            list.clear() //資料清空,不然會累加
                        }
                        list.addAll(rowData.data.obj.goodsInfoList)
                        listLiveData.postValue(list)
                    }
                }).toString()
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
    }

    fun adapterChange(){
        isAdapterChange = !isAdapterChange
        adapterChangeLiveData.postValue(isAdapterChange)
    }

    fun onLoadMore(goodsName: String?) {
        loadStateLiveData.postValue(IRecyclerViewAdapterLoad.LOADING)
        if (list.size < totalCnt) {
            //加載下一頁
            myPage = (myPage.toInt() + 1).toString()
            getData(myPage, goodsName, false)
            loadStateLiveData.postValue(IRecyclerViewAdapterLoad.LOADING_COMPLETE)
        } else {
            // 顯示加載到底的提示
            loadStateLiveData.postValue(IRecyclerViewAdapterLoad.LOADING_END)
        }
    }

    fun search(goodsName: String?){
        myPage = "1"
        getData(myPage, goodsName, true)
    }

    //購物車總數量統計
    private fun calculateShoppingCartQuantity() {
        val list: List<MutableMap<String, String>>? = dbHelper?.let {
            ShoppingCarDbUtil.getShoppingCartAll(it)
        }
        var totalCount = 0
        if (list != null) {
            for (i in list.indices) {
                totalCount = totalCount + list?.get(i)?.get("count")!!.toInt()
            }
        }
        totalCountLiveData.postValue(totalCount)
    }
}