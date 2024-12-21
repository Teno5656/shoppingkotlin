package com.example.shoppingkotlinmvvm.bean

data class LogInData(
    val code: String,
    val resultType: String,
    val msgInfo: String,
    val `data`: Data
){
    data class Data(
        val obj: Obj
    ){
        data class Obj(
            val userId: String,
            val account: String,
            val pwd: String,
            val name: String
        )
    }
}