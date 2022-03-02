package com.books.ui.search.scroll

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewLoadMoreScroll : RecyclerView.OnScrollListener {

    private var visibleThreshold = 10
    private lateinit var mOnLoadMoreListener: OnLoadMoreListener
    private var isLoading: Boolean = false
    private var lastVisibleItem: Int = 0
    private var firstVisibleItem: Int = 0
    private var totalItemCount: Int = 0
    private var mLayoutManager: RecyclerView.LayoutManager


    constructor(layoutManager: LinearLayoutManager) {
        this.mLayoutManager = layoutManager
    }

    constructor(layoutManager: GridLayoutManager) {
        this.mLayoutManager = layoutManager
        visibleThreshold *= layoutManager.spanCount
    }

    fun setLoaded() {
        isLoading = false
    }

    fun getLoaded(): Boolean {
        return isLoading
    }

    fun setOnLoadMoreListener(mOnLoadMoreListener: OnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        when (mLayoutManager) {
            is GridLayoutManager -> {
                firstVisibleItem =
                    (mLayoutManager as GridLayoutManager).findFirstVisibleItemPosition()
                lastVisibleItem =
                    (mLayoutManager as GridLayoutManager).findLastVisibleItemPosition()
            }
            is LinearLayoutManager -> {
                firstVisibleItem =
                    (mLayoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                lastVisibleItem =
                    (mLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            }
        }
        if (dy > 0) {
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