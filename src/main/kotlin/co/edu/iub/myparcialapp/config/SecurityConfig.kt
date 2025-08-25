package co.edu.iub.myparcialapp.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val customUserDetailsService: CustomUserDetailsService,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val authenticationProvider = DaoAuthenticationProvider()
        authenticationProvider.setUserDetailsService(customUserDetailsService)
        authenticationProvider.setPasswordEncoder(passwordEncoder())
        return authenticationProvider
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    // Endpoints completamente públicos
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/api/health", "/api/info").permitAll()

                    // Registro de usuarios (público para que los clientes se puedan registrar)
                    .requestMatchers("/api/usuarios").permitAll()

                    // Swagger/OpenAPI - TODOS los paths necesarios
                    .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/v3/api-docs",
                        "/swagger-resources/**",
                        "/swagger-resources",
                        "/webjars/**",
                        "/swagger-ui/index.html"
                    ).permitAll()

                    // H2 Console (solo para desarrollo)
                    .requestMatchers("/h2-console/**").permitAll()

                    // Endpoints públicos para consultas básicas
                    .requestMatchers("/api/vuelos/buscar").permitAll()
                    .requestMatchers("/api/vuelos/{id}").permitAll()
                    .requestMatchers("/api/vuelos").permitAll() // Hacer públicos todos los vuelos para testing
                    .requestMatchers("/api/reservas/codigo/**").permitAll() // Consulta por código
                    .requestMatchers("/api/aeronaves").permitAll() // Público para testing
                    .requestMatchers("/", "/index.html", "/app.html", "/css/**", "/js/**").permitAll()
                    .requestMatchers("/api/reportes/estadisticas-generales").permitAll()

                    // Todos los demás endpoints requieren autenticación
                    .anyRequest().authenticated()

            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        // Para H2 Console - deshabilitar headers de frame
        http.headers { headers ->
            headers.frameOptions { frameOptions ->
                frameOptions.disable()
            }
        }

        return http.build()
    }
}