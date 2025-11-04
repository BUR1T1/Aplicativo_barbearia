package com.example.appbarbearia.models

import kotlinx.serialization.Serializable

@Serializable
data class AgendamentoResponse(
    val id: Int,
    val clienteNome: String,
    val barbeiroNome: String,
    val servicoNome: String,
    val dataHora: String
)