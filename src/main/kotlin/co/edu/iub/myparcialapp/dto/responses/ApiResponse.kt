package co.edu.iub.myparcialapp.dto.responses

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val timestamp: java.time.Instant = java.time.Instant.now()
)