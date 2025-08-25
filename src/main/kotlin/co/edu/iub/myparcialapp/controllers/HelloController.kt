package co.edu.iub.myparcialapp.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/api")
@Tag(name = "General", description = "Endpoints generales y de testing")
class HelloController {

    @GetMapping("/health")
    @Operation(summary = "Verificar estado del sistema")
    fun healthCheck(): ResponseEntity<Map<String, Any>> {
        val response = mapOf(
            "status" to "UP",
            "timestamp" to Instant.now(),
            "service" to "AeroTech Flight Management",
            "version" to "1.0.0"
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping("/info")
    @Operation(summary = "Información del sistema")
    fun info(): ResponseEntity<Map<String, String>> {
        val response = mapOf(
            "application" to "AeroTech Flight Management System",
            "description" to "Sistema de gestión de reservas de vuelos",
            "version" to "1.0.0",
            "documentation" to "/swagger-ui.html",
            "apiDocs" to "/v3/api-docs"
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping("/test")
    @Operation(summary = "Endpoint de prueba público")
    fun test(): ResponseEntity<Map<String, Any>> {
        val response = mapOf(
            "message" to "¡API funcionando correctamente!",
            "timestamp" to Instant.now(),
            "endpoints" to mapOf(
                "login" to "/api/auth/login",
                "register" to "/api/usuarios",
                "flights" to "/api/vuelos",
                "swagger" to "/swagger-ui.html"
            )
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping("/")
    @Operation(summary = "Endpoint raíz")
    fun root(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf(
            "message" to "Bienvenido a AeroTech API",
            "docs" to "/swagger-ui.html",
            "status" to "/api/health"
        ))
    }
}