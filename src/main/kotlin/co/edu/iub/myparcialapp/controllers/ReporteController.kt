package co.edu.iub.myparcialapp.controllers

import co.edu.iub.myparcialapp.dto.requests.ReportePeriodoRequest
import co.edu.iub.myparcialapp.dto.responses.EstadisticasGeneralesResponse
import co.edu.iub.myparcialapp.dto.responses.ReporteIngresosResponse
import co.edu.iub.myparcialapp.dto.responses.ReporteVuelosResponse
import co.edu.iub.myparcialapp.services.ReporteService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes", description = "Generación de reportes")
class ReporteController(
    private val reporteService: ReporteService
) {

    @PostMapping("/vuelos-mas-reservados")
    @Operation(summary = "Reporte de vuelos más reservados", description = "Solo administradores")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    fun getVuelosMasReservados(@Valid @RequestBody request: ReportePeriodoRequest): ResponseEntity<ReporteVuelosResponse> {
        val reporte = reporteService.getVuelosMasReservados(request)
        return ResponseEntity.ok(reporte)
    }

    @PostMapping("/ingresos")
    @Operation(summary = "Reporte de ingresos por período", description = "Solo administradores")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    fun getReporteIngresos(@Valid @RequestBody request: ReportePeriodoRequest): ResponseEntity<ReporteIngresosResponse> {
        val reporte = reporteService.getReporteIngresos(request)
        return ResponseEntity.ok(reporte)
    }

    @GetMapping("/estadisticas-generales")
    @Operation(
        summary = "Estadísticas generales del sistema",
        description = "Endpoint público para obtener métricas generales"
    )
    fun getEstadisticasGenerales(): ResponseEntity<EstadisticasGeneralesResponse> {
        val estadisticas = reporteService.getEstadisticasGenerales()
        return ResponseEntity.ok(estadisticas)
    }
}