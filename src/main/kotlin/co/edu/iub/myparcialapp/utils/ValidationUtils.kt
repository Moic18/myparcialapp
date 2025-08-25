package co.edu.iub.myparcialapp.utils

import co.edu.iub.myparcialapp.exception.BadRequestException
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit


@Component
object ValidationUtils {

    fun isValidEmail(email: String): Boolean {
        val emailPattern = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
        return email.matches(emailPattern.toRegex())
    }

    fun isValidPhone(phone: String?): Boolean {
        if (phone.isNullOrBlank()) return true
        val phonePattern = "^\\+?[1-9]\\d{1,14}$"
        return phone.matches(phonePattern.toRegex())
    }

    fun isValidDocumento(documento: String): Boolean {
        return documento.matches("^[A-Z0-9]{5,20}$".toRegex())
    }

    fun isValidCodigoVuelo(codigo: String): Boolean {
        return codigo.matches("^[A-Z]{2,3}[0-9]{3,4}$".toRegex())
    }

    fun isValidCodigoAeronave(codigo: String): Boolean {
        return codigo.matches("^[A-Z]{2,3}[0-9]{3,4}$".toRegex())
    }

    fun validateFechaVuelo(fechaSalida: Instant, fechaLlegada: Instant) {
        val now = Instant.now()

        if (fechaSalida.isBefore(now)) {
            throw BadRequestException("La fecha de salida debe ser futura")
        }

        if (fechaLlegada.isBefore(fechaSalida)) {
            throw BadRequestException("La fecha de llegada debe ser posterior a la fecha de salida")
        }

        val maxAdvance = now.plus(365, ChronoUnit.DAYS) // Máximo 1 año de anticipación
        if (fechaSalida.isAfter(maxAdvance)) {
            throw BadRequestException("No se pueden crear vuelos con más de 1 año de anticipación")
        }
    }

    fun validateReservaTiming(fechaVuelo: Instant) {
        val now = Instant.now()
        val dosHorasAntes = fechaVuelo.minusSeconds(2 * 60 * 60)

        if (now.isAfter(dosHorasAntes)) {
            throw BadRequestException("No se pueden hacer reservas con menos de 2 horas de anticipación")
        }
    }

    fun validateCancelacionTiming(fechaVuelo: Instant) {
        val now = Instant.now()
        val dosHorasAntes = fechaVuelo.minusSeconds(2 * 60 * 60)

        if (now.isAfter(dosHorasAntes)) {
            throw BadRequestException("No se pueden cancelar reservas con menos de 2 horas de anticipación")
        }
    }
}