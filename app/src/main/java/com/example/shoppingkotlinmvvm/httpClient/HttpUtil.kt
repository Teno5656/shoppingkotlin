package com.example.shoppingkotlinmvvm.httpClient

import cz.msebera.android.httpclient.HttpResponse
import cz.msebera.android.httpclient.client.methods.HttpPost
import cz.msebera.android.httpclient.entity.StringEntity
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient
import cz.msebera.android.httpclient.protocol.HTTP
import cz.msebera.android.httpclient.util.EntityUtils
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.StandardCharsets

class HttpUtil {
    companion object{
        var URL = "http://ec2-54-243-12-19.compute-1.amazonaws.com:16888"  //server路徑

        var LOGIN_API = "/login"
        var GOODS_LIST_API = "/goodsList"

        @JvmStatic fun httpPost(jsonObject: JSONObject?, apiPath: String, httpCallback: IHttpCallback) {
            var jsonString = jsonObject.toString()
            val httpPost = HttpPost(URL + apiPath) //建立HttpPost物件並給予要連線的Url

            httpPost.setHeader(
                "Content-Type",
                "application/json;charset=utf-8"
            ) //設定Header,傳輸內容(body)是json格式,設定utf-8
            httpPost.setEntity(StringEntity(jsonString, StandardCharsets.UTF_8)) //使用post來傳送參數給Server

            Thread {
                var httpResponse: HttpResponse? = null

                try {
                    httpResponse = DefaultHttpClient().execute(httpPost) //取得Server回傳的HttpResponse

                    //解析回傳資料
                    val strResult = EntityUtils.toString(
                        httpResponse.getEntity(),
                        HTTP.UTF_8
                    )
                    println("bbb: $strResult")
                    httpCallback.callback(strResult)
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
            }.start()
        }
    }
}