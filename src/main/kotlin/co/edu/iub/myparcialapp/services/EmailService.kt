package co.edu.iub.myparcialapp.services

import co.edu.iub.myparcialapp.entities.Reserva
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
class EmailService(private val mailSender: JavaMailSender) {
    fun enviarConfirmacion(reserva: Reserva) {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)
        helper.setTo(reserva.cliente.email)
        helper.setSubject("Confirmación de reserva ${reserva.codigoReserva}")

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault())
        val fechaSalida = formatter.format(reserva.vuelo.fechaSalida)
        val fechaLlegada = formatter.format(reserva.vuelo.fechaLlegada)

        val duracion = Duration.between(reserva.vuelo.fechaSalida, reserva.vuelo.fechaLlegada)
        val horas = duracion.toHours()
        val minutos = duracion.toMinutes() % 60

        val htmlContent = """
            <html>
            <body>
                <p>Estimado/a ${reserva.cliente.name},</p>
                <p>Su reserva con código <strong>${reserva.codigoReserva}</strong> ha sido confirmada.</p>
                <table>
                    <tr><td><strong>Número de vuelo:</strong></td><td>${reserva.vuelo.numeroVuelo}</td></tr>
                    <tr><td><strong>Origen:</strong></td><td>${reserva.vuelo.origen}</td></tr>
                    <tr><td><strong>Destino:</strong></td><td>${reserva.vuelo.destino}</td></tr>
                    <tr><td><strong>Salida:</strong></td><td>$fechaSalida</td></tr>
                    <tr><td><strong>Llegada:</strong></td><td>$fechaLlegada</td></tr>
                    <tr><td><strong>Duración:</strong></td><td>${horas}h ${minutos}m</td></tr>
                    <tr><td><strong>Cantidad de pasajeros:</strong></td><td>${reserva.cantidadPasajeros}</td></tr>
                    <tr><td><strong>Total pagado:</strong></td><td>${reserva.total}</td></tr>
                </table>
                <p>Recomendaciones:</p>
                <ul>
                    <li>Presentarse 2&nbsp;h antes del vuelo.</li>
                    <li>Llevar documento de identidad.</li>
                </ul>
                <p>Gracias por elegirnos.</p>
                <p>Atentamente,<br/>AeroTech Airlines</p>
            </body>
            </html>
        """.trimIndent()

        helper.setText(htmlContent, true)
        mailSender.send(message)
    }

    fun enviarCancelacion(reserva: Reserva) {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)
        helper.setTo(reserva.cliente.email)
        helper.setSubject("Cancelación de reserva ${reserva.codigoReserva}")

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault())
        val fechaSalida = formatter.format(reserva.vuelo.fechaSalida)
        val fechaLlegada = formatter.format(reserva.vuelo.fechaLlegada)

        val duracion = Duration.between(reserva.vuelo.fechaSalida, reserva.vuelo.fechaLlegada)
        val horas = duracion.toHours()
        val minutos = duracion.toMinutes() % 60

        val htmlContent = """
            <html>
            <body>
                <p>Estimado/a ${reserva.cliente.name},</p>
                <p>Su reserva con código <strong>${reserva.codigoReserva}</strong> ha sido cancelada.</p>
                <table>
                    <tr><td><strong>Número de vuelo:</strong></td><td>${reserva.vuelo.numeroVuelo}</td></tr>
                    <tr><td><strong>Origen:</strong></td><td>${reserva.vuelo.origen}</td></tr>
                    <tr><td><strong>Destino:</strong></td><td>${reserva.vuelo.destino}</td></tr>
                    <tr><td><strong>Salida:</strong></td><td>$fechaSalida</td></tr>
                    <tr><td><strong>Llegada:</strong></td><td>$fechaLlegada</td></tr>
                    <tr><td><strong>Duración:</strong></td><td>${horas}h ${minutos}m</td></tr>
                    <tr><td><strong>Cantidad de pasajeros:</strong></td><td>${reserva.cantidadPasajeros}</td></tr>
                    <tr><td><strong>Total pagado:</strong></td><td>${reserva.total}</td></tr>
                </table>
                <p>Recomendaciones:</p>
                <ul>
                    <li>Presentarse 2&nbsp;h antes del vuelo.</li>
                    <li>Llevar documento de identidad.</li>
                </ul>
                <p>Lamentamos los inconvenientes.</p>
                <p>Atentamente,<br/>AeroTech Airlines</p>
            </body>
            </html>
        """.trimIndent()

        helper.setText(htmlContent, true)
        mailSender.send(message)
    }
}