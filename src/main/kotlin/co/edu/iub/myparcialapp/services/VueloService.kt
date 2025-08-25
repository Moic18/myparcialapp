package co.edu.iub.myparcialapp.services

import co.edu.iub.myparcialapp.dto.requests.BuscarVuelosRequest
import co.edu.iub.myparcialapp.dto.requests.CreateVueloRequest
import co.edu.iub.myparcialapp.dto.requests.UpdateEstadoVueloRequest
import co.edu.iub.myparcialapp.dto.responses.BuscarVuelosResponse
import co.edu.iub.myparcialapp.dto.responses.VueloResponse
import co.edu.iub.myparcialapp.entities.EstadoVuelo
import co.edu.iub.myparcialapp.entities.Vuelo
import co.edu.iub.myparcialapp.exception.ResourceNotFoundException
import co.edu.iub.myparcialapp.exception.ConflictException
import co.edu.iub.myparcialapp.exception.BadRequestException
import co.edu.iub.myparcialapp.repositories.AeronaveRepository
import co.edu.iub.myparcialapp.repositories.VueloRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

@Service
@Transactional
class VueloService(
    private val vueloRepository: VueloRepository,
    private val aeronaveRepository: AeronaveRepository,
    private val aeronaveService: AeronaveService
) {

    fun mapToVueloResponse(vuelo: Vuelo): VueloResponse {
        val duracion = Duration.between(vuelo.fechaSalida, vuelo.fechaLlegada)
        val horas = duracion.toHours()
        val minutos = duracion.toMinutes() % 60

        return VueloResponse(
            id = vuelo.id,
            numeroVuelo = vuelo.numeroVuelo,
            origen = vuelo.origen,
            destino = vuelo.destino,
            fechaSalida = vuelo.fechaSalida,
            fechaLlegada = vuelo.fechaLlegada,
            precio = vuelo.precio,
            estado = vuelo.estado,
            asientosDisponibles = vuelo.asientosDisponibles,
            aeronave = aeronaveService.mapToAeronaveResponse(vuelo.aeronave),
            duracionVuelo = "${horas}h ${minutos}m",
            createdAt = vuelo.createdAt
        )
    }

    fun getAllVuelos(): List<VueloResponse> {
        return vueloRepository.findAll().map { mapToVueloResponse(it) }
    }

    fun getVueloById(id: Long): VueloResponse {
        val vuelo = vueloRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Vuelo con ID $id no encontrado")
        }
        return mapToVueloResponse(vuelo)
    }

    fun buscarVuelos(request: BuscarVuelosRequest): BuscarVuelosResponse {
        val fechaInstant = request.fecha.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val vuelos = vueloRepository.findByOrigenAndDestinoAndFecha(request.origen, request.destino, fechaInstant)
            .filter { it.estado == EstadoVuelo.PROGRAMADO && it.asientosDisponibles > 0 }
            .map { mapToVueloResponse(it) }

        return BuscarVuelosResponse(
            vuelos = vuelos,
            totalEncontrados = vuelos.size,
            criteriosBusqueda = request
        )
    }

    fun createVuelo(request: CreateVueloRequest): VueloResponse {
        // Validaciones de negocio
        if (request.origen.equals(request.destino, ignoreCase = true)) {
            throw BadRequestException("El origen y destino deben ser diferentes")
        }

        if (request.fechaLlegada.isBefore(request.fechaSalida)) {
            throw BadRequestException("La fecha de llegada debe ser posterior a la fecha de salida")
        }

        if (request.precio <= BigDecimal.ZERO) {
            throw BadRequestException("El precio debe ser mayor a 0")
        }

        if (vueloRepository.existsByNumeroVuelo(request.numeroVuelo)) {
            throw ConflictException("Ya existe un vuelo con el número ${request.numeroVuelo}")
        }

        val aeronave = aeronaveRepository.findById(request.aeronaveId).orElseThrow {
            ResourceNotFoundException("Aeronave con ID ${request.aeronaveId} no encontrada")
        }

        if (!aeronave.isActive) {
            throw BadRequestException("La aeronave seleccionada no está activa")
        }

        val vuelo = Vuelo(
            numeroVuelo = request.numeroVuelo,
            origen = request.origen,
            destino = request.destino,
            fechaSalida = request.fechaSalida,
            fechaLlegada = request.fechaLlegada,
            precio = request.precio,
            estado = EstadoVuelo.PROGRAMADO,
            asientosDisponibles = aeronave.capacidad,
            aeronave = aeronave,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val savedVuelo = vueloRepository.save(vuelo)
        return mapToVueloResponse(savedVuelo)
    }

    fun updateVuelo(request: CreateVueloRequest, id: Long): VueloResponse {
        val existingVuelo = vueloRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Vuelo con ID $id no encontrado")
        }

        // Validar que no se puede modificar un vuelo que no está programado
        if (existingVuelo.estado != EstadoVuelo.PROGRAMADO) {
            throw BadRequestException("No se puede modificar un vuelo que no está programado")
        }

        // Validaciones de negocio similares al crear
        if (request.origen.equals(request.destino, ignoreCase = true)) {
            throw BadRequestException("El origen y destino deben ser diferentes")
        }

        if (request.fechaLlegada.isBefore(request.fechaSalida)) {
            throw BadRequestException("La fecha de llegada debe ser posterior a la fecha de salida")
        }

        val vueloConNumero = vueloRepository.findByNumeroVuelo(request.numeroVuelo)
        if (vueloConNumero != null && vueloConNumero.id != id) {
            throw ConflictException("Ya existe otro vuelo con el número ${request.numeroVuelo}")
        }

        val aeronave = aeronaveRepository.findById(request.aeronaveId).orElseThrow {
            ResourceNotFoundException("Aeronave con ID ${request.aeronaveId} no encontrada")
        }

        val updatedVuelo = existingVuelo.copy(
            numeroVuelo = request.numeroVuelo,
            origen = request.origen,
            destino = request.destino,
            fechaSalida = request.fechaSalida,
            fechaLlegada = request.fechaLlegada,
            precio = request.precio,
            aeronave = aeronave,
            updatedAt = Instant.now()
        )

        val savedVuelo = vueloRepository.save(updatedVuelo)
        return mapToVueloResponse(savedVuelo)
    }

    fun updateEstadoVuelo(id: Long, request: UpdateEstadoVueloRequest): VueloResponse {
        val vuelo = vueloRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Vuelo con ID $id no encontrado")
        }

        val updatedVuelo = vuelo.copy(
            estado = request.estado,
            updatedAt = Instant.now()
        )

        val savedVuelo = vueloRepository.save(updatedVuelo)
        return mapToVueloResponse(savedVuelo)
    }

    fun getVuelosByEstado(estado: EstadoVuelo): List<VueloResponse> {
        return vueloRepository.findByEstado(estado).map { mapToVueloResponse(it) }
    }
}