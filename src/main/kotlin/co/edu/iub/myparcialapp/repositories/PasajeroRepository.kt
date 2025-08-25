package co.edu.iub.myparcialapp.repositories

import co.edu.iub.myparcialapp.entities.Pasajero
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PasajeroRepository : JpaRepository<Pasajero, Long> {
    fun findByReservaId(reservaId: Long): List<Pasajero>
    fun findByDocumento(documento: String): Pasajero?

    @Query("SELECT p FROM Pasajero p WHERE p.reserva.vuelo.id = :vueloId")
    fun findByVueloId(vueloId: Long): List<Pasajero>

    @Query("SELECT COUNT(p) FROM Pasajero p WHERE p.reserva.vuelo.id = :vueloId")
    fun countPasajerosByVueloId(vueloId: Long): Long
}