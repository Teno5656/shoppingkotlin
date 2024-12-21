package com.example.shoppingkotlinmvvm

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.shoppingkotlinmvvm.bean.UserInfo
import com.example.shoppingkotlinmvvm.databinding.MemberCentreBinding
import com.example.shoppingkotlinmvvm.shoppingCart.ShoppingCartActivity

/**
 * 會員中心
 */
class MemberCentre : AppCompatActivity(),  View.OnClickListener {

    var viewModel: MemberCentreViewModel? = null
    private lateinit var binding: MemberCentreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(MemberCentreViewModel::class.java)

        binding = MemberCentreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //註冊頁
        binding.btRegister.setOnClickListener {
            val intent = Intent(this@MemberCentre, Register::class.java)
            startActivity(intent)
        }

        //登入頁
        binding.btLogin.setOnClickListener {
            val intent = Intent(this@MemberCentre, Login::class.java)
            startActivity(intent)
        }

        binding.layHome.setOnClickListener(this)
        binding.btHome.setOnClickListener(this) //首頁
        binding.tvHome.setOnClickListener(this)
        binding.btShoppingCar.setOnClickListener(this) //購物車
        binding.tvShoppingCar.setOnClickListener(this)

        //是否有被登入
        if (UserInfo.userId != null) {
            //已登入
            binding.btRegister.setVisibility(View.GONE)
            binding.btLogin.setVisibility(View.GONE)
            binding.tvUserId.setText(UserInfo.userId)
            binding.tvMember.setVisibility(View.VISIBLE)
            binding.tvUserId.setVisibility(View.VISIBLE)
        }

        viewModel!!.totalCountLiveData.observe(this, androidx.lifecycle.Observer {totalCount->
            if (totalCount > 0) {
                binding.tvNumber.setText(totalCount.toString())
                binding.tvNumber.setVisibility(View.VISIBLE)
            } else {
                binding.tvNumber.setVisibility(View.GONE)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        viewModel!!.start(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.lay_home, R.id.bt_home, R.id.tv_home -> {
                val intent = Intent(this@MemberCentre, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.bt_shoppingCar, R.id.tv_shoppingCar -> {
                val intent1 = Intent(this@MemberCentre, ShoppingCartActivity::class.java)
                startActivity(intent1)
            }
        }
    }
}