package co.edu.iub.myparcialapp.services

import co.edu.iub.myparcialapp.dto.responses.PasajeroResponse
import co.edu.iub.myparcialapp.entities.Pasajero
import co.edu.iub.myparcialapp.repositories.PasajeroRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class PasajeroService(
    private val pasajeroRepository: PasajeroRepository
) {

    fun mapToPasajeroResponse(pasajero: Pasajero): PasajeroResponse {
        return PasajeroResponse(
            id = pasajero.id,
            nombre = pasajero.nombre,
            documento = pasajero.documento,
            edad = pasajero.edad,
            email = pasajero.email,
            telefono = pasajero.telefono
        )
    }

    fun getPasajerosByVuelo(vueloId: Long): List<PasajeroResponse> {
        return pasajeroRepository.findByVueloId(vueloId).map { mapToPasajeroResponse(it) }
    }

    fun getPasajerosByReserva(reservaId: Long): List<PasajeroResponse> {
        return pasajeroRepository.findByReservaId(reservaId).map { mapToPasajeroResponse(it) }
    }
}