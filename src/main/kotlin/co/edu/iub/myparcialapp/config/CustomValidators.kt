package co.edu.iub.myparcialapp.config

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.stereotype.Component
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Component
class FlightNumberValidator : ConstraintValidator<ValidFlightNumber, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        return value?.matches("^[A-Z]{2,3}[0-9]{3,4}$".toRegex()) ?: false
    }
}

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [FlightNumberValidator::class])
annotation class ValidFlightNumber(
    val message: String = "Número de vuelo inválido. Formato: XX000 o XXX000",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)