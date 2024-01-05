package com.ntg.stepi.api

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ntg.stepi.models.res.SummaryRes

class UsersPagingSource(
    private val newsApiService: ApiService,
    private val base: String,
): PagingSource<Int, SummaryRes>() {
    override fun getRefreshKey(state: PagingState<Int, SummaryRes>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SummaryRes> {
        return try {
            val page = params.key ?: 1
            val response = newsApiService.getUserBase(base = base, page = page)

            LoadResult.Page(
                data = response.body()?.data?.users.orEmpty(),
                prevKey = if (page == 1) null else page.minus(1),
                nextKey = if (response.body()?.data?.users.orEmpty().isEmpty()) null else page.plus(1),
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}