package co.edu.iub.myparcialapp.utils

import co.edu.iub.myparcialapp.entities.Aeronave
import co.edu.iub.myparcialapp.entities.User
import co.edu.iub.myparcialapp.entities.UserRole
import co.edu.iub.myparcialapp.entities.*
import co.edu.iub.myparcialapp.repositories.AeronaveRepository
import co.edu.iub.myparcialapp.repositories.UserRepository
import co.edu.iub.myparcialapp.repositories.VueloRepository
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.Instant

@Component
class DataInitializer(
    private val userRepository: UserRepository,
    private val aeronaveRepository: AeronaveRepository,
    private val vueloRepository: VueloRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @EventListener(ApplicationReadyEvent::class)
    fun initializeData() {
        createInitialUsers()
        createInitialAeronaves()
        createInitialVuelos()
    }

    private fun createInitialUsers() {
        if (userRepository.count() == 0L) {
            // Crear administrador por defecto
            val admin = User(
                name = "Administrador Sistema",
                email = "admin@aerotech.com",
                password = passwordEncoder.encode("admin123"),
                role = UserRole.ADMINISTRADOR,
                phone = "+57 300 123 4567",
                isActive = true,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )

            // Crear empleado por defecto
            val empleado = User(
                name = "Juan Empleado",
                email = "empleado@aerotech.com",
                password = passwordEncoder.encode("empleado123"),
                role = UserRole.EMPLEADO,
                phone = "+57 300 234 5678",
                isActive = true,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )

            // Crear cliente por defecto
            val cliente = User(
                name = "María Cliente",
                email = "cliente@aerotech.com",
                password = passwordEncoder.encode("cliente123"),
                role = UserRole.CLIENTE,
                phone = "+57 300 345 6789",
                isActive = true,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )

            userRepository.saveAll(listOf(admin, empleado, cliente))
            println("Usuarios iniciales creados:")
            println("Admin: admin@aerotech.com / admin123")
            println("Empleado: empleado@aerotech.com / empleado123")
            println("Cliente: cliente@aerotech.com / cliente123")
        }
    }

    private fun createInitialAeronaves() {
        if (aeronaveRepository.count() == 0L) {
            val aeronaves = listOf(
                Aeronave(
                    modelo = "Boeing 737-800",
                    capacidad = 189,
                    codigo = "AT001",
                    isActive = true,
                    createdAt = Instant.now()
                ),
                Aeronave(
                    modelo = "Airbus A320",
                    capacidad = 150,
                    codigo = "AT002",
                    isActive = true,
                    createdAt = Instant.now()
                ),
                Aeronave(
                    modelo = "Embraer E190",
                    capacidad = 100,
                    codigo = "AT003",
                    isActive = true,
                    createdAt = Instant.now()
                )
            )

            aeronaveRepository.saveAll(aeronaves)
            println("Aeronaves iniciales creadas")
        }
    }

    private fun createInitialVuelos() {
        if (vueloRepository.count() == 0L) {
            val aeronaves = aeronaveRepository.findAll()
            if (aeronaves.isNotEmpty()) {
                val vuelos = listOf(
                    Vuelo(
                        numeroVuelo = "AT1001",
                        origen = "Bogotá",
                        destino = "Medellín",
                        fechaSalida = Instant.parse("2024-07-01T08:00:00Z"),
                        fechaLlegada = Instant.parse("2024-07-01T09:00:00Z"),
                        precio = BigDecimal("150000"),
                        estado = EstadoVuelo.PROGRAMADO,
                        asientosDisponibles = 150,
                        aeronave = aeronaves[0]
                    ),
                    Vuelo(
                        numeroVuelo = "AT1002",
                        origen = "Cali",
                        destino = "Cartagena",
                        fechaSalida = Instant.parse("2024-07-02T10:00:00Z"),
                        fechaLlegada = Instant.parse("2024-07-02T12:00:00Z"),
                        precio = BigDecimal("200000"),
                        estado = EstadoVuelo.PROGRAMADO,
                        asientosDisponibles = 120,
                        aeronave = aeronaves.getOrElse(1) { aeronaves[0] }
                    ),
                    Vuelo(
                        numeroVuelo = "AT1003",
                        origen = "Barranquilla",
                        destino = "Bogotá",
                        fechaSalida = Instant.parse("2024-07-03T14:00:00Z"),
                        fechaLlegada = Instant.parse("2024-07-03T15:30:00Z"),
                        precio = BigDecimal("180000"),
                        estado = EstadoVuelo.PROGRAMADO,
                        asientosDisponibles = 100,
                        aeronave = aeronaves.getOrElse(2) { aeronaves[0] }
                    )
                )
                vueloRepository.saveAll(vuelos)
                println("Vuelos iniciales creados")
            }
        }
    }
}