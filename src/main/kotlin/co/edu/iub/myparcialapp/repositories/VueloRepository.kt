package co.edu.iub.myparcialapp.repositories

import co.edu.iub.myparcialapp.entities.EstadoVuelo
import co.edu.iub.myparcialapp.entities.Vuelo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository


@Repository
interface VueloRepository : JpaRepository<Vuelo, Long> {
    fun findByNumeroVuelo(numeroVuelo: String): Vuelo?
    fun existsByNumeroVuelo(numeroVuelo: String): Boolean

    @Query("SELECT v FROM Vuelo v WHERE v.origen = :origen AND v.destino = :destino AND DATE(v.fechaSalida) = DATE(:fecha)")
    fun findByOrigenAndDestinoAndFecha(origen: String, destino: String, fecha: java.time.Instant): List<Vuelo>

    @Query("SELECT v FROM Vuelo v WHERE v.estado = :estado")
    fun findByEstado(estado: EstadoVuelo): List<Vuelo>

    @Query("SELECT v FROM Vuelo v WHERE v.origen = :origen OR v.destino = :destino")
    fun findByOrigenOrDestino(origen: String, destino: String): List<Vuelo>

    @Query("SELECT v FROM Vuelo v WHERE v.fechaSalida BETWEEN :fechaInicio AND :fechaFin")
    fun findByFechaSalidaBetween(fechaInicio: java.time.Instant, fechaFin: java.time.Instant): List<Vuelo>

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.vuelo.id = :vueloId")
    fun countReservasByVueloId(vueloId: Long): Long
}