package com.example.appbarbearia.models

import kotlinx.serialization.Serializable
@Serializable
    data class Cliente(
        val id: Int? = null,
        val nome: String,
        val telefone: String
    )
