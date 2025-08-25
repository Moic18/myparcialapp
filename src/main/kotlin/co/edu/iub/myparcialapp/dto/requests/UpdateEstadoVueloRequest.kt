package co.edu.iub.myparcialapp.dto.requests

import co.edu.iub.myparcialapp.entities.EstadoVuelo
import jakarta.validation.constraints.NotNull

data class UpdateEstadoVueloRequest(
    @field:NotNull(message = "El estado es requerido")
    val estado: EstadoVuelo
)