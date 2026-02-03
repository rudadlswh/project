package com.crossfit.app.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String
)

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val email: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class AuthResponse(
    val accessToken: String,
    val role: String,
    val displayName: String
)

@JsonClass(generateAdapter = true)
data class UserResponse(
    val id: Long,
    val email: String,
    val displayName: String,
    val role: String,
    val active: Boolean
)

@JsonClass(generateAdapter = true)
data class CreateCoachRequest(
    val email: String,
    val displayName: String
)

@JsonClass(generateAdapter = true)
data class AdminUserResponse(
    val id: Long,
    val email: String,
    val displayName: String,
    val role: String,
    val active: Boolean
)

@JsonClass(generateAdapter = true)
data class ExtendMembershipRequest(
    val query: String,
    val days: Int
)

@JsonClass(generateAdapter = true)
data class SessionResponse(
    val id: Long,
    val date: String,
    val timeSlot: String,
    val capacity: Int?,
    val bookedCount: Long,
    val waitlistCount: Long,
    val myStatus: String?,
    val myWaitlistPosition: Int?
)

@JsonClass(generateAdapter = true)
data class SessionReservationResponse(
    val reservationId: Long,
    val userId: Long,
    val displayName: String,
    val status: String,
    val createdAt: String
)

@JsonClass(generateAdapter = true)
data class MyReservationResponse(
    val reservationId: Long,
    val sessionId: Long,
    val date: String,
    val timeSlot: String,
    val status: String
)

@JsonClass(generateAdapter = true)
data class ReserveRequest(
    val date: String,
    val timeSlot: String
)

@JsonClass(generateAdapter = true)
data class ReservationResponse(
    val reservationId: Long,
    val status: String,
    val waitlistPosition: Int?,
    val message: String
)

@JsonClass(generateAdapter = true)
data class AttendanceSummaryResponse(
    val dates: List<String>,
    val totalDays: Int,
    val weekdaysInMonth: Int,
    val attendanceRate: Double
)

@JsonClass(generateAdapter = true)
data class WodResponse(
    val id: Long,
    val date: String,
    val title: String,
    val type: String,
    val description: String
)

@JsonClass(generateAdapter = true)
data class CreateWodRequest(
    val date: String,
    val title: String,
    val type: String,
    val description: String
)

@JsonClass(generateAdapter = true)
data class CreateRecordRequest(
    val wodId: Long?,
    val type: String,
    val value: String,
    val recordDate: String,
    val imageUrl: String?
)

@JsonClass(generateAdapter = true)
data class RecordResponse(
    val id: Long,
    val wodId: Long?,
    val type: String,
    val value: String,
    val imageUrl: String?,
    val recordDate: String
)

@JsonClass(generateAdapter = true)
data class NoticeResponse(
    val id: Long,
    val title: String,
    val content: String,
    val createdBy: String,
    val createdAt: String
)

@JsonClass(generateAdapter = true)
data class CreateNoticeRequest(
    val title: String,
    val content: String
)

@JsonClass(generateAdapter = true)
data class MembershipResponse(
    val type: String,
    val startDate: String?,
    val endDate: String?,
    val remainingCount: Int?,
    val remainingDays: Long?
)

@JsonClass(generateAdapter = true)
data class UploadResponse(
    val url: String
)

@JsonClass(generateAdapter = true)
data class BulkRecordRequest(
    val members: List<String>,
    val recordType: String,
    val value: String,
    val recordDate: String,
    val wodTitle: String
)

@JsonClass(generateAdapter = true)
data class BulkRecordResponse(
    val createdCount: Int,
    val failedMembers: List<String>,
    val message: String
)
