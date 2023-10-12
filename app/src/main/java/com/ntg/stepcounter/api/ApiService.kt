package com.ntg.stepcounter.api

import com.ntg.stepcounter.models.FieldOfStudy
import com.ntg.stepcounter.models.ResponseBody
import com.ntg.stepcounter.models.Step
import com.ntg.stepcounter.models.res.StepSynced
import com.ntg.stepcounter.models.res.SummariesRes
import com.ntg.stepcounter.models.res.SummaryRes
import com.ntg.stepcounter.models.res.UserProfile
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

    @FormUrlEncoded
    @POST("userStep.php")
    suspend fun syncSteps(
        @Field("uid") uid: String,
        @Field("date") date: String,
        @Field("steps") steps: Int?,
    ): Response<ResponseBody<StepSynced?>>

    @FormUrlEncoded
    @POST("rankData.php")
    suspend fun summariesData(
        @Field("uid") uid: String
    ): Response<ResponseBody<SummariesRes?>>


    @FormUrlEncoded
    @POST("userProfile.php")
    suspend fun userProfile(
        @Field("uid") uid: String
    ): Response<ResponseBody<UserProfile?>>
}