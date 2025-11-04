package com.example.appbarbearia.models

import kotlinx.serialization.Serializable
    @Serializable
    data class Barbeiro(
        val id: Int? = null,
        val nome: String,
        val especialidade: String,
        val telefone: String)
