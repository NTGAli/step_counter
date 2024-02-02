package com.ntg.stepi.api.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ntg.stepi.api.ApiService
import com.ntg.stepi.models.res.UserRes
import com.ntg.stepi.util.extension.orZero

class UserDataPagingSource (
    private val fos: String,
    private val apiService: ApiService
) : PagingSource<Int, UserRes>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserRes> {
        return try {
            val nextPageNumber = params.key ?: 1
            val response = apiService.userOfFos(fos, nextPageNumber)

            LoadResult.Page(
                data = response.body()?.data.orEmpty(),
                prevKey = null,
                nextKey = if (response.body()?.data.orEmpty().isNotEmpty()) response.body()?.page.orZero() + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UserRes>): Int =
        ((state.anchorPosition ?: 0) - state.config.initialLoadSize / 2)
            .coerceAtLeast(0)
}