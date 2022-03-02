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
import com.books.repo.Status
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
        private const val ITEMS_PER_PAGE = 10
    }

    private lateinit var binding: FragmentSearchBinding
    private lateinit var scrollListener: RecyclerViewLoadMoreScroll

    private val searchViewModel: SearchViewModel by viewModels()
    private val bookListAdapter: BookListAdapter by lazy {
        BookListAdapter(cardViewClicked = { cardViewClicked(it) })
    }
    private var isFirst: Boolean = true

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

    override fun onDestroyView() {
        super.onDestroyView()

        searchViewModel.clear()
    }

    private fun setLayout() {
        setSearchView()
        setBookListView()
        setLoadMoreButtonListener()
    }

    private fun setSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                bookListAdapter.clear()
                scrollListener.setLoaded()
                query?.let { bookTitle ->
                    isFirst = true
                    showProgressDialog()
                    binding.loadMoreBtn.visibility = View.INVISIBLE
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
                binding.progressBar.visibility = View.VISIBLE
                searchViewModel.searchBook(binding.searchView.query.toString())
            }
        })

        binding.searchBookListRecyclerView.addOnScrollListener(scrollListener)
    }

    private fun subscribeSearchResult() {
        Log.d(TAG, "subscribeSearchResult")
        searchViewModel.searchBookResult.observe(viewLifecycleOwner) { result ->
            binding.progressBar.visibility = View.INVISIBLE
            binding.loadMoreBtn.visibility = View.INVISIBLE
            dismissProgressDialog()

            result?.let {
                when (result.status) {
                    Status.SUCCESS -> {
                        result.data?.let { bookList ->
                            bookListAdapter.addBook(bookList, bookList.size)
                            if (isFirst) {
                                isFirst = false
                                if (bookList.size < ITEMS_PER_PAGE) {
                                    binding.loadMoreBtn.visibility = View.VISIBLE
                                }
                            }
                            scrollListener.setLoaded()
                        }
                    }
                    Status.ERROR,
                    Status.FAIL -> {
                        scrollListener.setLoaded()
                        result.message.let { message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun setLoadMoreButtonListener() {
        binding.loadMoreBtn.setOnClickListener {
            binding.loadMoreBtn.visibility = View.INVISIBLE
            searchViewModel.searchBook(binding.searchView.query.toString())
        }
    }

    private fun cardViewClicked(cardView: Book) {
        findNavController().navigate(
            SearchFragmentDirections.actionSearchFragmentToDetailFragment(cardView.isbn13)
        )
    }
}