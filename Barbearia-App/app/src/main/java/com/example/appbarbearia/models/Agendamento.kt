package com.example.appbarbearia.models

import kotlinx.serialization.Serializable

@Serializable
data class Agendamento(
    val id: Int? = null,
    val clienteId: Int,
    val barbeiroId: Int,
    val servicoId: Int,
    val dataHora: String,
    val clienteNome: String? = null,
    val barbeiroNome: String? = null,
    val servicoNome: String? = null
)
