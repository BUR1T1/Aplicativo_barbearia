package com.example.appagendamento

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class Barbeiro(val id: Int = 0, val nome: String, val especialidade: String, val telefone: String)

@Serializable
data class Cliente(val id: Int = 0, val nome: String, val telefone: String)

@Serializable
data class Servico(val id: Int = 0, val nome: String, val preco: Double, val duracaoMin: Int)

@Serializable
data class Agendamento(
    val id: Int = 0,
    val clienteId: Int,
    val barbeiroId: Int,
    val servicoId: Int,
    val dataHora: String
)

val client = HttpClient(Android) {
    install(ContentNegotiation) {
        json()
    }
}

const val BASE_URL = "http://10.0.2.2:5000/api" // Para emulador Android acessar localhost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BarberTechApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarberTechApp() {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("💈 BarberTech") })
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("barbeiros") { BarbeiroScreen() }
            composable("clientes") { ClienteScreen() }
            composable("servicos") { ServicoScreen() }
            composable("agendamentos") { AgendamentoScreen() }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        listOf(
            "Home" to "home",
            "Barbeiros" to "barbeiros",
            "Clientes" to "clientes",
            "Serviços" to "servicos",
            "Agendamentos" to "agendamentos"
        ).forEach { (label, route) ->
            NavigationBarItem(
                label = { Text(label) },
                selected = navController.currentBackStackEntryAsState().value?.destination?.route == route,
                onClick = { navController.navigate(route) },
                icon = {}
            )
        }
    }
}

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bem-vindo ao BarberTech 💈", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(20.dp))
        Button(onClick = { navController.navigate("agendamentos") }) {
            Text("Fazer Agendamento")
        }
    }
}

@Composable
fun BarbeiroScreen() {
    val scope = rememberCoroutineScope()
    var nome by remember { mutableStateOf(TextFieldValue("")) }
    var especialidade by remember { mutableStateOf(TextFieldValue("")) }
    var telefone by remember { mutableStateOf(TextFieldValue("")) }
    var barbeiros by remember { mutableStateOf(listOf<Barbeiro>()) }

    LaunchedEffect(Unit) {
        barbeiros = client.get("$BASE_URL/barbeiro").body()
    }

    Column(Modifier.padding(16.dp)) {
        Text("Cadastrar Barbeiro", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") })
        OutlinedTextField(value = especialidade, onValueChange = { especialidade = it }, label = { Text("Especialidade") })
        OutlinedTextField(value = telefone, onValueChange = { telefone = it }, label = { Text("Telefone") })
        Button(onClick = {
            scope.launch {
                client.post("$BASE_URL/barbeiro") {
                    setBody(Barbeiro(nome = nome.text, especialidade = especialidade.text, telefone = telefone.text))
                }
                barbeiros = client.get("$BASE_URL/barbeiro").body()
            }
        }) { Text("Salvar") }

        Spacer(Modifier.height(20.dp))
        Text("Barbeiros Cadastrados", style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(barbeiros.size) { i ->
                Text("• ${barbeiros[i].nome} (${barbeiros[i].especialidade}) - ${barbeiros[i].telefone}")
            }
        }
    }
}

@Composable
fun ClienteScreen() {
    val scope = rememberCoroutineScope()
    var nome by remember { mutableStateOf(TextFieldValue("")) }
    var telefone by remember { mutableStateOf(TextFieldValue("")) }
    var clientes by remember { mutableStateOf(listOf<Cliente>()) }

    LaunchedEffect(Unit) {
        clientes = client.get("$BASE_URL/cliente").body()
    }

    Column(Modifier.padding(16.dp)) {
        Text("Cadastrar Cliente", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") })
        OutlinedTextField(value = telefone, onValueChange = { telefone = it }, label = { Text("Telefone") })
        Button(onClick = {
            scope.launch {
                client.post("$BASE_URL/cliente") {
                    setBody(Cliente(nome = nome.text, telefone = telefone.text))
                }
                clientes = client.get("$BASE_URL/cliente").body()
            }
        }) { Text("Salvar") }

        Spacer(Modifier.height(20.dp))
        Text("Clientes", style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(clientes.size) { i ->
                Text("• ${clientes[i].nome} - ${clientes[i].telefone}")
            }
        }
    }
}

@Composable
fun ServicoScreen() {
    val scope = rememberCoroutineScope()
    var nome by remember { mutableStateOf(TextFieldValue("")) }
    var preco by remember { mutableStateOf(TextFieldValue("")) }
    var duracao by remember { mutableStateOf(TextFieldValue("")) }
    var servicos by remember { mutableStateOf(listOf<Servico>()) }

    LaunchedEffect(Unit) {
        servicos = client.get("$BASE_URL/servico").body()
    }

    Column(Modifier.padding(16.dp)) {
        Text("Cadastrar Serviço", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") })
        OutlinedTextField(value = preco, onValueChange = { preco = it }, label = { Text("Preço") })
        OutlinedTextField(value = duracao, onValueChange = { duracao = it }, label = { Text("Duração (min)") })
        Button(onClick = {
            scope.launch {
                client.post("$BASE_URL/servico") {
                    setBody(Servico(nome = nome.text, preco = preco.text.toDouble(), duracaoMin = duracao.text.toInt()))
                }
                servicos = client.get("$BASE_URL/servico").body()
            }
        }) { Text("Salvar") }

        Spacer(Modifier.height(20.dp))
        Text("Serviços", style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(servicos.size) { i ->
                Text("• ${servicos[i].nome} - R$${servicos[i].preco}")
            }
        }
    }
}

@Composable
fun AgendamentoScreen() {
    val scope = rememberCoroutineScope()
    var clienteId by remember { mutableStateOf(TextFieldValue("")) }
    var barbeiroId by remember { mutableStateOf(TextFieldValue("")) }
    var servicoId by remember { mutableStateOf(TextFieldValue("")) }
    var dataHora by remember { mutableStateOf(TextFieldValue("")) }

    Column(Modifier.padding(16.dp)) {
        Text("Novo Agendamento", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(value = clienteId, onValueChange = { clienteId = it }, label = { Text("ID Cliente") })
        OutlinedTextField(value = barbeiroId, onValueChange = { barbeiroId = it }, label = { Text("ID Barbeiro") })
        OutlinedTextField(value = servicoId, onValueChange = { servicoId = it }, label = { Text("ID Serviço") })
        OutlinedTextField(value = dataHora, onValueChange = { dataHora = it }, label = { Text("Data e Hora (2025-10-20T15:00)") })

        Button(onClick = {
            scope.launch {
                client.post("$BASE_URL/agendamento") {
                    setBody(
                        Agendamento(
                            clienteId = clienteId.text.toInt(),
                            barbeiroId = barbeiroId.text.toInt(),
                            servicoId = servicoId.text.toInt(),
                            dataHora = dataHora.text
                        )
                    )
                }
            }
        }) { Text("Agendar") }
    }
}
