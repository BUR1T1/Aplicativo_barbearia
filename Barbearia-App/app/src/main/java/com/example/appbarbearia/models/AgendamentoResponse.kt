package com.example.appbarbearia.models

import kotlinx.serialization.Serializable


@Serializable
data class AgendamentoResponse(
    val id: Int,
    val clienteId: Int,
    val barbeiroId: Int,
    val servicoId: Int,
    val dataHora: String
)
