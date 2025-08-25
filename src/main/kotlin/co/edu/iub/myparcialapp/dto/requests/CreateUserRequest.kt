package co.edu.iub.myparcialapp.dto.requests

import co.edu.iub.myparcialapp.entities.UserRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateUserRequest(
    @field:NotBlank(message = "El nombre es requerido")
    @field:Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    val name: String,

    @field:Email(message = "Email debe ser válido")
    @field:NotBlank(message = "El email es requerido")
    val email: String,

    @field:NotBlank(message = "La contraseña es requerida")
    @field:Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    val password: String,

    @field:NotNull(message = "El rol es requerido")
    val role: UserRole,

    val phone: String? = null
)