package co.edu.iub.myparcialapp.dto.responses

data class EstadisticasGeneralesResponse(
    val totalUsuarios: Long,
    val totalClientes: Long,
    val totalEmpleados: Long,
    val totalAdministradores: Long,
    val totalVuelos: Long,
    val totalReservas: Long,
    val totalAeronaves: Long,
    val vuelosProgramados: Long,
    val vuelosCompletados: Long,
    val reservasConfirmadas: Long,
    val ingresosTotales: java.math.BigDecimal
)