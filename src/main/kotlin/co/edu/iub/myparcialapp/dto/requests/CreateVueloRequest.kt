package co.edu.iub.myparcialapp.dto.requests

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateVueloRequest(
    @field:NotBlank(message = "El número de vuelo es requerido")
    @field:Size(min = 3, max = 20, message = "El número de vuelo debe tener entre 3 y 20 caracteres")
    val numeroVuelo: String,

    @field:NotBlank(message = "El origen es requerido")
    @field:Size(min = 3, max = 100, message = "El origen debe tener entre 3 y 100 caracteres")
    val origen: String,

    @field:NotBlank(message = "El destino es requerido")
    @field:Size(min = 3, max = 100, message = "El destino debe tener entre 3 y 100 caracteres")
    val destino: String,

    @field:NotNull(message = "La fecha de salida es requerida")
    val fechaSalida: java.time.Instant,

    @field:NotNull(message = "La fecha de llegada es requerida")
    val fechaLlegada: java.time.Instant,

    @field:NotNull(message = "El precio es requerido")
    @field:DecimalMin("0.0", message = "El precio debe ser mayor a 0")
    val precio: java.math.BigDecimal,

    @field:NotNull(message = "El ID de aeronave es requerido")
    val aeronaveId: Long
)