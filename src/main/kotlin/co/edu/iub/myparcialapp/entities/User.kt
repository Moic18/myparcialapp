package co.edu.iub.myparcialapp.entities

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "usuarios")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "nombre", nullable = false)
    val name: String,

    @Column(name = "email", nullable = false, unique = true)
    val email: String,

    @Column(name = "password", nullable = false)
    val password: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    val role: UserRole,

    @Column(name = "telefono")
    val phone: String? = null,

    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true,

    @Column(name = "created_at")
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at")
    val updatedAt: Instant = Instant.now(),

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    val reservas: List<Reserva> = emptyList()
)

enum class UserRole {
    CLIENTE, EMPLEADO, ADMINISTRADOR
}