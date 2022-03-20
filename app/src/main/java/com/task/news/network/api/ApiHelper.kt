package com.task.news.network.api


import com.task.news.model.NewsResponse
import retrofit2.Response

interface ApiHelper {

    suspend fun searchNews(query: String, pageNumber: Int): Response<NewsResponse>
    suspend fun getNews(countryCode: String, pageNumber: Int): Response<NewsResponse>
}