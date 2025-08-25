package co.edu.iub.myparcialapp.dto.requests

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreatePasajeroRequest(
    @field:NotBlank(message = "El nombre del pasajero es requerido")
    @field:Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    val nombre: String,

    @field:NotBlank(message = "El documento es requerido")
    @field:Size(min = 5, max = 20, message = "El documento debe tener entre 5 y 20 caracteres")
    val documento: String,

    @field:Min(0, message = "La edad debe ser mayor o igual a 0")
    @field:Max(120, message = "La edad debe ser menor a 120")
    val edad: Int,

    @field:Email(message = "El email debe ser válido")
    val email: String? = null,

    val telefono: String? = null
)