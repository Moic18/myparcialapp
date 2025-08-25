package co.edu.iub.myparcialapp.controllers

import co.edu.iub.myparcialapp.dto.requests.LoginRequest
import co.edu.iub.myparcialapp.dto.responses.AuthResponse
import co.edu.iub.myparcialapp.dto.responses.ErrorResponse
import co.edu.iub.myparcialapp.services.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@Tag(
    name = "Autenticación",
    description = "Endpoints para autenticación de usuarios. Permite a los usuarios iniciar sesión y obtener tokens JWT."
)
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica un usuario con email y contraseña, retorna un token JWT válido por 24 horas"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Login exitoso",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = AuthResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Datos de entrada inválidos",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Credenciales incorrectas",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponse::class)
                )]
            )
        ]
    )
    fun login(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<AuthResponse> {
        val authResponse = authService.login(loginRequest)
        return ResponseEntity.ok(authResponse)
    }
}