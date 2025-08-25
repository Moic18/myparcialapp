package co.edu.iub.myparcialapp.dto.requests

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class BuscarVuelosRequest(
    @field:NotBlank(message = "El origen es requerido")
    val origen: String,

    @field:NotBlank(message = "El destino es requerido")
    val destino: String,

    @field:NotNull(message = "La fecha es requerida")
    val fecha: java.time.LocalDate
)