package com.example.shoppingkotlinmvvm

import android.view.View

interface IRecyclerViewClickListener {
    fun onClick(view: View?, position: Int)
}