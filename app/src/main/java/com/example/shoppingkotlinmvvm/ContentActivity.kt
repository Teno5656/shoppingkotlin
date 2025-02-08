package com.example.shoppingkotlinmvvm

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.LayoutParams
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.shoppingkotlinmvvm.databinding.ConActivityBinding
import com.example.shoppingkotlinmvvm.shoppingCart.ShoppingCartActivity
import com.example.shoppingkotlinmvvm.sqlite.ShoppingCartDatabaseHelper

class ContentActivity: AppCompatActivity() {

    var viewModel: ContentViewModel? = null  //先取得viewModel

    private lateinit var binding: ConActivityBinding

    private var adapter: PagerAdapter? = null
    private var imgUrlList: ArrayList<String>? = null  //api圖片網址資料
    private var imageView: ArrayList<ImageView>? = null  //放圖片(api網址轉成圖片)

    //包括點點的layout
    private var imageDot: ImageView? = null

    //定義一組imageView數組,來存放生成的小圓點
    private var imageViews: Array<ImageView?>? = null

    var goodsName = "GoodsName not set"
    var goodsSubName = "goodsSubName"
    var goodsPrice = "goodsPrice"
    var salePrice = "salePrice"
    var goodsCode = "goodsCode"
    var goodsStock = "goodsStock"
    var goodsFeatureUrl = "goodsFeatureUrl"
    var warranty :String? = "warranty"
    var brand :String? = "brand"
    var discount :String? = "discount"
    var imgUrl = "imgUrl"

    var dbHelper: ShoppingCartDatabaseHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(ContentViewModel::class.java)

        binding = ConActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ibCart.setOnClickListener {  //購物車
            val intent = Intent(this@ContentActivity, ShoppingCartActivity::class.java)
            startActivity(intent)
        }

        binding.btAddBuy.setOnClickListener {  //直接購買
            viewModel!!.addGoodsToCart(goodsCode, goodsName, goodsSubName, goodsPrice, salePrice, goodsStock,
                goodsFeatureUrl, warranty, brand, discount, imgUrl) //呼叫viewModel的fun
            val intent = Intent(this@ContentActivity, ShoppingCartActivity::class.java)
            startActivity(intent)
        }

        binding.btAddCart.setOnClickListener {  //加入購物車
            viewModel!!.addGoodsCount(goodsCode, goodsName, goodsSubName, goodsPrice, salePrice, goodsStock,
                goodsFeatureUrl, warranty, brand, discount, imgUrl)
        }

        viewModel!!.goodsItemsLiveData.observe(this){totalCount -> //購物車數量
            if (totalCount > 0) {
                binding.tvNumber.text = totalCount.toString()
                binding.tvNumber.visibility = View.VISIBLE
            } else {
                binding.tvNumber.visibility = View.GONE
            }
        }

        dbHelper = ShoppingCartDatabaseHelper(this)

        val extras = intent.extras
        if (extras != null) {
            goodsName = extras.getString("goodsName")!!
            goodsSubName = extras.getString("goodsSubName")!!
            goodsPrice = extras.getString("goodsPrice")!!
            salePrice = extras.getString("salePrice")!!
            goodsCode = extras.getString("goodsCode")!!
            goodsStock = extras.getString("goodsStock")!!
            goodsFeatureUrl = extras.getString("goodsFeatureUrl")!!
            warranty = extras.getString("warranty")
            brand = extras.getString("brand")
            discount = extras.getString("discount")
            imgUrl = extras.getString("imgUrl")!!
        }

        binding.tvGoodsName.text = goodsName //接收api的goodsName(商品名稱)
        binding.tvGoodsSubname.text = goodsSubName //折扣
        binding.tvGoodsPrice.text = goodsPrice //接收api的goodsPrice(商品價格)
        binding.tvGoodsCode.text = goodsCode //接收api的goodsCode(商品編號)
        binding.tvGoodsStock.text = goodsStock //接收api的goodsStock(商品數量)
        binding.webView.settings.javaScriptEnabled = true //設置支持webView(js)顯示
        binding.webView.loadUrl(goodsFeatureUrl) //接收api的goodsFeatureUrl(商品影片)
        imgUrlList = intent.getStringArrayListExtra("imgUrlArray") //接收api圖片
        binding.tvWarranty.text = warranty //保固日期
        binding.tvBrand.text = brand //品牌
        binding.tvDiscount.text = discount //特惠商品

