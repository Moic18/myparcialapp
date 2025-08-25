package co.edu.iub.myparcialapp.dto.responses

import co.edu.iub.myparcialapp.entities.UserRole

data class UserResponse(
    val id: Long,
    val name: String,
    val email: String,
    val role: UserRole,
    val phone: String?,
    val isActive: Boolean,
    val createdAt: java.time.Instant
)