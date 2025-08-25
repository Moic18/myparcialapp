package co.edu.iub.myparcialapp.dto.responses

data class ReporteVuelosResponse(
    val vuelosMasReservados: List<VueloReservadoInfo>,
    val totalVuelos: Long,
    val vuelosConReservas: Long,
    val periodo: String
)

data class VueloReservadoInfo(
    val numeroVuelo: String,
    val origen: String,
    val destino: String,
    val totalReservas: Long,
    val ingresoTotal: java.math.BigDecimal
)