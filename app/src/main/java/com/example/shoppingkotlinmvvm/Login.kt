package com.example.shoppingkotlinmvvm

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.shoppingkotlinmvvm.databinding.LoginLayoutBinding


class Login: AppCompatActivity() {
    var viewModel: LoginViewModel? = null

    private val TAG = Login::class.java.simpleName
    private lateinit var binding: LoginLayoutBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        binding = LoginLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btConfirm.setOnClickListener {
        if (binding.testAct.text.toString().isEmpty() && binding.testPad.text.toString().isEmpty()) {  //檢查是否有輸入資料
            Toast.makeText(this@Login, "Please enter accountand password", Toast.LENGTH_SHORT).show()
        } else {
            viewModel!!.getLongInData(binding.edAccount.text.toString(), binding.edPassword.text.toString())
        }
    }
        viewModel!!.loginDataLiveData.observe(this) { loginData ->
            if (loginData.code.equals("00000")) {  //成功就換頁
                val intent = Intent(this@Login, MemberCentre::class.java)
                startActivity(intent) //開始執行換頁
            } else {
                runOnUiThread {
                    Toast.makeText(
                        this@Login,
                        loginData.msgInfo,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        //每次進入登入頁,輸入密碼時都是隱藏的
        val methodHide = PasswordTransformationMethod.getInstance()
        binding.testPad.setTransformationMethod(methodHide)

        //ImageButton監聽事件,密碼顯示(按住不放)與不顯示密碼
        binding.closePassword.setOnTouchListener { view, motionEvent ->
            if (motionEvent.getAction() === MotionEvent.ACTION_DOWN) {  //按住設定的圖示不放,顯示密碼
                binding.closePassword.setImageDrawable(getDrawable(R.drawable.open))
                binding.edPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
            } else if (motionEvent.getAction() === MotionEvent.ACTION_UP) {  //鬆開後,密碼隱藏
                binding.closePassword.setImageDrawable(getDrawable(R.drawable.hide))
                binding.edPassword.setTransformationMethod(PasswordTransformationMethod.getInstance())
            }
            return@setOnTouchListener false
        }

        //CheckBox監聽事件,密碼顯示與不顯示
        binding.showPassword.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //打勾就顯示密碼
                binding.edPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
            } else {
                //隱藏密碼
                binding.edPassword.setTransformationMethod(PasswordTransformationMethod.getInstance())
            }
        }
    }
}

