package co.edu.iub.myparcialapp.services

import co.edu.iub.myparcialapp.dto.requests.CreateReservaRequest
import co.edu.iub.myparcialapp.dto.responses.ReservaResponse
import co.edu.iub.myparcialapp.entities.EstadoReserva
import co.edu.iub.myparcialapp.entities.EstadoVuelo
import co.edu.iub.myparcialapp.entities.Pasajero
import co.edu.iub.myparcialapp.entities.Reserva
import co.edu.iub.myparcialapp.exception.ResourceNotFoundException
import co.edu.iub.myparcialapp.exception.BadRequestException
import co.edu.iub.myparcialapp.repositories.PasajeroRepository
import co.edu.iub.myparcialapp.repositories.ReservaRepository
import co.edu.iub.myparcialapp.repositories.UserRepository
import co.edu.iub.myparcialapp.repositories.VueloRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.Instant

@Service
@Transactional
class ReservaService(
    private val reservaRepository: ReservaRepository,
    private val vueloRepository: VueloRepository,
    private val vueloService: VueloService,
    private val userService: UserService,
    private val pasajeroService: PasajeroService,
    private val userRepository: UserRepository,
    private val pasajeroRepository: PasajeroRepository,
    private val emailService: EmailService
) {

    private val logger = LoggerFactory.getLogger(ReservaService::class.java)

    fun mapToReservaResponse(reserva: Reserva): ReservaResponse {
        val puedeCancel = puedeCapcelReserva(reserva)

        return ReservaResponse(
            id = reserva.id,
            codigoReserva = reserva.codigoReserva,
            vuelo = vueloService.mapToVueloResponse(reserva.vuelo),
            cliente = userService.mapToUserResponse(reserva.cliente),
            cantidadPasajeros = reserva.cantidadPasajeros,
            total = reserva.total,
            estado = reserva.estado,
            fechaReserva = reserva.fechaReserva,
            pasajeros = reserva.pasajeros.map { pasajeroService.mapToPasajeroResponse(it) },
            puedeCancel = puedeCancel
        )
    }

    private fun puedeCapcelReserva(reserva: Reserva): Boolean {
        if (reserva.estado != EstadoReserva.CONFIRMADA) return false

        val dosHorasAntes = reserva.vuelo.fechaSalida.minusSeconds(2 * 60 * 60) // 2 horas en segundos
        return Instant.now().isBefore(dosHorasAntes)
    }

    fun createReserva(request: CreateReservaRequest, clienteId: Long): ReservaResponse {
        logger.info("Creando reserva para vuelo ${request.vueloId} por cliente $clienteId")

        val vuelo = vueloRepository.findById(request.vueloId).orElseThrow {
            logger.error("Vuelo con ID ${request.vueloId} no encontrado")
            ResourceNotFoundException("Vuelo con ID ${request.vueloId} no encontrado")
        }

        // Validaciones de negocio
        if (vuelo.estado != EstadoVuelo.PROGRAMADO) {
            logger.warn("Intento de reservar vuelo ${vuelo.id} con estado ${vuelo.estado}")
            throw BadRequestException("Solo se pueden reservar vuelos programados")
        }

        if (vuelo.asientosDisponibles < request.pasajeros.size) {
            logger.warn("No hay suficientes asientos disponibles para vuelo ${vuelo.id}")
            throw BadRequestException("No hay suficientes asientos disponibles. Disponibles: ${vuelo.asientosDisponibles}")
        }

        val cliente = userRepository.findById(clienteId).orElseThrow {
            logger.error("Cliente con ID $clienteId no encontrado")
            ResourceNotFoundException("Cliente con ID $clienteId no encontrado")
        }

        val codigoReserva = generateCodigoReserva()
        val total = vuelo.precio.multiply(BigDecimal(request.pasajeros.size))

        val reserva = Reserva(
            codigoReserva = codigoReserva,
            vuelo = vuelo,
            cliente = cliente,
            cantidadPasajeros = request.pasajeros.size,
            total = total,
            estado = EstadoReserva.CONFIRMADA,
            fechaReserva = Instant.now(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val savedReserva = reservaRepository.save(reserva)
        logger.info("Reserva ${savedReserva.id} creada correctamente")

        emailService.enviarConfirmacion(savedReserva)

        // Crear pasajeros
        val pasajeros = request.pasajeros.map { pasajeroRequest ->
            Pasajero(
                nombre = pasajeroRequest.nombre,
                documento = pasajeroRequest.documento,
                edad = pasajeroRequest.edad,
                email = pasajeroRequest.email,
                telefono = pasajeroRequest.telefono,
                reserva = savedReserva,
                createdAt = Instant.now()
            )
        }

        pasajeroRepository.saveAll(pasajeros)

        // Actualizar asientos disponibles del vuelo
        val vueloActualizado = vuelo.copy(
            asientosDisponibles = vuelo.asientosDisponibles - request.pasajeros.size,
            updatedAt = Instant.now()
        )
        vueloRepository.save(vueloActualizado)

        return mapToReservaResponse(savedReserva.copy(pasajeros = pasajeros))
    }

    fun getReservasByCliente(clienteId: Long): List<ReservaResponse> {
        return reservaRepository.findByClienteId(clienteId).map { mapToReservaResponse(it) }
    }

    fun getAllReservas(): List<ReservaResponse> {
        return reservaRepository.findAll().map { mapToReservaResponse(it) }
    }

    fun getReservaById(id: Long): ReservaResponse {
        val reserva = reservaRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Reserva con ID $id no encontrada")
        }
        return mapToReservaResponse(reserva)
    }

    fun getReservaByCodigoReserva(codigoReserva: String): ReservaResponse {
        val reserva = reservaRepository.findByCodigoReserva(codigoReserva) ?: throw ResourceNotFoundException("Reserva con código $codigoReserva no encontrada")
        return mapToReservaResponse(reserva)
    }

    fun cancelReserva(id: Long, clienteId: Long): ReservaResponse {
        logger.info("Cancelando reserva $id por cliente $clienteId")

        val reserva = reservaRepository.findById(id).orElseThrow {
            logger.error("Reserva con ID $id no encontrada")
            ResourceNotFoundException("Reserva con ID $id no encontrada")
        }

        // Verificar que la reserva pertenece al cliente
        if (reserva.cliente.id != clienteId) {
            logger.warn("Cliente $clienteId intentó cancelar reserva $id que no le pertenece")
            throw BadRequestException("No tiene permisos para cancelar esta reserva")
        }

        if (!puedeCapcelReserva(reserva)) {
            logger.warn("No se puede cancelar la reserva $id. Debe cancelar con antelación o la reserva ya no está confirmada")
            throw BadRequestException("No se puede cancelar la reserva. Debe cancelar al menos 2 horas antes del vuelo o la reserva ya no está confirmada")
        }

        val reservaCancelada = reserva.copy(
            estado = EstadoReserva.CANCELADA,
            updatedAt = Instant.now()
        )

        // Devolver asientos disponibles al vuelo
        val vuelo = reserva.vuelo
        val vueloActualizado = vuelo.copy(
            asientosDisponibles = vuelo.asientosDisponibles + reserva.cantidadPasajeros,
            updatedAt = Instant.now()
        )
        vueloRepository.save(vueloActualizado)

        val savedReserva = reservaRepository.save(reservaCancelada)
        logger.info("Reserva $id cancelada correctamente")
        emailService.enviarCancelacion(savedReserva)
        return mapToReservaResponse(savedReserva)
    }

    private fun generateCodigoReserva(): String {
        val timestamp = System.currentTimeMillis().toString().takeLast(6)
        val randomLetters = (1..3).map { ('A'..'Z').random() }.joinToString("")
        return "AT$randomLetters$timestamp"
    }
}
