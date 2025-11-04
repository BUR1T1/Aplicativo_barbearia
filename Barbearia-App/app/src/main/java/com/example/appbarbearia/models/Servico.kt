package com.example.appbarbearia.models

import kotlinx.serialization.Serializable

@Serializable
data class Servico(
    val id: Int? = null,
    val nome: String,
    val preco: Double
)