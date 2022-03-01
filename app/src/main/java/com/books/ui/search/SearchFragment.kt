package com.books.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.books.data.repo.Book
import com.books.databinding.FragmentSearchBinding
import com.books.ui.base.BaseFragment
import com.books.ui.search.scroll.OnLoadMoreListener
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
            cardViewClicked = { cardView -> cardViewClicked(cardView)},
            loadMoreButtonClicked = { loadMoreButtonClicked()}
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

        binding.bookListRecyclerView.addOnScrollListener(scrollListener)
    }

    private fun subscribeSearchResult() {
        searchViewModel.bookList.observe(viewLifecycleOwner) { bookList ->
            dismissProgressDialog()
            bookListAdapter.addBook(bookList, bookList.size)
            scrollListener.setLoaded()
        }

        searchViewModel.error.observe(viewLifecycleOwner) { error ->
            dismissProgressDialog()
            scrollListener.setLoaded()
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
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