package co.edu.iub.myparcialapp.entities


import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "vuelos")
data class Vuelo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "numero_vuelo", nullable = false, unique = true)
    val numeroVuelo: String,

    @Column(name = "origen", nullable = false)
    val origen: String,

    @Column(name = "destino", nullable = false)
    val destino: String,

    @Column(name = "fecha_salida", nullable = false)
    val fechaSalida: Instant,

    @Column(name = "fecha_llegada", nullable = false)
    val fechaLlegada: Instant,

    @Column(name = "precio", nullable = false, precision = 19, scale = 2)
    val precio: java.math.BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    val estado: EstadoVuelo = EstadoVuelo.PROGRAMADO,

    @Column(name = "asientos_disponibles", nullable = false)
    val asientosDisponibles: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aeronave_id", nullable = false)
    val aeronave: Aeronave,

    @Column(name = "created_at")
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at")
    val updatedAt: Instant = Instant.now(),

    @OneToMany(mappedBy = "vuelo", fetch = FetchType.LAZY)
    val reservas: List<Reserva> = emptyList()
)

enum class EstadoVuelo {
    PROGRAMADO, RETRASADO, CANCELADO, COMPLETADO
}