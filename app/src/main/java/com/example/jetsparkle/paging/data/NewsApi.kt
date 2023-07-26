package com.example.jetsparkle.paging.data

import com.example.jetsparkle.paging.domain.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    companion object{
        const val API_KEY = "94ac61bb12d24d2e931b865fe2cc975a"
    }

    @GET("everything?q=apple&sortBy=popularity&apiKey=${API_KEY}&pageSize=20")
    suspend fun getNews(
        @Query("page") page: Int
    ): NewsResponse


}