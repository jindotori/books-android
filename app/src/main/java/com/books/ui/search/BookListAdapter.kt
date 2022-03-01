package com.books.ui.search

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.books.R
import com.books.data.repo.Book
import com.books.databinding.ItemBookBinding
import com.books.module.GlideApp
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

class BookListAdapter(
    private val bookList: ArrayList<Book> = ArrayList(),
    var bookCardClicked: (book: Book) -> Unit
) : RecyclerView.Adapter<BookListAdapter.BookViewHolder>() {
    class BookViewHolder(val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(ItemBookBinding.bind(view))
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
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
                bookCardClicked.invoke(book)
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