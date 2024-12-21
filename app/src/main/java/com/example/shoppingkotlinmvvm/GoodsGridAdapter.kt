package com.example.shoppingkotlinmvvm

import android.app.Activity
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.shoppingkotlinmvvm.bean.RowData
import com.example.shoppingkotlinmvvm.databinding.GoodsGridAdaperBinding
import com.example.shoppingkotlinmvvm.databinding.RefreshFooterBinding

//設定傳入所有的商品資料
class GoodsGridAdapter(var goodsGridList: List<RowData.Data.Obj.GoodsInfo>, var listener: IRecyclerViewClickListener, var activity: Activity): RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    IRecyclerViewAdapterLoad{

    val TYPE_ITEM = 1 //普通布局

    val TYPE_FOOTER = 2 //頁尾布局

    private var loadState = 2 //當前加載狀態,默認為加載完成

    var windowSize = 0

    inner class GoodsGridViewHolder(goodsGridAdaperBinding: GoodsGridAdaperBinding): RecyclerView.ViewHolder(goodsGridAdaperBinding.root), View.OnClickListener {
        val igGoods = goodsGridAdaperBinding.igGoods  //商品圖片
        val tvProduct = goodsGridAdaperBinding.tvProduct  //商品介紹
        val tvPrice = goodsGridAdaperBinding.tvPrice  //商品價格
        init {
            goodsGridAdaperBinding.root.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            listener.onClick(v, adapterPosition)  //換頁進ContentActivity
        }
    }

    inner class FootViewHolder(refreshFooterBinding: RefreshFooterBinding): RecyclerView.ViewHolder(refreshFooterBinding.root) {
        val pbLoading = refreshFooterBinding.pbLoading
        val tvLoading = refreshFooterBinding.tvLoading
        val llEnd = refreshFooterBinding.llEnd
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == TYPE_ITEM){
            val goodsGridAdaperBinding = GoodsGridAdaperBinding.inflate(LayoutInflater.from(parent.context), parent, false)

            //非Activity取得螢幕解析度
            val metric = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(metric)
            val height = metric.heightPixels // 螢幕高度（像素）
            goodsGridAdaperBinding.root.getLayoutParams().height = (height.toDouble() / 2.7).toInt()
            return  GoodsGridViewHolder(goodsGridAdaperBinding)
        }else{
            val refreshFooterBinding = RefreshFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return  FootViewHolder(refreshFooterBinding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is GoodsGridAdapter.GoodsGridViewHolder) {
            goodsGridList?.run {
                val goods = goodsGridList.get(position)
                holder.igGoods.getLayoutParams().width = windowSize  //設定image寬度
                holder.tvProduct.setText(goods.goodsName)  //得到商品名稱
                holder.tvPrice.setText(goods.goodsPrice)  //得到商品價格

                holder.igGoods.load(goods.imgUrl) {
                    placeholder(R.drawable.picture) //設定預設圖片
                    error(R.drawable.picture) // Error image in case of loading failure
                    transformations(RoundedCornersTransformation())  //邊緣圓角的特性
                }
            }
        }else if (holder is GoodsGridAdapter.FootViewHolder){  //到底的畫面
            val footViewHolder: GoodsGridAdapter.FootViewHolder = holder as GoodsGridAdapter.FootViewHolder
            when (loadState) {
                IRecyclerViewAdapterLoad.LOADING -> {  // 正在加載
                    footViewHolder.pbLoading.setVisibility(View.VISIBLE)
                    footViewHolder.tvLoading.setVisibility(View.VISIBLE)
                    footViewHolder.llEnd.setVisibility(View.GONE)
                }
                IRecyclerViewAdapterLoad.LOADING_COMPLETE -> {  // 加載完成
                    footViewHolder.pbLoading.setVisibility(View.INVISIBLE)
                    footViewHolder.tvLoading.setVisibility(View.INVISIBLE)
                    footViewHolder.llEnd.setVisibility(View.GONE)
                }
                IRecyclerViewAdapterLoad.LOADING_END -> {  // 加載到底
                    footViewHolder.pbLoading.setVisibility(View.GONE)
                    footViewHolder.tvLoading.setVisibility(View.GONE)
                    footViewHolder.llEnd.setVisibility(View.VISIBLE)
                }
                else -> {}
            }
        }
    }

    override fun getItemCount(): Int {
        return goodsGridList.size + 1  //取數量
    }

    override fun getItemViewType(position: Int): Int {
        // 最後一個item設置為FooterView
        return if (position + 1 == itemCount) {
            TYPE_FOOTER
        } else {
            TYPE_ITEM
        }
    }

    /**
     * 設置上拉加載狀態
     *
     * @param loadState 0.正在加載 1.加載完成 2.加載到底
     */
    override fun setLoadState(loadState: Int): Unit {
        this.loadState = loadState
        notifyDataSetChanged() //畫面更新
    }
}