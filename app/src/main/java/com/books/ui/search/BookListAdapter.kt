package com.books.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.books.R
import com.books.data.repo.Book
import com.books.databinding.ItemBookBinding
import com.books.databinding.ItemLoadMoreBinding
import com.books.module.GlideApp
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

class BookListAdapter(
    private val bookList: ArrayList<Book> = ArrayList(),
    var cardViewClicked: (book: Book) -> Unit,
    var loadMoreButtonClicked: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class BookViewHolder(val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root)
    class LoadMoreViewHolder(val binding: ItemLoadMoreBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.item_book -> BookViewHolder(ItemBookBinding.bind(view))
            R.layout.item_load_more -> LoadMoreViewHolder(ItemLoadMoreBinding.bind(view))
            else -> {
                throw  IllegalArgumentException("Cannot create ViewHolder")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == bookList.size) {
            R.layout.item_load_more
        } else {
            R.layout.item_book
        }
    }

    override fun getItemCount(): Int {
        return bookList.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BookViewHolder -> {
                bookList[position].let { book ->
                    holder.binding.tvTitle.text = book.title
                    holder.binding.tvSubTitle.text = book.subtitle
                    holder.binding.tvPrice.text = book.price

                    GlideApp.with(holder.itemView.context)
                        .load(book.image)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(RequestOptions.bitmapTransform(RoundedCorners(14)))
                        .into(holder.binding.imgBook)

                    holder.binding.itemCardView.setOnClickListener {
                        cardViewClicked.invoke(book)
                    }
                }
            }
            is LoadMoreViewHolder -> {
                if (position == 0) {
                    holder.binding.btnLoadMore.visibility = View.INVISIBLE
                } else {
                    holder.binding.btnLoadMore.visibility = View.VISIBLE
                }
                holder.binding.btnLoadMore.setOnClickListener { loadMoreButtonClicked.invoke() }
            }
        }
    }

    fun addBook(list: List<Book>, size: Int) {
        bookList.addAll(list)
        notifyItemRangeChanged(bookList.size, size)
    }

    fun clear() {
        bookList.clear()
        notifyDataSetChanged()
    }
}