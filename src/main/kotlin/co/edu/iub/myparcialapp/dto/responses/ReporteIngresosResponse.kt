package co.edu.iub.myparcialapp.dto.responses

data class ReporteIngresosResponse(
    val ingresoTotal: java.math.BigDecimal,
    val totalReservas: Long,
    val reservasPorMes: List<IngresoPorMes>,
    val promedioReservaPorDia: Double,
    val periodo: String
)

data class IngresoPorMes(
    val mes: String,
    val ingresos: java.math.BigDecimal,
    val cantidadReservas: Long
)