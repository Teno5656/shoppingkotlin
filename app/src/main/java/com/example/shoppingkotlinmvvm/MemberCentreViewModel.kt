package com.example.shoppingkotlinmvvm

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppingkotlinmvvm.sqlite.ShoppingCarDbUtil
import com.example.shoppingkotlinmvvm.sqlite.ShoppingCartDatabaseHelper

class MemberCentreViewModel : ViewModel(){

    var dbHelper: ShoppingCartDatabaseHelper? = null
    var totalCountLiveData = MutableLiveData<Int>()

    fun start(context: Context){
        dbHelper = ShoppingCartDatabaseHelper(context)
        calculateShoppingCartQuantity()
    }

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