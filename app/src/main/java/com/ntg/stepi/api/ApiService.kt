package com.ntg.stepi.api

import com.ntg.stepi.models.FieldOfStudy
import com.ntg.stepi.models.ResponseBody
import com.ntg.stepi.models.res.AccountStateRes
import com.ntg.stepi.models.res.Achievement
import com.ntg.stepi.models.res.DataChallenge
import com.ntg.stepi.models.res.FosDetailsRes
import com.ntg.stepi.models.res.MessageRes
import com.ntg.stepi.models.res.StepRes
import com.ntg.stepi.models.res.StepSynced
import com.ntg.stepi.models.res.SummariesRes
import com.ntg.stepi.models.res.UpdateRes
import com.ntg.stepi.models.res.UserBaseRes
import com.ntg.stepi.models.res.UserProfile
import com.ntg.stepi.models.res.UserRes
import com.ntg.stepi.models.res.UserWinnerData
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("stepi/login.php")
    suspend fun login(
        @Field("phone") phone: String
    ): Response<ResponseBody<String?>>

    @GET("stepi/filedOfStudies.php")
    suspend fun filedOfStudies(
    ): Response<ResponseBody<List<FieldOfStudy>?>>

    @GET("stepi/jobList.php")
    suspend fun jobList(
    ): Response<ResponseBody<List<FieldOfStudy>?>>

    @FormUrlEncoded
    @POST("stepi/registerNewUser.php")
    suspend fun register(
        @Field("fullname") fullName: String,
        @Field("phone") phone: String,
        @Field("state") state: String,
        @Field("uid") uid: String,
        @Field("fos_id") fosId: String,
        @Field("grad_id") gradId: String,
        @Field("timeSign") timeSign: String,
    ): Response<ResponseBody<String?>>

    @FormUrlEncoded
    @POST("stepi/editUserData.php")
    suspend fun editUserData(
        @Field("fullName") fullName: String,
        @Field("phone") phone: String,
        @Field("state") state: String,
        @Field("uid") uid: String,
        @Field("fos_id") fosId: String,
        @Field("grad_id") gradId: String,
    ): Response<ResponseBody<String?>>

    @FormUrlEncoded
    @POST("stepi/editPhoneNumber.php")
    suspend fun editPhoneNumber(
        @Field("phone") phone: String,
        @Field("uid") uid: String
    ): Response<ResponseBody<String?>>

    @FormUrlEncoded
    @POST("stepi/userStep.php")
    suspend fun syncSteps(
        @Field("date") date: String,
        @Field("steps") steps: Int,
        @Field("uid") uid: String?,
    ): Response<ResponseBody<StepSynced?>>

    @FormUrlEncoded
    @POST("stepi/userStep.php")
    fun syncStepsInBack(
        @Field("date") date: String,
        @Field("steps") steps: Int,
        @Field("uid") uid: String?,
    ): Call<ResponseBody<StepSynced?>>

    @FormUrlEncoded
    @POST("stepi/rankData.php")
    suspend fun summariesData(
        @Field("uid") uid: String
    ): Response<ResponseBody<SummariesRes?>>

    @FormUrlEncoded
    @POST("stepi/getUserBase.php")
    suspend fun getUserBase(
        @Field("base") base: String,
        @Field("page") page: Int,
    ): Response<ResponseBody<UserBaseRes?>>


    @FormUrlEncoded
    @POST("stepi/userProfile.php")
    suspend fun userProfile(
        @Field("uid") uid: String,
        @Field("userId") userId: String,
    ): Response<ResponseBody<UserProfile?>>

    @FormUrlEncoded
    @POST("stepi/FiledOfStudyData.php")
    suspend fun fosDetails(
        @Field("fosId") fosId: String,
    ): Response<ResponseBody<FosDetailsRes?>>

    @FormUrlEncoded
    @POST("stepi/UserOfFieldStudy.php")
    suspend fun userOfFos(
        @Field("fosId") fosId: String,
    ): Response<ResponseBody<List<UserRes>?>>

    @FormUrlEncoded
    @POST("stepi/userDataSteps.php")
    suspend fun userSteps(
        @Field("uid") uid: String
    ): Response<ResponseBody<List<StepRes>?>>

    @FormUrlEncoded
    @POST("stepi/clap.php")
    suspend fun clap(
        @Field("uid") uid: String,
        @Field("for") forUid: String
    )

    @FormUrlEncoded
    @POST("stepi/accountSate.php")
    suspend fun accountSate(
        @Field("uid") uid: String
    ): Response<ResponseBody<AccountStateRes?>>

    @FormUrlEncoded
    @POST("stepi/signIn.php")
    suspend fun signIn(
        @Field("uid") uid: String,
        @Field("phone") phone: String,
        @Field("timeSign") timeSign: String,
    ): Response<ResponseBody<UserProfile?>>

    @FormUrlEncoded
    @POST("stepi/clapData.php")
    suspend fun clapData(
        @Field("uid") uid: String
    ): Response<ResponseBody<List<UserRes>?>>

    @FormUrlEncoded
    @POST("stepi/insertSocial.php")
    suspend fun insertSocial(
        @Field("uid") uid: String,
        @Field("name") name: String,
        @Field("pageId") pageId: String,
    ): Response<ResponseBody<Int?>>

    @FormUrlEncoded
    @POST("stepi/updateSocial.php")
    suspend fun updateSocial(
        @Field("uid") uid: String,
        @Field("id") id: Int,
        @Field("name") name: String,
        @Field("pageId") pageId: String,
    ): Response<ResponseBody<Int?>>

    @FormUrlEncoded
    @POST("stepi/userAchievement.php")
    suspend fun userAchievement(
        @Field("uid") uid: String
    ): Response<ResponseBody<Achievement?>>

    @FormUrlEncoded
    @POST("stepi/visibilityWorkout.php")
    suspend fun setLock(
        @Field("uid") uid: String,
        @Field("isLock") isLock: Boolean
    ): Response<ResponseBody<Boolean?>>

    @FormUrlEncoded
    @POST("stepi/fcmUpdate.php")
    suspend fun syncFCM(
        @Field("uid") uid: String,
        @Field("fcm") fcm: String
    ): Response<ResponseBody<String?>>


    @FormUrlEncoded
    @POST("stepi/deleteSocial.php")
    suspend fun deleteSocial(
        @Field("uid") uid: String,
        @Field("id") id: Int
    ): Response<ResponseBody<Int?>>

    @FormUrlEncoded
    @POST("stepi/Messages.php")
    suspend fun messages(
        @Field("uid") uid: String,
    ): Response<ResponseBody<List<MessageRes>?>>

    @FormUrlEncoded
    @POST("stepi/ReadMessage.php")
    suspend fun readMessages(
        @Field("ids") ids: String,
        @Field("uid") uid: String
    ): Response<ResponseBody<String?>>


    @POST("stepi/updateInfo.php")
    suspend fun updateInfo(
    ): Response<ResponseBody<UpdateRes?>>


    @FormUrlEncoded
    @POST("stepi/dataChallenge.php")
    suspend fun dataChallenge(
        @Field("uid") uid: String
    ): Response<ResponseBody<DataChallenge?>>

    @POST("stepi/winner.php")
    suspend fun winners(
    ): Response<ResponseBody<List<UserWinnerData>?>>

    @FormUrlEncoded
    @POST("stepi/insertNewFOS.php")
    suspend fun insertNewFOS(
        @Field("title") title: String
    ): Response<ResponseBody<Int?>>

}