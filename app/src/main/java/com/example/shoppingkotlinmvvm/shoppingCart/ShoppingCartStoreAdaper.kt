package com.example.shoppingkotlinmvvm.shoppingCart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingkotlinmvvm.databinding.ShoppingStoreBinding
import com.example.shoppingkotlinmvvm.sqlite.ShoopingCartEntry
import com.example.shoppingkotlinmvvm.sqlite.ShoppingCartDatabaseHelper

class ShoppingCartStoreAdaper(var storeList: List<MutableList<MutableMap<String, String>>>?, var goodsCallback: GoodsCallback, var dbHelper: ShoppingCartDatabaseHelper): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ShoppingCartStoreViewHolder(shoppingStoreBinding: ShoppingStoreBinding): RecyclerView.ViewHolder(shoppingStoreBinding.root), View.OnClickListener {
        val tvStoreName = shoppingStoreBinding.tvStoreName  //品牌名稱
        val rvGoods = shoppingStoreBinding.rvGoods  //RecyclerView
        override fun onClick(v: View?) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val shoppingStoreBinding = ShoppingStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingCartStoreViewHolder(shoppingStoreBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val recyclerViewHolder: ShoppingCartStoreViewHolder = holder as ShoppingCartStoreViewHolder
        recyclerViewHolder.tvStoreName.setText(storeList?.get(position)?.get(0)?.get(ShoopingCartEntry.BRAND))

        val itemAdaper = storeList?.get(position)?.let { ShoppingCartItemAdaper(it, goodsCallback, dbHelper) }
        recyclerViewHolder.rvGoods.setLayoutManager(LinearLayoutManager(holder.itemView.context))
        recyclerViewHolder.rvGoods.setAdapter(itemAdaper)
    }

    override fun getItemCount(): Int {
        return storeList!!.size
    }
}