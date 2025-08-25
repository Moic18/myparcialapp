package co.edu.iub.myparcialapp.dto.requests

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:Email(message = "Email debe ser válido")
    @field:NotBlank(message = "El email es obligatorio")
    val email: String,

    @field:NotBlank(message = "La contraseña es obligatoria")
    val password: String
)



















