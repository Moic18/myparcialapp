package co.edu.iub.myparcialapp.services

import co.edu.iub.myparcialapp.dto.requests.CreateUserRequest
import co.edu.iub.myparcialapp.exception.ConflictException
import co.edu.iub.myparcialapp.dto.responses.UserResponse
import co.edu.iub.myparcialapp.entities.User
import co.edu.iub.myparcialapp.entities.UserRole
import co.edu.iub.myparcialapp.exception.ResourceNotFoundException
import co.edu.iub.myparcialapp.repositories.UserRepository
import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun mapToUserResponse(user: User): UserResponse {
        return UserResponse(
            id = user.id,
            name = user.name,
            email = user.email,
            role = user.role,
            phone = user.phone,
            isActive = user.isActive,
            createdAt = user.createdAt
        )
    }

    fun getAllUsers(): List<UserResponse> {
        return userRepository.findByIsActiveTrue().map { mapToUserResponse(it) }
    }

    fun getUsersByRole(role: UserRole): List<UserResponse> {
        return userRepository.findByRole(role).filter { it.isActive }.map { mapToUserResponse(it) }
    }

    fun createUser(request: CreateUserRequest): UserResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw ConflictException("El email ${request.email} ya está en uso")
        }

        val user = User(
            name = request.name,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            role = request.role,
            phone = request.phone,
            isActive = true,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val savedUser = userRepository.save(user)
        return mapToUserResponse(savedUser)
    }

    fun updateUser(request: CreateUserRequest, id: Long): UserResponse {
        val existingUser = userRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Usuario con ID $id no encontrado")
        }

        val userWithEmail = userRepository.findByEmail(request.email)
        if (userWithEmail != null && userWithEmail.id != id) {
            throw ConflictException("El email ${request.email} ya está en uso")
        }

        val updatedUser = existingUser.copy(
            name = request.name,
            email = request.email,
            password = if (request.password.isNotBlank()) passwordEncoder.encode(request.password) else existingUser.password,
            role = request.role,
            phone = request.phone,
            updatedAt = Instant.now()
        )

        val savedUser = userRepository.save(updatedUser)
        return mapToUserResponse(savedUser)
    }

    fun deleteUser(id: Long): Boolean {
        val user = userRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Usuario con ID $id no encontrado")
        }

        val inactiveUser = user.copy(
            isActive = false,
            updatedAt = Instant.now()
        )

        userRepository.save(inactiveUser)
        return true
    }

    fun getUserById(id: Long): UserResponse {
        val user = userRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Usuario con ID $id no encontrado")
        }
        return mapToUserResponse(user)
    }
}