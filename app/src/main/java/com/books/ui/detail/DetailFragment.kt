package com.books.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.books.databinding.FragmentDetailBinding
import com.books.module.GlideApp
import com.books.ui.base.BaseFragment
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : BaseFragment() {

    private lateinit var viewBinding: FragmentDetailBinding

    private val detailViewModel: DetailViewModel by viewModels()

    private val args by navArgs<DetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentDetailBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeBookDetails()
        showProgressDialog()
        detailViewModel.getDetailBook(args.isbn13)
    }


    private fun subscribeBookDetails() {
        detailViewModel.bookDetails.observe(viewLifecycleOwner) {bookDetails ->
            viewBinding.ivCover

            GlideApp.with(viewBinding.ivCover.context)
                .load(bookDetails.image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.bitmapTransform(RoundedCorners(14)))
                .into(viewBinding.ivCover)

            dismissProgressDialog()
        }
    }
}