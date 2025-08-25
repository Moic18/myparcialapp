package co.edu.iub.myparcialapp.dto.requests

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateAeronaveRequest(
    @field:NotBlank(message = "El modelo es requerido")
    @field:Size(min = 3, max = 50, message = "El modelo debe tener entre 3 y 50 caracteres")
    val modelo: String,

    @field:Min(1, message = "La capacidad debe ser mayor a 0")
    @field:Max(1000, message = "La capacidad debe ser menor a 1000")
    val capacidad: Int,

    @field:NotBlank(message = "El código es requerido")
    @field:Size(min = 3, max = 20, message = "El código debe tener entre 3 y 20 caracteres")
    val codigo: String
)