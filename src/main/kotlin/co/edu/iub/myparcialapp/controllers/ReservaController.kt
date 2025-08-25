package co.edu.iub.myparcialapp.controllers

import co.edu.iub.myparcialapp.dto.requests.CreateReservaRequest
import co.edu.iub.myparcialapp.dto.responses.ReservaResponse
import co.edu.iub.myparcialapp.repositories.UserRepository
import co.edu.iub.myparcialapp.services.ReservaService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import co.edu.iub.myparcialapp.exception.BadRequestException
import org.springframework.security.core.userdetails.UserDetails

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Gestión de reservas de vuelos")
class ReservaController(
    private val reservaService: ReservaService,
    private val userRepository: UserRepository
) {

    @PostMapping
    @Operation(summary = "Crear reserva", description = "Crear una nueva reserva de vuelo")
    @PreAuthorize("hasRole('CLIENTE')")
    fun createReserva(
        @Valid @RequestBody request: CreateReservaRequest,
        authentication: Authentication
    ): ResponseEntity<ReservaResponse> {
        val clienteId = getCurrentUserId(authentication)
        val reserva = reservaService.createReserva(request, clienteId)
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva)
    }

    @GetMapping("/mis-reservas")
    @Operation(summary = "Obtener reservas del cliente autenticado")
    @PreAuthorize("hasRole('CLIENTE')")
    fun getMisReservas(authentication: Authentication): ResponseEntity<List<ReservaResponse>> {
        val clienteId = getCurrentUserId(authentication)
        val reservas = reservaService.getReservasByCliente(clienteId)
        return ResponseEntity.ok(reservas)
    }

    @GetMapping
    @Operation(summary = "Obtener todas las reservas", description = "Solo empleados y administradores")
    @PreAuthorize("hasRole('EMPLEADO') or hasRole('ADMINISTRADOR')")
    fun getAllReservas(): ResponseEntity<List<ReservaResponse>> {
        val reservas = reservaService.getAllReservas()
        return ResponseEntity.ok(reservas)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener reserva por ID")
    fun getReservaById(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<ReservaResponse> {
        val reserva = reservaService.getReservaById(id)

        // Verificar permisos: cliente solo ve sus reservas, empleados/admin ven todas
        val currentUserId = getCurrentUserId(authentication)
        val userRole = getCurrentUserRole(authentication)

        if (userRole == "CLIENTE" && reserva.cliente.id != currentUserId) {
            throw BadRequestException("No tiene permisos para ver esta reserva")
        }

        return ResponseEntity.ok(reserva)
    }

    @GetMapping("/codigo/{codigoReserva}")
    @Operation(summary = "Obtener reserva por código de reserva")
    fun getReservaByCodigoReserva(@PathVariable codigoReserva: String): ResponseEntity<ReservaResponse> {
        val reserva = reservaService.getReservaByCodigoReserva(codigoReserva)
        return ResponseEntity.ok(reserva)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar reserva")
    @PreAuthorize("hasRole('CLIENTE')")
    fun cancelReserva(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<ReservaResponse> {
        val clienteId = getCurrentUserId(authentication)
        val reserva = reservaService.cancelReserva(id, clienteId)
        return ResponseEntity.ok(reserva)
    }

    private fun getCurrentUserId(authentication: Authentication): Long {
        // Implementar lógica para extraer el ID del usuario del token JWT
        val userDetails = authentication.principal as UserDetails
        val user = userRepository.findByEmail(userDetails.username)
        return user?.id ?: throw RuntimeException("Usuario no encontrado")
    }

    private fun getCurrentUserRole(authentication: Authentication): String {
        val userDetails = authentication.principal as UserDetails
        val user = userRepository.findByEmail(userDetails.username)
        return user?.role?.name ?: throw RuntimeException("Usuario no encontrado")
    }
}