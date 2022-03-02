package com.books.ui.detail.pdflist

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.books.R
import com.books.databinding.ItemPdfBinding

class PdfListAdapter(
    private val pdfList: ArrayList<Pair<String, String>> = ArrayList()
) : RecyclerView.Adapter<PdfListAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemPdfBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pdf, parent, false)
        return ViewHolder(ItemPdfBinding.bind(view))
    }

    override fun getItemCount(): Int {
        return pdfList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        pdfList[position].let { item ->
            holder.binding.tvPdfKey.text = item.first + ": "
            holder.binding.tvPdfValue.text = item.second
        }
    }

    fun addPdf(pdf: HashMap<String, String>, size: Int) {
        pdf.map { entry ->
            pdfList.add(Pair(entry.key, entry.value))
        }
        pdfList.sortBy { pdf -> pdf.first }
        notifyItemRangeChanged(pdfList.size, size)
    }
}