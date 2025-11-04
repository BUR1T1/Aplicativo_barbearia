package com.example.appbarbearia.models

import kotlinx.serialization.Serializable

@Serializable
data class AgendamentoRequest(
    val clienteId: Int,
    val barbeiroId: Int,
    val servicoId: Int,
    val dataHora: String
)