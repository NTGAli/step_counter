package com.ntg.stepi.api.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ntg.stepi.api.ApiService
import com.ntg.stepi.models.res.SummaryRes
import com.ntg.stepi.util.extension.orZero

class UserPagingSource (
    private val query: String,
    private val apiService: ApiService
) : PagingSource<Int, SummaryRes>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SummaryRes> {
        return try {
            val nextPageNumber = params.key ?: 1
            val response = apiService.getUserBase(query, nextPageNumber)

            LoadResult.Page(
                data = response.body()?.data?.users.orEmpty(),
                prevKey = null,
                nextKey = if (response.body()?.data?.users.orEmpty().isNotEmpty()) response.body()?.page.orZero() + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, SummaryRes>): Int =
        ((state.anchorPosition ?: 0) - state.config.initialLoadSize / 2)
            .coerceAtLeast(0)
}