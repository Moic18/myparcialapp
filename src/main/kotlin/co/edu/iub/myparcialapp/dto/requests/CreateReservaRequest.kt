package co.edu.iub.myparcialapp.dto.requests

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class CreateReservaRequest(
    @field:NotNull(message = "El ID del vuelo es requerido")
    val vueloId: Long,

    @field:NotEmpty(message = "Debe incluir al menos un pasajero")
    @field:Valid
    val pasajeros: List<CreatePasajeroRequest>
)