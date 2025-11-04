package com.example.appagendamento

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.material3.*
import com.example.appagendamento.ui.BarberTechApp

// ===================== MODELOS =====================

// ===================== CONFIGURAÇÃO KTOR =====================

// ===================== MAIN ACTIVITY =====================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                BarberTechApp()
            }
        }
    }
}

// ===================== APP PRINCIPAL =====================

// ===================== MENU RETRÁTIL =====================

// ===================== TELA INICIAL =====================

// ===================== COMPONENTE REUTILIZÁVEL =====================

// ===================== COMPONENTE DE LISTA =====================

// ===================== BARBEIRO SCREEN =====================

// ===================== CLIENTE SCREEN =====================

// ===================== SERVIÇO SCREEN =====================

// ===================== AGENDAMENTO SCREEN =====================
