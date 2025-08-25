package co.edu.iub.myparcialapp.controllers

import co.edu.iub.myparcialapp.dto.requests.BuscarVuelosRequest
import co.edu.iub.myparcialapp.dto.requests.CreateVueloRequest
import co.edu.iub.myparcialapp.dto.requests.UpdateEstadoVueloRequest
import co.edu.iub.myparcialapp.dto.responses.BuscarVuelosResponse
import co.edu.iub.myparcialapp.dto.responses.VueloResponse
import co.edu.iub.myparcialapp.entities.EstadoVuelo
import co.edu.iub.myparcialapp.services.VueloService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/vuelos")
@Tag(name = "Vuelos", description = "Gestión de vuelos")
class VueloController(
    private val vueloService: VueloService
) {

    @GetMapping
    @Operation(summary = "Obtener todos los vuelos")
    fun getAllVuelos(): ResponseEntity<List<VueloResponse>> {
        val vuelos = vueloService.getAllVuelos()
        return ResponseEntity.ok(vuelos)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener vuelo por ID")
    fun getVueloById(@PathVariable id: Long): ResponseEntity<VueloResponse> {
        val vuelo = vueloService.getVueloById(id)
        return ResponseEntity.ok(vuelo)
    }

    @PostMapping("/buscar")
    @Operation(summary = "Buscar vuelos", description = "Busca vuelos por origen, destino y fecha")
    fun buscarVuelos(@Valid @RequestBody request: BuscarVuelosRequest): ResponseEntity<BuscarVuelosResponse> {
        val resultado = vueloService.buscarVuelos(request)
        return ResponseEntity.ok(resultado)
    }

    @PostMapping
    @Operation(summary = "Crear vuelo", description = "Solo empleados y administradores")
    @PreAuthorize("hasRole('EMPLEADO') or hasRole('ADMINISTRADOR')")
    fun createVuelo(@Valid @RequestBody request: CreateVueloRequest): ResponseEntity<VueloResponse> {
        val vuelo = vueloService.createVuelo(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(vuelo)
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar vuelo", description = "Solo empleados y administradores")
    @PreAuthorize("hasRole('EMPLEADO') or hasRole('ADMINISTRADOR')")
    fun updateVuelo(
        @PathVariable id: Long,
        @Valid @RequestBody request: CreateVueloRequest
    ): ResponseEntity<VueloResponse> {
        val vuelo = vueloService.updateVuelo(request, id)
        return ResponseEntity.ok(vuelo)
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado del vuelo", description = "Solo empleados y administradores")
    @PreAuthorize("hasRole('EMPLEADO') or hasRole('ADMINISTRADOR')")
    fun updateEstadoVuelo(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateEstadoVueloRequest
    ): ResponseEntity<VueloResponse> {
        val vuelo = vueloService.updateEstadoVuelo(id, request)
        return ResponseEntity.ok(vuelo)
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Obtener vuelos por estado", description = "Solo empleados y administradores")
    @PreAuthorize("hasRole('EMPLEADO') or hasRole('ADMINISTRADOR')")
    fun getVuelosByEstado(@PathVariable estado: EstadoVuelo): ResponseEntity<List<VueloResponse>> {
        val vuelos = vueloService.getVuelosByEstado(estado)
        return ResponseEntity.ok(vuelos)
    }
}