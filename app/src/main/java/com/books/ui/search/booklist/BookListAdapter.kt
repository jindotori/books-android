package com.books.ui.search.booklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.books.R
import com.books.databinding.ItemBookBinding
import com.books.repo.search.Book
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

class BookListAdapter(
    private val bookList: ArrayList<Book> = ArrayList(),
    var cardViewClicked: (book: Book) -> Unit
) : RecyclerView.Adapter<BookListAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return ViewHolder(ItemBookBinding.bind(view))
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bookList[position].let { book ->
            holder.binding.tvTitle.text = book.title
            holder.binding.tvSubTitle.text = book.subtitle
            holder.binding.tvPrice.text = book.price

            Glide.with(holder.itemView.context)
                .load(book.image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.bitmapTransform(RoundedCorners(14)))
                .into(holder.binding.imgBook)

            holder.binding.itemCardView.setOnClickListener {
                cardViewClicked.invoke(book)
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