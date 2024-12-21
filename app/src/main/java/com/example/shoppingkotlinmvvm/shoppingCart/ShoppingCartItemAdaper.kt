package com.example.shoppingkotlinmvvm.shoppingCart

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.shoppingkotlinmvvm.R
import com.example.shoppingkotlinmvvm.databinding.ShoppingItemBinding
import com.example.shoppingkotlinmvvm.sqlite.ShoopingCartEntry
import com.example.shoppingkotlinmvvm.sqlite.ShoppingCarDbUtil
import com.example.shoppingkotlinmvvm.sqlite.ShoppingCartDatabaseHelper


class ShoppingCartItemAdaper(var itemList: MutableList<MutableMap<String, String>>, var goodsCallback: GoodsCallback, var dbHelper: ShoppingCartDatabaseHelper): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    inner class ShoppingCartItemViewHolder(shoppingItemBinding: ShoppingItemBinding): RecyclerView.ViewHolder(shoppingItemBinding.root), View.OnClickListener {
        var ivGoods = shoppingItemBinding.ivGoods
        var tvGoodsName = shoppingItemBinding.tvGoodsName  //商品名稱
        var tvGoodsPrice = shoppingItemBinding.tvGoodsPrice  //商品價格
        var tvGoodsNum = shoppingItemBinding.tvGoodsNum  // 1
        var tvIncreaseGoodsNum = shoppingItemBinding.tvIncreaseGoodsNum  // +
        var tvReduceGoodsNum = shoppingItemBinding.tvReduceGoodsNum  // -
        var ivCheckedGoods = shoppingItemBinding.ivCheckedGoods

