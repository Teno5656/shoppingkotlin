package com.example.shoppingkotlinmvvm

interface IRecyclerViewAdapterLoad {
    companion object{  //JAVA的static
        val LOADING = 1 //正在加載

        val LOADING_COMPLETE = 2 //加載完成

        val LOADING_END = 3 //加載到底
    }
    fun setLoadState(loadState: Int)
}