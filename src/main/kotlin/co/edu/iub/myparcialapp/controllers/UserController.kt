package co.edu.iub.myparcialapp.controllers

import co.edu.iub.myparcialapp.dto.requests.CreateUserRequest
import co.edu.iub.myparcialapp.dto.responses.UserResponse
import co.edu.iub.myparcialapp.entities.UserRole
import co.edu.iub.myparcialapp.services.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios")
class UserController(
    private val userService: UserService
) {

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios", description = "Solo administradores")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    fun getAllUsers(): ResponseEntity<List<UserResponse>> {
        val users = userService.getAllUsers()
        return ResponseEntity.ok(users)
    }

    @GetMapping("/rol/{role}")
    @Operation(summary = "Obtener usuarios por rol", description = "Solo administradores")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    fun getUsersByRole(@PathVariable role: UserRole): ResponseEntity<List<UserResponse>> {
        val users = userService.getUsersByRole(role)
        return ResponseEntity.ok(users)
    }

    @PostMapping
    @Operation(summary = "Crear usuario")
    fun createUser(@Valid @RequestBody request: CreateUserRequest): ResponseEntity<UserResponse> {
        val user = userService.createUser(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(user)
    }

    @PostMapping("/empleado")
    @Operation(summary = "Crear empleado", description = "Solo administradores")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    fun createEmpleado(@Valid @RequestBody request: CreateUserRequest): ResponseEntity<UserResponse> {
        val empleadoRequest = request.copy(role = UserRole.EMPLEADO)
        val user = userService.createUser(empleadoRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(user)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    fun getUserById(@PathVariable id: Long): ResponseEntity<UserResponse> {
        val user = userService.getUserById(id)
        return ResponseEntity.ok(user)
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Solo administradores")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    fun updateUser(
        @PathVariable id: Long,
        @Valid @RequestBody request: CreateUserRequest
    ): ResponseEntity<UserResponse> {
        val user = userService.updateUser(request, id)
        return ResponseEntity.ok(user)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario", description = "Solo administradores")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }
}