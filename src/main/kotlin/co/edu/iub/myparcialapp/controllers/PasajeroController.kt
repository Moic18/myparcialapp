package co.edu.iub.myparcialapp.controllers


import co.edu.iub.myparcialapp.dto.responses.PasajeroResponse
import co.edu.iub.myparcialapp.services.PasajeroService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/pasajeros")
@Tag(name = "Pasajeros", description = "Gestión de pasajeros")
class PasajeroController(
    private val pasajeroService: PasajeroService
) {

    @GetMapping("/vuelo/{vueloId}")
    @Operation(summary = "Obtener pasajeros por vuelo", description = "Solo empleados y administradores")
    @PreAuthorize("hasRole('EMPLEADO') or hasRole('ADMINISTRADOR')")
    fun getPasajerosByVuelo(@PathVariable vueloId: Long): ResponseEntity<List<PasajeroResponse>> {
        val pasajeros = pasajeroService.getPasajerosByVuelo(vueloId)
        return ResponseEntity.ok(pasajeros)
    }

    @GetMapping("/reserva/{reservaId}")
    @Operation(summary = "Obtener pasajeros por reserva")
    fun getPasajerosByReserva(
        @PathVariable reservaId: Long,
        authentication: Authentication
    ): ResponseEntity<List<PasajeroResponse>> {
        // Verificar permisos similar al ReservaController
        val pasajeros = pasajeroService.getPasajerosByReserva(reservaId)
        return ResponseEntity.ok(pasajeros)
    }
}