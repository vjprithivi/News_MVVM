package com.task.news

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.core.view.isGone

import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.task.news.base.BaseFragment
import com.task.news.databinding.NewsdetailsBinding
import android.content.Intent
import android.net.Uri
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.common.reflect.Reflection.getPackageName


class NewsDetails : BaseFragment<NewsdetailsBinding>()
{
    override fun setBinding(): NewsdetailsBinding =
        NewsdetailsBinding.inflate(layoutInflater)

    private lateinit var viewModel: MainViewModel
    private val args: NewsDetailsArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).mainViewModel
        setupUI(view)
        setupObserver()
    }




    private fun setupUI(view: View) {
        val news = args.news

        binding.deails.text =news.description
        binding.newsPublishedAt.text =news.publishedAt
        binding.newsTitle.text =news.title

        Glide.with(this)
            .load(news.urlToImage)
            .placeholder(R.drawable.favorite)
            .into(binding.newsImage)

        binding.fab.setOnClickListener {
            viewModel.saveNews(news)
            Snackbar.make(view, "News article saved successfully", Snackbar.LENGTH_SHORT).show()
        }

        binding.share.setOnClickListener{


            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_TEXT,  "NEWS \n"+args.news?.title+"\n"+args.news?.publishedAt+" \n"+args.news?.author+"\n"+args.news?.urlToImage)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, "send"))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId())
        {
            android.R.id.home -> {
                view?.let {findNavController().navigate(
                    R.id.action_NewsDetails_to_News,
                    null
                )
                return true
            }}
        }
        return super.onOptionsItemSelected(item)
    }

    fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    private fun setupObserver() {
        viewModel.getFavoriteNews().observe(viewLifecycleOwner, { news ->
            binding.fab.isGone = news.any { it.title == args.news.title }
        })
    }

}