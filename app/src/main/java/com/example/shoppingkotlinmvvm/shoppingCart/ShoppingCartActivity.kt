package com.example.shoppingkotlinmvvm.shoppingCart

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingkotlinmvvm.R
import com.example.shoppingkotlinmvvm.databinding.ShoppingMainBinding

class ShoppingCartActivity: AppCompatActivity(), GoodsCallback, View.OnClickListener{

    var viewModel: ShoppingCartViewModel? = null

    private var dialog: AlertDialog? = null

    private var adapter: ShoppingCartStoreAdaper? = null

    private lateinit var binding: ShoppingMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(ShoppingCartViewModel::class.java)

        binding = ShoppingMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel!!.init(this)  //呼叫viewModel的初始化
        viewModel!!.getAllBrand()  //初始化產品列表

        viewModel!!.allBrandGoodsListLiveData.observe(this) {
            adapter!!.notifyDataSetChanged()
        }

        adapter = ShoppingCartStoreAdaper(viewModel!!.allBrandGoodsList, this, viewModel!!.dbHelper!!)
        binding.rvStore!!.layoutManager = LinearLayoutManager(this)
        binding.rvStore.adapter = adapter

        binding.ivCheckedAll.setOnClickListener(this)  //全選
        binding.tvSettlement.setOnClickListener(this)  //結算

        //控制頁面全選
        viewModel!!.isAllCheckedLiveData.observe(this, androidx.lifecycle.Observer {isAllChecked->
            if (isAllChecked) {
                binding.ivCheckedAll!!.setImageDrawable(getDrawable(R.drawable.ic_check))  //不全選
                viewModel!!.calculationPrice()
            } else {
                binding.ivCheckedAll!!.setImageDrawable(getDrawable(R.drawable.ic_checked))  //全選
                viewModel!!.calculationPrice()
            }
        })

        viewModel!!.listItemChangeLiveData.observe(this){
            adapter!!.notifyItemChanged(it)
        }

        viewModel!!.checkIvCheckedAllStatus()

        //上方商品列表點擊後檢查全選是否打勾
        viewModel!!.isCheckedAllGoodsLiveData.observe(this){isAllChecked ->
            if (isAllChecked) {
                binding.ivCheckedAll.setImageDrawable(getDrawable(R.drawable.ic_checked))
            } else {
                binding.ivCheckedAll.setImageDrawable(getDrawable(R.drawable.ic_check))
            }
        }
        viewModel!!.calculationPrice()
        viewModel!!.totalPriceLiveData.observe(this){totalPrice ->
            binding.tvTotal!!.text = totalPrice.toString()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_checked_all ->
                viewModel!!.controlAllChecked()
            R.id.tv_settlement -> {
                if (!viewModel!!.isHaveGoods) {
                    showMsg("當前購物車空空如也~")
                    return
                }
                dialog = AlertDialog.Builder(this)
                        .setMessage("總計:" + viewModel!!.totalPrice + "元")
                        .setPositiveButton("確定") { dialog: DialogInterface?, which: Int ->  }
                        .setNegativeButton("取消") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                        .create()
                dialog!!.show()
            }
        }
    }

    //確認全選按鈕狀態
    //商品在購物車中全部選取,打勾
    //有商品未選取,取消打勾
    override fun checkIvCheckedAllStatus() {
        viewModel!!.checkIvCheckedAllStatus()
    }

    //商品價格
    override fun calculationPrice() {
        viewModel!!.calculationPrice()
    }

    override fun goodsNotifyDataSetChanged(brand: String) {
        viewModel!!.goodsNotifyDataSetChanged(brand)
    }

    private fun showMsg(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}