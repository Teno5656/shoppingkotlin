package com.example.shoppingkotlinmvvm.shoppingCart

/**
 * 商品回調接口
 */
interface GoodsCallback {
    /**
     * 計算價格
     */
    fun calculationPrice()

    /**
    * 更新購物車adapter
    */
    fun goodsNotifyDataSetChanged(brand: String)

    fun checkIvCheckedAllStatus()
}