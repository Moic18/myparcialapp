package co.edu.iub.myparcialapp.entities

import jakarta.persistence.*
import java.time.Instant


@Entity
@Table(name = "aeronaves")
data class Aeronave(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "modelo", nullable = false)
    val modelo: String,

    @Column(name = "capacidad", nullable = false)
    val capacidad: Int,

    @Column(name = "codigo", nullable = false, unique = true)
    val codigo: String,

    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true,

    @Column(name = "created_at")
    val createdAt: Instant = Instant.now(),

    @OneToMany(mappedBy = "aeronave", fetch = FetchType.LAZY)
    val vuelos: List<Vuelo> = emptyList()
)