package co.edu.iub.myparcialapp.dto.responses

import co.edu.iub.myparcialapp.dto.requests.BuscarVuelosRequest

data class BuscarVuelosResponse(
    val vuelos: List<VueloResponse>,
    val totalEncontrados: Int,
    val criteriosBusqueda: BuscarVuelosRequest
)