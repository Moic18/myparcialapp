package co.edu.iub.myparcialapp.repositories

import co.edu.iub.myparcialapp.entities.Aeronave
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AeronaveRepository : JpaRepository<Aeronave, Long> {
    fun findByCodigo(codigo: String): Aeronave?
    fun existsByCodigo(codigo: String): Boolean
    fun findByIsActiveTrue(): List<Aeronave>
    fun findByCodigoAndIdNot(codigo: String, id: Long): Aeronave?
}