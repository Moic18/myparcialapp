package co.edu.iub.myparcialapp.entities

import jakarta.persistence.*
import java.time.Instant


@Entity
@Table(name = "reservas")
data class Reserva(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "codigo_reserva", nullable = false, unique = true)
    val codigoReserva: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vuelo_id", nullable = false)
    val vuelo: Vuelo,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    val cliente: User,

    @Column(name = "cantidad_pasajeros", nullable = false)
    val cantidadPasajeros: Int,

    @Column(name = "total", nullable = false, precision = 19, scale = 2)
    val total: java.math.BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    val estado: EstadoReserva = EstadoReserva.CONFIRMADA,

    @Column(name = "fecha_reserva", nullable = false)
    val fechaReserva: Instant = Instant.now(),

    @Column(name = "created_at")
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at")
    val updatedAt: Instant = Instant.now(),

    @OneToMany(mappedBy = "reserva", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val pasajeros: List<Pasajero> = emptyList()
)

enum class EstadoReserva {
    CONFIRMADA, CANCELADA, COMPLETADA
}