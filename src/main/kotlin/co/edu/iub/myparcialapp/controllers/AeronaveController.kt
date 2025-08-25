package co.edu.iub.myparcialapp.controllers


import co.edu.iub.myparcialapp.dto.requests.CreateAeronaveRequest
import co.edu.iub.myparcialapp.dto.responses.AeronaveResponse
import co.edu.iub.myparcialapp.services.AeronaveService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/aeronaves")
@Tag(name = "Aeronaves", description = "Gestión de aeronaves")
class AeronaveController(
    private val aeronaveService: AeronaveService
) {

    @GetMapping
    @Operation(summary = "Obtener todas las aeronaves")
    fun getAllAeronaves(): ResponseEntity<List<AeronaveResponse>> {
        val aeronaves = aeronaveService.getAllAeronaves()
        return ResponseEntity.ok(aeronaves)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener aeronave por ID")
    fun getAeronaveById(@PathVariable id: Long): ResponseEntity<AeronaveResponse> {
        val aeronave = aeronaveService.getAeronaveById(id)
        return ResponseEntity.ok(aeronave)
    }

    @PostMapping
    @Operation(summary = "Crear aeronave", description = "Solo administradores")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    fun createAeronave(@Valid @RequestBody request: CreateAeronaveRequest): ResponseEntity<AeronaveResponse> {
        val aeronave = aeronaveService.createAeronave(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(aeronave)
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar aeronave", description = "Solo administradores")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    fun updateAeronave(
        @PathVariable id: Long,
        @Valid @RequestBody request: CreateAeronaveRequest
    ): ResponseEntity<AeronaveResponse> {
        val aeronave = aeronaveService.updateAeronave(request, id)
        return ResponseEntity.ok(aeronave)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar aeronave", description = "Solo administradores")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    fun deleteAeronave(@PathVariable id: Long): ResponseEntity<Void> {
        aeronaveService.deleteAeronave(id)
        return ResponseEntity.noContent().build()
    }
}