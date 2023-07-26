package com.example.jetsparkle.paging.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.jetsparkle.paging.data.NewsRepository
import com.example.jetsparkle.paging.domain.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: NewsRepository,
): ViewModel() {

    fun getBreakingNews(): Flow<PagingData<Article>> = repository.getNews().cachedIn(viewModelScope)
}