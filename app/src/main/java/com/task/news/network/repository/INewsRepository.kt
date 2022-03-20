package com.task.news.network.repository

import androidx.lifecycle.LiveData
import com.task.news.model.NewsArticle
import com.task.news.model.NewsResponse
import com.task.news.state.NetworkState

interface INewsRepository {
    suspend fun getNews(countryCode: String, pageNumber: Int): NetworkState<NewsResponse>


    suspend fun saveNews(news: NewsArticle): Long

    fun getSavedNews(): LiveData<List<NewsArticle>>

    suspend fun deleteNews(news: NewsArticle)

    suspend fun deleteAllNews()
}