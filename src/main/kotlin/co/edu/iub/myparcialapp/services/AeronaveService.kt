package co.edu.iub.myparcialapp.services

import co.edu.iub.myparcialapp.dto.requests.CreateAeronaveRequest
import co.edu.iub.myparcialapp.dto.responses.AeronaveResponse
import co.edu.iub.myparcialapp.entities.Aeronave
import co.edu.iub.myparcialapp.exception.ResourceNotFoundException
import co.edu.iub.myparcialapp.exception.ConflictException
import co.edu.iub.myparcialapp.repositories.AeronaveRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.Instant

@Service
@Transactional
class AeronaveService(
    private val aeronaveRepository: AeronaveRepository
) {

    fun mapToAeronaveResponse(aeronave: Aeronave): AeronaveResponse {
        return AeronaveResponse(
            id = aeronave.id,
            modelo = aeronave.modelo,
            capacidad = aeronave.capacidad,
            codigo = aeronave.codigo,
            isActive = aeronave.isActive,
            createdAt = aeronave.createdAt
        )
    }

    fun getAllAeronaves(): List<AeronaveResponse> {
        return aeronaveRepository.findByIsActiveTrue().map { mapToAeronaveResponse(it) }
    }

    fun getAeronaveById(id: Long): AeronaveResponse {
        val aeronave = aeronaveRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Aeronave con ID $id no encontrada")
        }
        return mapToAeronaveResponse(aeronave)
    }

    fun createAeronave(request: CreateAeronaveRequest): AeronaveResponse {
        if (aeronaveRepository.existsByCodigo(request.codigo)) {
            throw ConflictException("Ya existe una aeronave con el código ${request.codigo}")
        }

        val aeronave = Aeronave(
            modelo = request.modelo,
            capacidad = request.capacidad,
            codigo = request.codigo,
            isActive = true,
            createdAt = Instant.now()
        )

        val savedAeronave = aeronaveRepository.save(aeronave)
        return mapToAeronaveResponse(savedAeronave)
    }

    fun updateAeronave(request: CreateAeronaveRequest, id: Long): AeronaveResponse {
        val existingAeronave = aeronaveRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Aeronave con ID $id no encontrada")
        }

        val aeronaveConCodigo = aeronaveRepository.findByCodigoAndIdNot(request.codigo, id)
        if (aeronaveConCodigo != null) {
            throw ConflictException("Ya existe otra aeronave con el código ${request.codigo}")
        }

        val updatedAeronave = existingAeronave.copy(
            modelo = request.modelo,
            capacidad = request.capacidad,
            codigo = request.codigo
        )

        val savedAeronave = aeronaveRepository.save(updatedAeronave)
        return mapToAeronaveResponse(savedAeronave)
    }

    fun deleteAeronave(id: Long): Boolean {
        val aeronave = aeronaveRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Aeronave con ID $id no encontrada")
        }

        val inactiveAeronave = aeronave.copy(isActive = false)
        aeronaveRepository.save(inactiveAeronave)
        return true
    }
}