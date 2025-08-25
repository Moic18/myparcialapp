package co.edu.iub.myparcialapp.repositories

import co.edu.iub.myparcialapp.entities.EstadoReserva
import co.edu.iub.myparcialapp.entities.Reserva
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository


@Repository
interface ReservaRepository : JpaRepository<Reserva, Long> {
    fun findByCodigoReserva(codigoReserva: String): Reserva?
    fun findByClienteId(clienteId: Long): List<Reserva>
    fun findByVueloId(vueloId: Long): List<Reserva>
    fun findByEstado(estado: EstadoReserva): List<Reserva>

    @Query("SELECT r FROM Reserva r WHERE r.cliente.id = :clienteId AND r.estado = :estado")
    fun findByClienteIdAndEstado(clienteId: Long, estado: EstadoReserva): List<Reserva>

    @Query("SELECT r FROM Reserva r WHERE r.fechaReserva BETWEEN :fechaInicio AND :fechaFin")
    fun findByFechaReservaBetween(fechaInicio: java.time.Instant, fechaFin: java.time.Instant): List<Reserva>

    @Query("SELECT SUM(r.total) FROM Reserva r WHERE r.fechaReserva BETWEEN :fechaInicio AND :fechaFin AND r.estado = 'CONFIRMADA'")
    fun sumTotalByFechaBetween(fechaInicio: java.time.Instant, fechaFin: java.time.Instant): java.math.BigDecimal?

    @Query("SELECT v.numeroVuelo, COUNT(r) as reservas FROM Reserva r JOIN r.vuelo v WHERE r.estado = 'CONFIRMADA' GROUP BY v.id ORDER BY reservas DESC")
    fun findVuelosMasReservados(): List<Array<Any>>
}

