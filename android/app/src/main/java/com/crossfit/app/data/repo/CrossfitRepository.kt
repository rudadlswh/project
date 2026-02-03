package com.crossfit.app.data.repo

import com.crossfit.app.data.api.ApiService
import com.crossfit.app.data.model.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CrossfitRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun sessions(date: String): List<SessionResponse> = apiService.sessions(date)

    suspend fun sessionReservations(sessionId: Long): List<SessionReservationResponse> =
        apiService.sessionReservations(sessionId)

    suspend fun login(req: LoginRequest): AuthResponse = apiService.login(req)

    suspend fun reserve(date: String, timeSlot: String): ReservationResponse =
        apiService.reserve(ReserveRequest(date, timeSlot))

    suspend fun cancel(date: String, timeSlot: String): ReservationResponse =
        apiService.cancel(date, timeSlot)

    suspend fun myReservations(): List<MyReservationResponse> = apiService.myReservations()

    suspend fun monthlyAttendance(month: String): AttendanceSummaryResponse =
        apiService.monthlyAttendance(month)

    suspend fun wod(date: String): WodResponse = apiService.wod(date)

    suspend fun createWod(req: CreateWodRequest): WodResponse = apiService.createWod(req)

    suspend fun deleteWod(date: String) = apiService.deleteWod(date)

    suspend fun createRecord(req: CreateRecordRequest): RecordResponse = apiService.createRecord(req)

    suspend fun bulkRecords(req: BulkRecordRequest): BulkRecordResponse = apiService.bulkRecords(req)

    suspend fun myRecords(): List<RecordResponse> = apiService.myRecords()

    suspend fun notices(): List<NoticeResponse> = apiService.notices()

    suspend fun notice(id: Long): NoticeResponse = apiService.notice(id)

    suspend fun membership(): MembershipResponse = apiService.myMembership()

    suspend fun createNotice(req: CreateNoticeRequest): NoticeResponse = apiService.createNotice(req)

    suspend fun me(): UserResponse = apiService.me()

    suspend fun createCoach(req: CreateCoachRequest): AdminUserResponse = apiService.createCoach(req)

    suspend fun extendMembership(req: ExtendMembershipRequest): MembershipResponse =
        apiService.extendMembership(req)

    suspend fun uploadImage(bytes: ByteArray, filename: String, mimeType: String?): UploadResponse {
        val mediaType = mimeType?.toMediaTypeOrNull() ?: "image/jpeg".toMediaTypeOrNull()
        val requestBody = bytes.toRequestBody(mediaType)
        val part = MultipartBody.Part.createFormData("image", filename, requestBody)
        return apiService.uploadImage(part)
    }
}
