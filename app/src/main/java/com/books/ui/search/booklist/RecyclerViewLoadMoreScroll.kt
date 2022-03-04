package com.books.ui.search.booklist

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewLoadMoreScroll(layoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {

    companion object {
        private const val TAG = "RecyclerViewLoadMoreScroll"
    }

    private lateinit var loadMoreListener: OnLoadMoreListener
    private var isLoading: Boolean = false
    private var lastVisibleItem: Int = 0
    private var totalItemCount: Int = 0
    private var mLayoutManager: RecyclerView.LayoutManager = layoutManager

    fun setLoaded() {
        isLoading = false
    }

    fun getLoaded(): Boolean {
        return isLoading
    }

    fun setOnLoadMoreListener(loadMoreListener: OnLoadMoreListener) {
        this.loadMoreListener = loadMoreListener
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            loadMoreListener.onDragging()
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        lastVisibleItem =
            (mLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        if (dy > 0) {
            Log.d(TAG, "itemCount: ${mLayoutManager.itemCount}")
            Log.d(TAG, "lastVisibleItem: $lastVisibleItem")
            totalItemCount = mLayoutManager.itemCount

            if (!isLoading && totalItemCount <= lastVisibleItem + 1) {
                loadMoreListener.onLoadMore()
                isLoading = true
            }
        }
    }

    interface OnLoadMoreListener {
        fun onLoadMore()
        fun onDragging()
    }
}
