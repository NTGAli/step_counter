package com.ntg.stepcounter.api

import com.ntg.stepcounter.models.FieldOfStudy
import com.ntg.stepcounter.models.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("login.php")
    suspend fun login(
        @Field("phone") phone: String
    ): Response<ResponseBody<String?>>

    @GET("filedOfStudies.php")
    suspend fun filedOfStudies(
    ): Response<ResponseBody<List<FieldOfStudy>?>>

    @FormUrlEncoded
    @POST("registerNewUser.php")
    suspend fun register(
        @Field("fullname") fullName: String,
        @Field("phone") phone: String,
        @Field("state") state: String,
        @Field("uid") uid: String,
        @Field("fos_id") fosId: String,
        @Field("grad_id") gradId: String,
    ): Response<ResponseBody<String?>>

}