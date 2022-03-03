package com.books.ui.detail

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.books.R
import com.books.databinding.FragmentDetailBinding
import com.books.module.GlideApp
import com.books.repo.detail.Detail
import com.books.ui.base.BaseFragment
import com.books.ui.detail.pdflist.PdfListAdapter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.books.repo.Result

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
        binding.rvPdfList.layoutManager = LinearLayoutManager(context)
        binding.rvPdfList.adapter = pdfListAdapter
        subscribeBookDetails()
        detailViewModel.getDetailBook(args.isbn13)
        showProgressDialog()
        binding.clDetail.visibility = View.INVISIBLE
    }

    private fun subscribeBookDetails() {
        detailViewModel.bookDetailsResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> setDataOnView(result.data)
                is Result.Error -> {
                    val errorMessage:String = result.exception.message.toString()
                    toast(errorMessage)
                    findNavController().popBackStack()
                }
            }
            binding.clDetail.visibility = View.VISIBLE
            dismissProgressDialog()
        }
    }

    private fun setDataOnView(detail: Detail) {
        GlideApp.with(binding.ivCover.context)
            .load(detail.image)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(RequestOptions.bitmapTransform(RoundedCorners(14)))
            .into(binding.ivCover)

        binding.tvTitleBody.text = detail.title
        binding.tvSubTitleBody.text = detail.subtitle
        binding.tvAuthorsBody.text = detail.authors
        binding.tvIsbn10Body.text = detail.isbn10
        binding.tvIsbn13Body.text = detail.isbn13
        binding.tvPagesBody.text = detail.pages
        binding.tvYearBody.text = detail.year
        binding.ratingBar.rating = detail.rating.toFloat()
        binding.tvDescBody.text = detail.desc
        binding.tvPriceBody.text = detail.price
        binding.tvUrl.movementMethod = LinkMovementMethod.getInstance()
        val linkedText = String.format(getString(R.string.hyperlink_text, detail.url))
        binding.tvUrl.text = Html.fromHtml(linkedText, Html.FROM_HTML_MODE_COMPACT)
        detail.pdf?.let { pdf ->
            val pdfList = pdf.keys.map { Pair(it, pdf[it]) }
            pdfListAdapter.addPdf(pdfList, pdfList.size)
            binding.clFreeDownloads.visibility = View.VISIBLE
        }
    }
}