package com.example.appbarbearia.models

import kotlinx.serialization.Serializable

@Serializable
data class Agendamento(
    val id: Int? = null,
    val clienteNome: String,
    val servicoNome: String,
    val data: String,
    val hora: String
)