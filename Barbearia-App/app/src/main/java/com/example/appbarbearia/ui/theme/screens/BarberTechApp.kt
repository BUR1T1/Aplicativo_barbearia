package com.example.appagendamento.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*

import com.example.appbarbearia.ui.theme.screens.AgendamentoScreen
import com.example.appbarbearia.ui.theme.screens.BarbeiroScreen
import com.example.appbarbearia.ui.theme.screens.ClienteScreen
import com.example.appbarbearia.ui.theme.screens.ServicoScreen
import com.seuapp.ui.screens.HomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarberTechApp() {
    val navController = rememberNavController()

    Scaffold(
        topBar = { TopAppBar(title = { Text("💈 BarberTech") }) },

    ) { inner ->
        NavHost(
            navController,
            startDestination = "home",
            Modifier.padding(inner)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("barbeiros") { BarbeiroScreen() }
            composable("clientes") { ClienteScreen() }
            composable("servicos") { ServicoScreen() }
            composable("agendamentos") { AgendamentoScreen() }
        }
    }
}
