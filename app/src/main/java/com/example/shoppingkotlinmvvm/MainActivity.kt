package com.example.shoppingkotlinmvvm

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingkotlinmvvm.databinding.ActivityMainBinding
import com.example.shoppingkotlinmvvm.shoppingCart.ShoppingCartActivity
import java.util.*

class MainActivity : AppCompatActivity(), IRecyclerViewClickListener, View.OnClickListener,
    TextView.OnEditorActionListener, TextWatcher {

    var viewModel: MainViewModel? = null

    var adapter: RecyclerView.Adapter<*>? = null

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListener()

        //輸入完收鍵盤
        binding.edSearch.setImeOptions(EditorInfo.IME_ACTION_DONE)
        binding.edSearch.setSingleLine()

        //RecyclerView必要設定
        binding.recycler.setHasFixedSize(true)  //要設定固定大小

        //做直線式recycler,就用LinearLayoutManager
        binding.recycler.layoutManager = LinearLayoutManager(this)  //商品清單列表

        adapter = viewModel!!.list.let { GoodsListAdapter(it, this) }
        binding.recycler.adapter = adapter

        viewModel!!.adapterChangeLiveData.observe(this, androidx.lifecycle.Observer { isAdapterChange->
            if (isAdapterChange) {
                adapter = viewModel!!.list.let { GoodsListAdapter(it, this@MainActivity) }
                binding.recycler.setLayoutManager(LinearLayoutManager(this@MainActivity))
                binding.ibGoods.setBackground(getDrawable(R.drawable.list)) //圖示切換
                binding.recycler.setAdapter(adapter)
            } else {
                //grid adapter
                adapter = viewModel!!.list.let { GoodsGridAdapter(it, this@MainActivity, this@MainActivity) }
                binding.recycler.setLayoutManager(GridLayoutManager(this@MainActivity, 2)) //網格
                binding.ibGoods.setBackground(getDrawable(R.drawable.grid)) //圖示切換
                binding.recycler.setAdapter(adapter)
            }
        })

        binding.ibGoods.setOnClickListener { viewModel!!.adapterChange()}

        viewModel!!.listLiveData.observe(this, androidx.lifecycle.Observer {
            adapter!!.notifyDataSetChanged() //資料有改變了,請重新畫
        })

        viewModel!!.loadStateLiveData.observe(this, androidx.lifecycle.Observer {loadState->
            (adapter as IRecyclerViewAdapterLoad).setLoadState(loadState)
        })

        binding.recycler.addOnScrollListener(object : EndlessRecyclerOnScrollListener() {
            override fun onLoadMore() {
                viewModel!!.onLoadMore(binding.edSearch.text.toString())
            }
        })

        //購物車總數量
        viewModel!!.totalCountLiveData.observe(this, androidx.lifecycle.Observer {totalCount->
            if (totalCount > 0) {
                binding.tvNumber.setText(totalCount.toString())
                binding.tvNumber.setVisibility(View.VISIBLE)
            } else {
                binding.tvNumber.setVisibility(View.GONE)
            }
        })
    }

    fun initListener(){
        binding.layAccount.setOnClickListener(this) //會員中心
        binding.btAccount.setOnClickListener(this)
        binding.tvAccount.setOnClickListener(this)
        binding.btShoppingCar.setOnClickListener(this)  //購物車
        binding.tvShoppingCar.setOnClickListener(this)
        binding.ibGoodsSearch.setOnClickListener(this)
        binding.edSearch.setOnEditorActionListener(this)
        binding.edSearch.addTextChangedListener(this)
        binding.ibDelete.setOnClickListener(this)
        binding.ibReturn.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        viewModel!!.start(this)
    }

    override fun onClick(view: View?, position: Int) {
        val intent = Intent(this@MainActivity, ContentActivity::class.java)

        var warrantyStr = if(viewModel!!.list.get(position).warranty != null) viewModel!!.list.get(position).warranty else ""
        var brandStr = if(viewModel!!.list.get(position).brand != null) viewModel!!.list.get(position).brand else ""
        var discountStr = if(viewModel!!.list.get(position).discount != null) viewModel!!.list.get(position).discount else ""

        intent.putExtra("goodsName", viewModel!!.list.get(position).goodsName)  //商品名稱
        intent.putExtra("goodsSubName", viewModel!!.list.get(position).goodsSubName)
        intent.putExtra("goodsPrice",viewModel!!.list.get(position).goodsPrice) //商品價格
        intent.putExtra("salePrice", viewModel!!.list.get(position).SALE_PRICE)
        intent.putExtra("goodsCode", viewModel!!.list.get(position).goodsCode) //商品編號
        intent.putExtra("goodsStock", viewModel!!.list.get(position).goodsStock) //商品數量
        intent.putExtra("goodsFeatureUrl", viewModel!!.list.get(position).goodsFeatureUrl) //影片
        intent.putExtra("warranty", warrantyStr) //保固日期
        intent.putExtra("brand", brandStr) //品牌
        intent.putExtra("discount", discountStr) //特惠商品
        intent.putExtra("imgUrl", viewModel!!.list.get(position).imgUrl) //商品圖片

        intent.putStringArrayListExtra("imgUrlArray", viewModel!!.list.get(position).imgUrlArray as ArrayList<String?>?)
        startActivity(intent)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.lay_account, R.id.bt_account, R.id.tv_account -> {
                val intent = Intent(this@MainActivity, MemberCentre::class.java)
                startActivity(intent)
            }
            R.id.bt_shoppingCar, R.id.tv_shoppingCar -> {
                val intent = Intent(this@MainActivity, ShoppingCartActivity::class.java)
                startActivity(intent)
            }
            R.id.ib_goodsSearch ->{
                binding.ibReturn.setVisibility(View.VISIBLE)
                binding.ibDelete.setVisibility(View.VISIBLE)
                binding.edSearch.setVisibility(View.VISIBLE)
                binding.tvShopping.setVisibility(View.GONE)
                binding.ibGoodsSearch.setVisibility(View.GONE)
            }
            R.id.ib_delete ->{
                if (binding.edSearch.getEditableText().length > 0) {
                    binding.edSearch.getText().clear()
                }
            }
            R.id.ib_return ->{
                binding.ibReturn.setVisibility(View.GONE)
                binding.ibDelete.setVisibility(View.GONE)
                binding.edSearch.setVisibility(View.GONE)
                binding.tvShopping.setVisibility(View.VISIBLE)
                binding.ibGoodsSearch.setVisibility(View.VISIBLE)
                viewModel!!.search(null)
            }
        }
    }

    abstract class EndlessRecyclerOnScrollListener : RecyclerView.OnScrollListener() {
        //用來標記是否正在向上滑动
        private var isSlidingUpward = false

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            val manager = recyclerView.layoutManager as LinearLayoutManager?
            // 當不滑動時
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                //獲取最後一個完全顯示的itemPosition
                val lastItemPosition = manager!!.findLastCompletelyVisibleItemPosition()
                val itemCount = manager.itemCount

                // 判断是否滑动到了最後一個item,並且是向上滑動
                if (lastItemPosition == itemCount - 1 && isSlidingUpward) {
                    //加载更多
                    onLoadMore()
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            // 大於0表示正在向上滑動,小於等於0表示停止或向下滑動
            isSlidingUpward = dy > 0
        }

        /**
         * 加載更多回調
         */
        abstract fun onLoadMore()
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {  //按Enter
            viewModel!!.search(binding.edSearch.getText().toString())
        }
        return false
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {  //輸入框文字改變
        if (!binding.edSearch.getText().toString().isEmpty()) {
            binding.edSearch.setCompoundDrawables(null, null, null, null) //隱藏輸入框內放大鏡
        }
    }
}

