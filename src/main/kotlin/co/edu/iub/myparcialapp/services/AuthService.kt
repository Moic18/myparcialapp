package co.edu.iub.myparcialapp.services

import co.edu.iub.myparcialapp.dto.requests.LoginRequest
import co.edu.iub.myparcialapp.dto.responses.AuthResponse
import co.edu.iub.myparcialapp.repositories.UserRepository
import co.edu.iub.myparcialapp.utils.JwtUtils
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService,
    private val jwtUtils: JwtUtils,
    private val userService: UserService,
    private val userRepository: UserRepository
) {
    fun login(loginRequest: LoginRequest): AuthResponse {
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    loginRequest.email,
                    loginRequest.password
                )
            )
        } catch (e: Exception) {
            throw RuntimeException("Credenciales inválidas")
        }

        val userDetails = userDetailsService.loadUserByUsername(loginRequest.email)
        val jwtToken = jwtUtils.generateToken(userDetails)

        val user = userRepository.findByEmail(loginRequest.email)
            ?: throw RuntimeException("Usuario no encontrado con email ${loginRequest.email}")

        val userResponse = userService.mapToUserResponse(user)

        return AuthResponse(
            token = jwtToken,
            user = userResponse
        )
    }
}