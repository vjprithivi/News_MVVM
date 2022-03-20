package com.task.news.feed


import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.EditText

import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat


import androidx.lifecycle.lifecycleScope

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.task.news.MainActivity
import com.task.news.MainViewModel
import com.task.news.R
import com.task.news.base.BaseFragment
import com.task.news.databinding.NewsBinding
import com.task.news.state.NetworkState
import com.task.news.ui.adapter.NewsAdapter
import com.task.news.utlis.Constants
import com.task.news.utlis.Constants.Companion.QUERY_PER_PAGE
import com.task.news.utlis.EndlessRecyclerOnScrollListener
import com.task.news.utlis.EspressoIdlingResource
import kotlinx.coroutines.flow.collect

class News : BaseFragment<NewsBinding>() {
    override fun setBinding(): NewsBinding = NewsBinding.inflate(layoutInflater)

    private lateinit var onScrollListener: EndlessRecyclerOnScrollListener
    lateinit var mainViewModel: MainViewModel
    private lateinit var newsAdapter: NewsAdapter
    val countryCode = Constants.CountryCode


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = (activity as MainActivity).mainViewModel


        setupUI()
        setupRecyclerView()
        setupObservers()
        setHasOptionsMenu(true)
    }


    private fun setupUI() {
        EspressoIdlingResource.increment()
        binding.itemErrorMessage.btnRetry.setOnClickListener {
                mainViewModel.fetchNews(countryCode)

            hideErrorMessage()
        }

        onScrollListener = object : EndlessRecyclerOnScrollListener(QUERY_PER_PAGE) {
            override fun onLoadMore() {


                    mainViewModel.fetchNews(countryCode)

            }
        }

        //Swipe refresh listener
        val refreshListener = SwipeRefreshLayout.OnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            mainViewModel.fetchNews(countryCode)
        }

        binding.swipeRefreshLayout.setOnRefreshListener(refreshListener)
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(onScrollListener)
        }
        newsAdapter.setOnItemClickListener { news ->
            var bundle = Bundle().apply {
                putSerializable("news", news)
            }
            findNavController().navigate(
                R.id.action_News_to_NewsDetails,
                bundle
            )
        }
    }




    private fun setupObservers()
    {
        lifecycleScope.launchWhenStarted {

                mainViewModel.newsResponse.collect { response ->
                    when (response) {
                        is NetworkState.Success -> {
                            hideProgressBar()
                            hideErrorMessage()
                            response.data?.let { newResponse ->
                                EspressoIdlingResource.decrement()
                                newsAdapter.differ.submitList(newResponse.articles.toList())
                                mainViewModel.totalPage =
                                    newResponse.totalResults / QUERY_PER_PAGE + 1
                                onScrollListener.isLastPage =
                                    mainViewModel.feedNewsPage == mainViewModel.totalPage + 1
                                hideBottomPadding()
                            }
                        }

                        is NetworkState.Loading -> {
                            showProgressBar()
                        }

                        is NetworkState.Error -> {
                            hideProgressBar()
                            response.message?.let {
                                showErrorMessage(response.message)
                            }
                        }
                    }
                }
            }


        lifecycleScope.launchWhenStarted {
            mainViewModel.errorMessage.collect { value ->
                if (value.isNotEmpty()) {
                    Toast.makeText(activity, value, Toast.LENGTH_LONG).show()
                }
                mainViewModel.hideErrorToast()
            }
        }
    }






    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showErrorMessage(message: String) {
        binding.itemErrorMessage.errorCard.visibility = View.VISIBLE
        binding.itemErrorMessage.tvErrorMessage.text = message
        onScrollListener.isError = true
    }

    private fun hideErrorMessage() {
        binding.itemErrorMessage.errorCard.visibility = View.GONE
        onScrollListener.isError = false
    }


    private fun hideBottomPadding() {
        if (onScrollListener.isLastPage) {
            binding.rvNews.setPadding(0, 0, 0, 0)
        }
    }


}


