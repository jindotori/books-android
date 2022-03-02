package com.books.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.books.databinding.FragmentSearchBinding
import com.books.repo.search.Book
import com.books.ui.base.BaseFragment
import com.books.ui.search.booklist.BookListAdapter
import com.books.ui.search.booklist.OnLoadMoreListener
import com.books.ui.search.booklist.RecyclerViewLoadMoreScroll
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
            cardViewClicked = { cardViewClicked(it) },
            loadMoreButtonClicked = { loadMoreButtonClicked() }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d(TAG, "onCreateView")

        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        setLayout()
        subscribeSearchResult()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchViewModel.resetViewModelData()
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
        binding.searchBookListRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.searchBookListRecyclerView.adapter = bookListAdapter

        scrollListener =
            RecyclerViewLoadMoreScroll(binding.searchBookListRecyclerView.layoutManager as LinearLayoutManager)
        scrollListener.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                searchViewModel.searchBook(binding.searchView.query.toString())
            }
        })

        binding.searchBookListRecyclerView.addOnScrollListener(scrollListener)
    }

    private fun subscribeSearchResult() {
        Log.d(TAG, "subscribeSearchResult")
        searchViewModel.bookList.observe(viewLifecycleOwner) { bookList ->
            dismissProgressDialog()
            Log.d(TAG, "nononon")
            bookList?.let {
                bookListAdapter.addBook(bookList, bookList.size)
                scrollListener.setLoaded()
            }
        }

        searchViewModel.error.observe(viewLifecycleOwner) { error ->
            dismissProgressDialog()
            scrollListener.setLoaded()
            error?.let {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cardViewClicked(cardView: Book) {
        findNavController().navigate(
            SearchFragmentDirections.actionSearchFragmentToDetailFragment(
                cardView.isbn13
            )
        )
    }

    private fun loadMoreButtonClicked() {
        searchViewModel.searchBook(binding.searchView.query.toString())
    }
}