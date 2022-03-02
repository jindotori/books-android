package com.books.ui.detail

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.books.databinding.FragmentDetailBinding
import com.books.module.GlideApp
import com.books.ui.base.BaseFragment
import com.books.ui.detail.pdflist.PdfListAdapter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : BaseFragment() {

    companion object {
        private const val TAG = "DetailFragment"
    }
    private lateinit var binding: FragmentDetailBinding

    private val detailViewModel: DetailViewModel by viewModels()

    private val args by navArgs<DetailFragmentArgs>()

    private val pdfListAdapter: PdfListAdapter by lazy {
        PdfListAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeBookDetails()
        showProgressDialog()
        binding.clDetail.visibility = View.INVISIBLE
        detailViewModel.getDetailBook(args.isbn13)

        binding.rvPdfList.layoutManager = LinearLayoutManager(context)
        binding.rvPdfList.adapter = pdfListAdapter
    }

    private fun subscribeBookDetails() {
        detailViewModel.bookDetails.observe(viewLifecycleOwner) {bookDetails ->
            dismissProgressDialog()
            binding.clDetail.visibility = View.VISIBLE

            GlideApp.with(binding.ivCover.context)
                .load(bookDetails.image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.bitmapTransform(RoundedCorners(14)))
                .into(binding.ivCover)

            binding.tvTitleBody.text = bookDetails.title
            binding.tvSubTitleBody.text = bookDetails.subtitle
            binding.tvAuthorsBody.text = bookDetails.authors
            binding.tvIsbn10Body.text = bookDetails.isbn10
            binding.tvIsbn13Body.text = bookDetails.isbn13
            binding.tvPagesBody.text = bookDetails.pages
            binding.tvYearBody.text = bookDetails.year
            binding.ratingBar.rating = bookDetails.rating.toFloat()
            binding.tvDescBody.text = bookDetails.desc
            binding.tvPriceBody.text = bookDetails.price

            binding.tvUrl.movementMethod = LinkMovementMethod.getInstance()
            val linkedText = String.format("<a href=\"%s\">Go to IT book store for more information.</a> ", bookDetails.url)
            binding.tvUrl.text = Html.fromHtml(linkedText, Html.FROM_HTML_MODE_COMPACT)

            bookDetails.pdf?.let {pdf ->
                binding.clFreeDownloads.visibility = View.VISIBLE
                pdfListAdapter.addPdf(pdf, pdf.size)
            }
        }
    }
}