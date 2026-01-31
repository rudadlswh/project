package com.crossfit.app.ui.model

enum class UserRole {
    MEMBER,
    COACH,
    ADMIN
}

fun UserRole.isStaff(): Boolean = this == UserRole.COACH || this == UserRole.ADMIN
fun UserRole.isAdmin(): Boolean = this == UserRole.ADMIN

fun UserRole.label(): String = when (this) {
    UserRole.MEMBER -> "회원"
    UserRole.COACH -> "코치"
    UserRole.ADMIN -> "관리자"
}
