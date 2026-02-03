package com.crossfit.app.data.api

import com.crossfit.app.data.model.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body req: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): AuthResponse

    @GET("sessions")
    suspend fun sessions(@Query("date") date: String): List<SessionResponse>

    @GET("sessions/{id}/reservations")
    suspend fun sessionReservations(@Path("id") id: Long): List<SessionReservationResponse>

    @POST("reservations")
    suspend fun reserve(@Body req: ReserveRequest): ReservationResponse

    @DELETE("reservations")
    suspend fun cancel(@Query("date") date: String, @Query("timeSlot") timeSlot: String): ReservationResponse

    @GET("reservations/me")
    suspend fun myReservations(): List<MyReservationResponse>

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

    @POST("records/bulk")
    suspend fun bulkRecords(@Body req: BulkRecordRequest): BulkRecordResponse

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

    @GET("users/me")
    suspend fun me(): UserResponse

    @POST("admin/coaches")
    suspend fun createCoach(@Body req: CreateCoachRequest): AdminUserResponse

    @POST("admin/memberships/extend")
    suspend fun extendMembership(@Body req: ExtendMembershipRequest): MembershipResponse

    @Multipart
    @POST("api/uploads/images")
    suspend fun uploadImage(@Part image: MultipartBody.Part): UploadResponse
}
