package co.edu.iub.myparcialapp.services

import co.edu.iub.myparcialapp.dto.requests.ReportePeriodoRequest
import co.edu.iub.myparcialapp.dto.responses.EstadisticasGeneralesResponse
import co.edu.iub.myparcialapp.dto.responses.IngresoPorMes
import co.edu.iub.myparcialapp.dto.responses.ReporteIngresosResponse
import co.edu.iub.myparcialapp.dto.responses.ReporteVuelosResponse
import co.edu.iub.myparcialapp.dto.responses.VueloReservadoInfo
import co.edu.iub.myparcialapp.entities.EstadoReserva
import co.edu.iub.myparcialapp.entities.EstadoVuelo
import co.edu.iub.myparcialapp.entities.UserRole
import co.edu.iub.myparcialapp.repositories.AeronaveRepository
import co.edu.iub.myparcialapp.repositories.ReservaRepository
import co.edu.iub.myparcialapp.repositories.UserRepository
import co.edu.iub.myparcialapp.repositories.VueloRepository
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.YearMonth
import java.time.ZoneId

@Service
class ReporteService(
    private val vueloRepository: VueloRepository,
    private val reservaRepository: ReservaRepository,
    private val userRepository: UserRepository,
    private val aeronaveRepository: AeronaveRepository
) {

    fun getVuelosMasReservados(request: ReportePeriodoRequest): ReporteVuelosResponse {
        val fechaInicio = request.fechaInicio.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val fechaFin = request.fechaFin.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()

        val resultados = reservaRepository.findVuelosMasReservados()
        val vuelosInfo = resultados.take(10).map { resultado ->
            val numeroVuelo = resultado[0] as String
            val totalReservas = resultado[1] as Long

            val vuelo = vueloRepository.findByNumeroVuelo(numeroVuelo)!!
            val ingresos = reservaRepository.findByVueloId(vuelo.id)
                .filter { it.estado == EstadoReserva.CONFIRMADA }
                .sumOf { it.total }

            VueloReservadoInfo(
                numeroVuelo = numeroVuelo,
                origen = vuelo.origen,
                destino = vuelo.destino,
                totalReservas = totalReservas,
                ingresoTotal = ingresos
            )
        }

        return ReporteVuelosResponse(
            vuelosMasReservados = vuelosInfo,
            totalVuelos = vueloRepository.count(),
            vuelosConReservas = vuelosInfo.size.toLong(),
            periodo = "${request.fechaInicio} - ${request.fechaFin}"
        )
    }

    fun getReporteIngresos(request: ReportePeriodoRequest): ReporteIngresosResponse {
        val fechaInicio = request.fechaInicio.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val fechaFin = request.fechaFin.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()

        val reservasDelPeriodo = reservaRepository.findByFechaReservaBetween(fechaInicio, fechaFin)
            .filter { it.estado == EstadoReserva.CONFIRMADA }

        val ingresoTotal = reservasDelPeriodo.sumOf { it.total }
        val totalReservas = reservasDelPeriodo.size.toLong()

        val reservasPorMes = reservasDelPeriodo
            .groupBy { YearMonth.from(it.fechaReserva.atZone(ZoneId.systemDefault())) }
            .map { (yearMonth, reservas) ->
                IngresoPorMes(
                    mes = yearMonth.toString(),
                    ingresos = reservas.sumOf { it.total },
                    cantidadReservas = reservas.size.toLong()
                )
            }.sortedBy { it.mes }

        val diasEnPeriodo = Duration.between(fechaInicio, fechaFin).toDays()
        val promedioReservaPorDia = if (diasEnPeriodo > 0) totalReservas.toDouble() / diasEnPeriodo else 0.0

        return ReporteIngresosResponse(
            ingresoTotal = ingresoTotal,
            totalReservas = totalReservas,
            reservasPorMes = reservasPorMes,
            promedioReservaPorDia = promedioReservaPorDia,
            periodo = "${request.fechaInicio} - ${request.fechaFin}"
        )
    }

    fun getEstadisticasGenerales(): EstadisticasGeneralesResponse {
        val totalUsuarios = userRepository.count()
        val totalClientes = userRepository.countByRole(UserRole.CLIENTE)
        val totalEmpleados = userRepository.countByRole(UserRole.EMPLEADO)
        val totalAdministradores = userRepository.countByRole(UserRole.ADMINISTRADOR)

        val totalVuelos = vueloRepository.count()
        val totalReservas = reservaRepository.count()
        val totalAeronaves = aeronaveRepository.count()

        val vuelosProgramados = vueloRepository.findByEstado(EstadoVuelo.PROGRAMADO).size.toLong()
        val vuelosCompletados = vueloRepository.findByEstado(EstadoVuelo.COMPLETADO).size.toLong()

        val reservasConfirmadas = reservaRepository.findByEstado(EstadoReserva.CONFIRMADA).size.toLong()

        val ingresosTotales = reservaRepository.findByEstado(EstadoReserva.CONFIRMADA).sumOf { it.total }

        return EstadisticasGeneralesResponse(
            totalUsuarios = totalUsuarios,
            totalClientes = totalClientes,
            totalEmpleados = totalEmpleados,
            totalAdministradores = totalAdministradores,
            totalVuelos = totalVuelos,
            totalReservas = totalReservas,
            totalAeronaves = totalAeronaves,
            vuelosProgramados = vuelosProgramados,
            vuelosCompletados = vuelosCompletados,
            reservasConfirmadas = reservasConfirmadas,
            ingresosTotales = ingresosTotales
        )
    }
}