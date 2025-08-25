package co.edu.iub.myparcialapp.dto.responses

data class ErrorResponse(
    val code: String,
    val message: String,
    val timestamp: java.time.LocalDateTime = java.time.LocalDateTime.now(),
    val path: String? = null,
    val details: List<String>? = null
)