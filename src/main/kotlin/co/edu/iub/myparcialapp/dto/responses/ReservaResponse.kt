package co.edu.iub.myparcialapp.dto.responses

import co.edu.iub.myparcialapp.entities.EstadoReserva

data class ReservaResponse(
    val id: Long,
    val codigoReserva: String,
    val vuelo: VueloResponse,
    val cliente: UserResponse,
    val cantidadPasajeros: Int,
    val total: java.math.BigDecimal,
    val estado: EstadoReserva,
    val fechaReserva: java.time.Instant,
    val pasajeros: List<PasajeroResponse>,
    val puedeCancel: Boolean
)