        override fun onClick(v: View?) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val shoppingItemBinding = ShoppingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingCartItemViewHolder(shoppingItemBinding)
    }

    @SuppressLint("UseCompatLoadingForDrawables", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val relativeViewHolder: ShoppingCartItemViewHolder = holder as ShoppingCartItemViewHolder //轉成客製化的Holder
        relativeViewHolder.tvGoodsName.setText(itemList[position][ShoopingCartEntry.GOODS_NAME]) //更新商品名稱
        relativeViewHolder.tvGoodsPrice.setText(itemList[position][ShoopingCartEntry.GOODS_PRICE]) //更新商品價格
        relativeViewHolder.tvGoodsNum.setText(itemList[position][ShoopingCartEntry.COUNT]) //更新商品數量
        relativeViewHolder.ivGoods.load(itemList.get(position).get(ShoopingCartEntry.IMG_URL)) {
            placeholder(R.drawable.picture) //設定預設圖片
            error(R.drawable.picture) // Error image in case of loading failure
            transformations(RoundedCornersTransformation())  //邊緣圓角的特性
        }

        val localPosition = position

        relativeViewHolder.tvIncreaseGoodsNum.setOnClickListener { //呼叫ShoppingCartActivity的click
            val count = itemList[localPosition][ShoopingCartEntry.COUNT]
            if (count!!.toInt() < itemList[localPosition][ShoopingCartEntry.GOODS_STOCK]!!.toInt()) {
                //不能超過商品總共數量就可以加
                itemList[localPosition].put(ShoopingCartEntry.COUNT, (count.toInt() + 1).toString())
                countChange(localPosition)
                relativeViewHolder.tvGoodsNum.text = itemList[localPosition][ShoopingCartEntry.COUNT]
            } else {
                //+號變成灰色不能再按
                relativeViewHolder.tvIncreaseGoodsNum.setTextColor(
                    Color.parseColor("#BEBEBE"))
            }
            goodsCallback.calculationPrice()
        }

        relativeViewHolder.tvReduceGoodsNum.setOnClickListener {
            var count = itemList[localPosition][ShoopingCartEntry.COUNT]

            if (count!!.toInt() > 0) {
                count = (count!!.toInt() - 1).toString()
                itemList[localPosition][ShoopingCartEntry.COUNT] = count
                countChange(localPosition)
                relativeViewHolder.tvGoodsNum.text = itemList[localPosition][ShoopingCartEntry.COUNT]
                if (count.toInt() <= 0) {
                    //刪除資料庫商品
                    ShoppingCarDbUtil.deleteShoppingCartItem(dbHelper, itemList[localPosition][ShoopingCartEntry.GOODS_CODE]!!)
                    val brand = itemList[localPosition][ShoopingCartEntry.BRAND]

                    //商品列刪除
                    itemList.removeAt(localPosition)
                    notifyDataSetChanged() //recyclerView刷新

                    if (itemList.size == 0) {
                        //刪除品牌
                        if (brand != null) {
                            goodsCallback.goodsNotifyDataSetChanged(brand)
                        }
                    }
                }
            } else {  //防呆(正常不會進來)
                //刪除資料庫商品
                ShoppingCarDbUtil.deleteShoppingCartItem(dbHelper, itemList[localPosition][ShoopingCartEntry.GOODS_CODE]!!)
                val brand = itemList[localPosition][ShoopingCartEntry.BRAND]
                //商品列刪除
                itemList.drop(localPosition)
                notifyDataSetChanged() //recyclerView刷新

                if (itemList.size == 0) {
                    //刪除品牌
                    if (brand != null) {
                        goodsCallback.goodsNotifyDataSetChanged(brand)
                    }
                }
            }
            goodsCallback.calculationPrice()
        }

        //每次進購物車畫面初始化
        if (itemList[localPosition][ShoopingCartEntry.IS_CHECKED] == "0") {
            // 0:未選中
            relativeViewHolder.ivCheckedGoods.setImageDrawable(relativeViewHolder.ivCheckedGoods.context.getDrawable(R.drawable.ic_check))
        } else {
            // 1:已選中
            relativeViewHolder.ivCheckedGoods.setImageDrawable(relativeViewHolder.ivCheckedGoods.context.getDrawable(R.drawable.ic_checked))
        }

        relativeViewHolder.ivCheckedGoods.setOnClickListener { v ->
            if (itemList[localPosition][ShoopingCartEntry.IS_CHECKED] == "1") {
                // 0:未選中
                relativeViewHolder.ivCheckedGoods.setImageDrawable(v.context.getDrawable(R.drawable.ic_check))
                itemList[localPosition].put(ShoopingCartEntry.IS_CHECKED, "0")
                countChange(localPosition)
            } else {
                // 1:已選中
                relativeViewHolder.ivCheckedGoods.setImageDrawable(v.context.getDrawable(R.drawable.ic_checked))
                itemList[localPosition].put(ShoopingCartEntry.IS_CHECKED, "1")
                countChange(localPosition)
            }
            goodsCallback.calculationPrice()
            goodsCallback.checkIvCheckedAllStatus()
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun countChange(localPosition: Int) {
        ShoppingCarDbUtil.updateShoppingCar(
            dbHelper,
            itemList.get(localPosition).get(ShoopingCartEntry.GOODS_CODE),
            itemList.get(localPosition).get(ShoopingCartEntry.GOODS_NAME),
            itemList.get(localPosition).get(ShoopingCartEntry.GOODS_SUB_NAME),
            itemList.get(localPosition).get(ShoopingCartEntry.GOODS_PRICE),
            itemList.get(localPosition).get(ShoopingCartEntry.SALE_PRICE),
            itemList.get(localPosition).get(ShoopingCartEntry.GOODS_STOCK),
            itemList.get(localPosition).get(ShoopingCartEntry.GOODS_FEATURE_URL),
            itemList.get(localPosition).get(ShoopingCartEntry.WARRANTY),
            itemList.get(localPosition).get(ShoopingCartEntry.BRAND),
            itemList.get(localPosition).get(ShoopingCartEntry.DISCOUNT),
            itemList.get(localPosition).get(ShoopingCartEntry.IMG_URL),
            itemList.get(localPosition).get(ShoopingCartEntry.COUNT),
            itemList.get(localPosition).get(ShoopingCartEntry.IS_CHECKED)
        )
    }
}