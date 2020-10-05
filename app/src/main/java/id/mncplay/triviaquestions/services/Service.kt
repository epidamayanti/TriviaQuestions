package id.mncplay.triviaquestions.services

import id.mncplay.triviaquestions.commons.Utils
import id.mncplay.triviaquestions.models.*
import io.reactivex.Observable
import retrofit2.http.*

interface Service {

    @POST(Utils.DATA_ENDPOINT)
    fun getData(@Query("amount")amount:Int,
                @Query("category")category:Int,
                @Query("difficulty")difficulty:String,
                @Query("type")type:String
    ): Observable<ResponseData>

    @POST(Utils.LOGIN_ENDPOINT)
    fun login(
        @Body login: LoginModel
    ): Observable<Response>

    @POST(Utils.REGIST_ENDPOINT)
    fun register(
        @Body  data: RegisModel
    ): Observable<Response>

    @GET(Utils.LEAD_ENDPOINT)
    fun leaderboard(): Observable<ResponseLead>

    @PUT(Utils.SCORE_ENDPOINT)
    fun scoreUpdate(@Body data: ScoreModel): Observable<Response>


}