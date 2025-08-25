package co.edu.iub.myparcialapp.repositories

import co.edu.iub.myparcialapp.entities.User
import co.edu.iub.myparcialapp.entities.UserRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
    fun findByRole(role: UserRole): List<User>
    fun findByIsActiveTrue(): List<User>
    fun countByRole(role: UserRole): Long
}