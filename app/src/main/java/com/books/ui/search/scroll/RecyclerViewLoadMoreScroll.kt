package com.books.ui.search.scroll

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewLoadMoreScroll(layoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {

    private var visibleThreshold = 10
    private lateinit var mOnLoadMoreListener: OnLoadMoreListener
    private lateinit var topToScrollListener: OnTopToScrollListener
    private var isLoading: Boolean = false
    private var lastVisibleItem: Int = 0
    private var firstVisibleItem: Int = 0
    private var totalItemCount: Int = 0
    private var mLayoutManager: RecyclerView.LayoutManager = layoutManager

    fun setLoaded() {
        isLoading = false
    }

    fun getLoaded(): Boolean {
        return isLoading
    }

    fun setOnLoadMoreListener(mOnLoadMoreListener: OnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener
    }

    fun setOnVisibleTopToScrollButtonListener(topToScrollListener: OnTopToScrollListener) {
        this.topToScrollListener = topToScrollListener
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        firstVisibleItem =
            (mLayoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        lastVisibleItem =
            (mLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()

        if (dy <= 0) {
            if (firstVisibleItem == 0) {
                topToScrollListener.onInvisibleTopToScrollButton()
            }
        } else {
            topToScrollListener.onVisibleTopToScrollButton()
            totalItemCount = mLayoutManager.itemCount

            if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
                mOnLoadMoreListener.onLoadMore()
                isLoading = true
            }
        }
    }
}

interface OnLoadMoreListener {
    fun onLoadMore()
}

interface OnTopToScrollListener {
    fun onVisibleTopToScrollButton()
    fun onInvisibleTopToScrollButton()
}