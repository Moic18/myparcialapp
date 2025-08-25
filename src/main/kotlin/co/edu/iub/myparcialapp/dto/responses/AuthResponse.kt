package co.edu.iub.myparcialapp.dto.responses

data class AuthResponse(
    val token: String,
    val type: String = "Bearer",
    val user: UserResponse
)