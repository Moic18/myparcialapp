package co.edu.iub.myparcialapp.dto.responses

data class PasajeroResponse(
    val id: Long,
    val nombre: String,
    val documento: String,
    val edad: Int,
    val email: String?,
    val telefono: String?
)