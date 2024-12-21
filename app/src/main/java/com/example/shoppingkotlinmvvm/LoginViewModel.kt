package com.example.shoppingkotlinmvvm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppingkotlinmvvm.bean.LogInData
import com.example.shoppingkotlinmvvm.bean.UserInfo
import com.example.shoppingkotlinmvvm.httpClient.HttpUtil
import com.example.shoppingkotlinmvvm.httpClient.IHttpCallback
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject

class LoginViewModel : ViewModel(){

    var loginDataLiveData = MutableLiveData<LogInData>()

    fun getLongInData(account: String?, password: String?) {

        try {
            val jsonObject = JSONObject()
            jsonObject.put("acc", account)
            jsonObject.put("pw", password)
            jsonObject.toString()

            HttpUtil.httpPost(jsonObject, HttpUtil.LOGIN_API,
                object : IHttpCallback {
                    override fun callback(data: String?) {
                        val gson = Gson()
                        val logInData: LogInData = gson.fromJson(data, LogInData::class.java)
                        if (logInData.code.equals("00000")) {  //成功就換頁
                            UserInfo.userId = logInData.data.obj.userId
                            UserInfo.account = logInData.data.obj.account
                            UserInfo.pwd = logInData.data.obj.pwd
                            UserInfo.name = logInData.data.obj.name
                        } else {

                        }
                        loginDataLiveData.postValue(logInData)
                    }
                })
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
    }

    private fun callback(data: String) {

    }
}