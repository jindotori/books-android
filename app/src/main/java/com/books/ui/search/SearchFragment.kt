package com.books.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.books.repo.Result
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
        BookListAdapter(cardViewClicked = { cardViewClicked(it) })
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
                query?.let {
                    binding.tvSearchGuide.visibility = View.INVISIBLE
                    showProgressDialog()
                    searchViewModel.init(it)
                    searchViewModel.searchBook()
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
                searchViewModel.searchBook()
            }

            override fun onDragging() {
                binding.searchView.clearFocus()
            }
        })

        binding.searchBookListRecyclerView.addOnScrollListener(scrollListener)
    }

    private fun subscribeSearchResult() {
        Log.d(TAG, "subscribeSearchResult")
        searchViewModel.searchBookResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    bookListAdapter.addBook(result.data, result.data.size)
                    val message: String = result.data.size.toString() + " data loaded."
                    toast(message)
                }
                is Result.Error -> {
                    val errorMessage: String = result.exception.message.toString()
                    toast(errorMessage)
                }
            }
            binding.progressBar.visibility = View.INVISIBLE
            dismissProgressDialog()
            scrollListener.setLoaded()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchViewModel.clear()
    }

    private fun cardViewClicked(cardView: Book) {
        toast.cancel()
        findNavController().navigate(
            SearchFragmentDirections.actionSearchFragmentToDetailFragment(cardView.isbn13)
        )
    }
}