        imageView = ArrayList() //放圖片(api網址轉成圖片)

        for (i in imgUrlList!!.indices) {
            val iView = ImageView(this)

            iView.layoutParams = LayoutParams()

            val metrics = DisplayMetrics()  //抓螢幕大小的解析度
            windowManager.defaultDisplay.getMetrics(metrics)

            iView.layoutParams.width = metrics.widthPixels
            iView.layoutParams.height = metrics.heightPixels / 2

            iView.load(imgUrlList!![i]) {
                placeholder(R.drawable.picture) //設定預設圖片
                error(R.drawable.picture) // Error image in case of loading failure
                transformations(RoundedCornersTransformation())  //邊緣圓角的特性
            }

            iView.adjustViewBounds = true
            iView.scaleType = ImageView.ScaleType.CENTER_INSIDE

            imageView!!.add(iView)
        }
        initEvent()
        initPointer()

        viewModel!!.totalCountLiveData.observe(this, androidx.lifecycle.Observer {totalCount->
            if (totalCount > 0) {
                binding.tvNumber.setText(totalCount.toString())
                binding.tvNumber.setVisibility(View.VISIBLE)
            } else {
                binding.tvNumber.setVisibility(View.GONE)
            }
        })

        viewModel!!.resultLiveData.observe(this, androidx.lifecycle.Observer {result ->
            if (result == 0L) {
                Toast.makeText(this, "寫入SQLite失敗", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        viewModel!!.start(this)
    }

    //為控件綁定事件,綁定適配器
    private fun initEvent() {
        adapter = imageView?.let { MyPagerAdapter(it) }
        binding.viewPager!!.adapter = adapter
        binding.viewPager.addOnPageChangeListener(GuidePageChangeListener())
    }

    private fun initPointer() {
        imageViews = arrayOfNulls<ImageView>(imgUrlList!!.size)
        for (i in imageViews!!.indices) {
            //對LinearLayout的設定
            val params = LinearLayout.LayoutParams(25, 25) //圓點的大小
            params.setMargins(10, 0, 10, 0) //圓點的間距
            imageDot = ImageView(this) //(放點點的)初始化
            imageDot!!.setLayoutParams(params)
            imageViews!![i] = imageDot
            if (i == 0) {
                imageViews!![i]?.setBackgroundResource(R.drawable.dot_back) //表示當前圖片
            } else {
                imageViews!![i]?.setBackgroundResource(R.drawable.dot_grey)
            }
            binding.viewGroup!!.addView(imageViews!![i])
        }
    }

    //初始化ViewPager
    inner class MyPagerAdapter(var viewPages: ArrayList<ImageView>) : PagerAdapter() {
        //獲取當前界面數量
        override fun getCount(): Int {
            return viewPages.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(imageView!![position])
        }

        //返回一個dot,這個dot顯示了PagerAdapter選擇哪個放在當前的ViewPager中
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            container.addView(viewPages[position])
            return viewPages[position]
        }
    }

    //ViewPager的OnPageChangeListener監聽事件,當ViewPager的page頁發生變化的時候調用
    inner class GuidePageChangeListener : OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        //頁面滑動完成後執行
        override fun onPageSelected(position: Int) {
            //判斷當前是在哪個page,就把對應下標的ImageView原點設置為選中狀態的圖片
            for (i in imageViews!!.indices) {
                imageViews!![position]?.setBackgroundResource(R.drawable.dot_back)
                if (position != i) {
                    imageViews!![i]?.setBackgroundResource(R.drawable.dot_grey)
                }
            }
        }

        //監聽頁面狀態,0-靜止,1-滑動,2-滑動完成
        override fun onPageScrollStateChanged(state: Int) {

        }
    }

    override fun onDestroy() {  //關閉連線
        dbHelper!!.close()
        super.onDestroy()
    }
}