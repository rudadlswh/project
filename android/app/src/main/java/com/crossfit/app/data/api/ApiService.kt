package com.crossfit.app.data.api

import com.crossfit.app.data.model.*
import retrofit2.http.*

interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body req: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): AuthResponse

    @GET("sessions")
    suspend fun sessions(@Query("date") date: String): List<SessionResponse>

    @POST("reservations")
    suspend fun reserve(@Body req: ReserveRequest): ReservationResponse

    @DELETE("reservations")
    suspend fun cancel(@Query("date") date: String, @Query("timeSlot") timeSlot: String): ReservationResponse

    @GET("attendance/monthly")
    suspend fun monthlyAttendance(@Query("month") month: String): AttendanceSummaryResponse

    @GET("wod")
    suspend fun wod(@Query("date") date: String): WodResponse

    @POST("wod")
    suspend fun createWod(@Body req: CreateWodRequest): WodResponse

    @DELETE("wod")
    suspend fun deleteWod(@Query("date") date: String)

    @POST("records")
    suspend fun createRecord(@Body req: CreateRecordRequest): RecordResponse

    @GET("records/my")
    suspend fun myRecords(): List<RecordResponse>

    @GET("notices")
    suspend fun notices(): List<NoticeResponse>

    @GET("notices/{id}")
    suspend fun notice(@Path("id") id: Long): NoticeResponse

    @POST("notices")
    suspend fun createNotice(@Body req: CreateNoticeRequest): NoticeResponse

    @GET("memberships/me")
    suspend fun myMembership(): MembershipResponse
}
