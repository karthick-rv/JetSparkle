package com.example.jetsparkle.paging.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val newsApiService: NewsApi
) {
    fun getNews() = Pager(
        config = PagingConfig(
            pageSize = 20,
        ),
        pagingSourceFactory = {
            NewsPagingSource(newsApiService)
        }
    ).flow
}