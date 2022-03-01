package com.books.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.books.data.repo.Book
import com.books.databinding.FragmentSearchBinding
import com.books.ui.base.BaseFragment
import com.books.ui.search.scroll.OnLoadMoreListener
import com.books.ui.search.scroll.OnTopToScrollListener
import com.books.ui.search.scroll.RecyclerViewLoadMoreScroll
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class SearchFragment : BaseFragment() {
    companion object {
        private const val TAG = "SearchFragment"
    }

    private lateinit var binding: FragmentSearchBinding
    private lateinit var scrollListener: RecyclerViewLoadMoreScroll

    private val searchViewModel: SearchViewModel by viewModels()
    private val bookListAdapter: BookListAdapter by lazy {
        BookListAdapter(
            bookCardClicked = { bookCard -> bookCardClicked(bookCard) }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLayout()
        subscribeSearchResult()
    }

    private fun setLayout() {
        setSearchView()
        setBookListView()
    }

    private fun setSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                bookListAdapter.clear()
                scrollListener.setLoaded()
                query?.let { bookTitle ->
                    showProgressDialog()
                    searchViewModel.init()
                    searchViewModel.searchBook(bookTitle)
                }
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun setBookListView() {
        binding.bookListRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.bookListRecyclerView.adapter = bookListAdapter

        scrollListener =
            RecyclerViewLoadMoreScroll(binding.bookListRecyclerView.layoutManager as LinearLayoutManager)
        scrollListener.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                showProgressDialog()
                searchViewModel.searchBook(binding.searchView.query.toString())
            }
        })

        scrollListener.setOnVisibleTopToScrollButtonListener(object : OnTopToScrollListener {
            override fun onVisibleTopToScrollButton() {
                visibleTopToScroll()
            }

            override fun onInvisibleTopToScrollButton() {
                invisibleTopToScroll()
            }
        })
        binding.bookListRecyclerView.addOnScrollListener(scrollListener)


        binding.scrollToTopButton.visibility = View.INVISIBLE
        binding.scrollToTopButton.setOnClickListener {
            binding.scrollToTopButton.visibility = View.INVISIBLE
            binding.bookListRecyclerView.scrollToPosition(0)
        }
    }

    private fun visibleTopToScroll() {
        if (!binding.scrollToTopButton.isVisible)
            binding.scrollToTopButton.visibility = View.VISIBLE
    }

    private fun invisibleTopToScroll() {
        if (binding.scrollToTopButton.isVisible)
            binding.scrollToTopButton.visibility = View.INVISIBLE
    }

    private fun subscribeSearchResult() {
        searchViewModel.bookList.observe(viewLifecycleOwner) { bookList ->
            dismissProgressDialog()
            bookListAdapter.addBook(bookList, bookList.size)
            scrollListener.setLoaded()
//            if (bookListAdapter.itemCount < 10) {
//                searchViewModel.searchBook(binding.searchView.query.toString())
//            }
        }
    }

    private fun bookCardClicked(bookCard: Book) {
        findNavController().navigate(
            SearchFragmentDirections.actionSearchFragmentToDetailFragment(
                bookCard.isbn13
            )
        )
    }
}