package co.edu.iub.myparcialapp.dto.requests

import jakarta.validation.constraints.NotNull

data class ReportePeriodoRequest(
    @field:NotNull(message = "La fecha de inicio es requerida")
    val fechaInicio: java.time.LocalDate,

    @field:NotNull(message = "La fecha de fin es requerida")
    val fechaFin: java.time.LocalDate
)