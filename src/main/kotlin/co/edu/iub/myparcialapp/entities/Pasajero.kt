package co.edu.iub.myparcialapp.entities

import jakarta.persistence.*
import java.time.Instant


@Entity
@Table(name = "pasajeros")
data class Pasajero(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "nombre", nullable = false)
    val nombre: String,

    @Column(name = "documento", nullable = false)
    val documento: String,

    @Column(name = "edad", nullable = false)
    val edad: Int,

    @Column(name = "email")
    val email: String? = null,

    @Column(name = "telefono")
    val telefono: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", nullable = false)
    val reserva: Reserva,

    @Column(name = "created_at")
    val createdAt: Instant = Instant.now()
)