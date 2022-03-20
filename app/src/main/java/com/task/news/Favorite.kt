package com.task.news

import android.os.Bundle

import android.view.View

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.task.news.base.BaseFragment
import com.task.news.databinding.FavoriteBinding
import com.task.news.ui.adapter.NewsAdapter
import com.task.news.utlis.EspressoIdlingResource



class Favorite : BaseFragment<FavoriteBinding>()
{
    lateinit var viewModel: MainViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).mainViewModel
        setupRecyclerView()
        setupUI(view)
        setupObserver()
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvFavoriteNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun setupUI(view: View)
    {
        EspressoIdlingResource.increment()
        newsAdapter.setOnItemClickListener {
            var bundle = Bundle().apply {
                putSerializable("news", it)
                putBoolean("isFromFavorite", true)
            }
            findNavController().navigate(
                R.id.action_Favorite_to_NewsDetails,
                bundle
            )
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
           ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                viewModel.deleteNews(article)
                Snackbar.make(view, "Successfully deleted news article", Snackbar.LENGTH_LONG)
                    .apply {
                        setAction("Undo") {
                            viewModel.saveNews(article)
                        }
                        show()
                    }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvFavoriteNews)
        }
    }

    private fun setupObserver() {
        viewModel.getFavoriteNews().observe(viewLifecycleOwner, { news ->
            EspressoIdlingResource.decrement()
            newsAdapter.differ.submitList(news)
        })
    }

    override fun setBinding():FavoriteBinding = FavoriteBinding.inflate(layoutInflater)

}