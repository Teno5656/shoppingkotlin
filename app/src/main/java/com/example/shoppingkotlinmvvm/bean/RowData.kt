package com.example.shoppingkotlinmvvm.bean

data class RowData(
    val code: String,
    val resultType: String,
    val msgInfo: String,
    val `data`: Data
){
    data class Data(
        val obj: Obj
    ){
        data class Obj(
            val totalCnt: Int,
            val curPage: Int,
            val goodsInfoList: List<GoodsInfo>,
            val minPage: Int,
            val maxPage: Int,
            val curPageGoodsCnt: Int
        ){
            data class GoodsInfo(
                val vodUrl: String,
                val goodsSubName: String,
                val discount: String,
                val goodsStock: String,
                val imgUrl: String,
                val SALE_PRICE: String,
                val goodsFeatureUrl: String,
                val goodsPrice: String,
                val imgUrlArray: List<String>,
                val warranty: String,
                val goodsCode: String,
                val goodsName: String,
                val brand: String,
                val goodsUrl: String,
                val imgTagUrl: String,
                val imgTypeUrlArray: List<String>,
                val externalImgUrlArray: List<String>,
                val action: Action
            ){
                data class Action(
                    val actionType: Int,
                    val actionValue: String,
                    val useDefault: Boolean,
                    val extraValue: ExtraValue
                ){
                    data class ExtraValue(
                        val isWebPage: String,
                        val urlParameter: String
                    )
                }
            }
        }
    }
}