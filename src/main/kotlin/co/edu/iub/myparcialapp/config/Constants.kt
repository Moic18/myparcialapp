package co.edu.iub.myparcialapp.config

object appConstants {

    // Estados del sistema
    const val STATUS_ACTIVE = true
    const val STATUS_INACTIVE = false

    // Códigos de error
    const val ERROR_RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND"
    const val ERROR_VALIDATION = "VALIDATION_ERROR"
    const val ERROR_UNAUTHORIZED = "UNAUTHORIZED"
    const val ERROR_FORBIDDEN = "FORBIDDEN"
    const val ERROR_CONFLICT = "CONFLICT"
    const val ERROR_BUSINESS_RULE = "BUSINESS_RULE_VIOLATION"

    // Límites del sistema
    const val MAX_PASSENGERS_PER_RESERVATION = 9
    const val MIN_HOURS_BEFORE_FLIGHT = 2
    const val MAX_AIRCRAFT_CAPACITY = 1000
    const val MAX_FLIGHT_ADVANCE_DAYS = 365

    // Formatos
    const val RESERVATION_CODE_PREFIX = "AT"
    const val DATE_FORMAT = "yyyy-MM-dd"
    const val DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss"

    // Roles
    object Roles {
        const val CLIENTE = "CLIENTE"
        const val EMPLEADO = "EMPLEADO"
        const val ADMINISTRADOR = "ADMINISTRADOR"
    }

    // Estados de vuelo
    object FlightStates {
        const val PROGRAMADO = "PROGRAMADO"
        const val RETRASADO = "RETRASADO"
        const val CANCELADO = "CANCELADO"
        const val COMPLETADO = "COMPLETADO"
    }

    // Estados de reserva
    object ReservationStates {
        const val CONFIRMADA = "CONFIRMADA"
        const val CANCELADA = "CANCELADA"
        const val COMPLETADA = "COMPLETADA"
    }
}