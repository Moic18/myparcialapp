package co.edu.iub.myparcialapp.dto.responses

data class AeronaveResponse(
    val id: Long,
    val modelo: String,
    val capacidad: Int,
    val codigo: String,
    val isActive: Boolean,
    val createdAt: java.time.Instant
)