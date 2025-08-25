package co.edu.iub.myparcialapp.dto.responses

import co.edu.iub.myparcialapp.entities.EstadoVuelo

data class VueloResponse(
    val id: Long,
    val numeroVuelo: String,
    val origen: String,
    val destino: String,
    val fechaSalida: java.time.Instant,
    val fechaLlegada: java.time.Instant,
    val precio: java.math.BigDecimal,
    val estado: EstadoVuelo,
    val asientosDisponibles: Int,
    val aeronave: AeronaveResponse,
    val duracionVuelo: String,
    val createdAt: java.time.Instant
)