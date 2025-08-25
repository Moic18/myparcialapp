package co.edu.iub.myparcialapp.config

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled


@Configuration
@EnableScheduling
class SchedulingConfig {

    @Scheduled(fixedRate = 3600000) // Cada hora
    fun updateFlightStatuses() {
        // Lógica para actualizar estados de vuelos automáticamente
        logger.info("Ejecutando actualización automática de estados de vuelos")
    }

    @Scheduled(cron = "0 0 1 * * ?") // Diariamente a la 1:00 AM
    fun cleanupExpiredReservations() {
        // Lógica para limpiar reservas expiradas
        logger.info("Ejecutando limpieza de reservas expiradas")
    }

    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(SchedulingConfig::class.java)
    }
}