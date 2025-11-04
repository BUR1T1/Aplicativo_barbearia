package com.example.appagendamento

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

import io.ktor.http.contentType
import kotlinx.serialization.json.Json

// ===================== MODELOS =====================

@Serializable
data class Barbeiro(val id: Int? = null, val nome: String, val especialidade: String, val telefone: String)

@Serializable
data class Cliente(
    val id: Int? = null,
    val nome: String,
    val telefone: String
)
@Serializable
data class Servico(
    val id: Int? = null,
    val nome: String,
    val preco: Double
)
@Serializable
data class Agendamento(
    val id: Int? = null,
    val clienteNome: String,
    val servicoNome: String,
    val data: String,
    val hora: String
)
@Serializable
data class AgendamentoRequest(
    val clienteId: Int,
    val barbeiroId: Int,
    val servicoId: Int,
    val dataHora: String
)

@Serializable
data class AgendamentoResponse(
    val id: Int,
    val clienteNome: String,
    val barbeiroNome: String,
    val servicoNome: String,
    val dataHora: String
)

// ===================== CONFIGURAÇÃO KTOR =====================

val client = HttpClient(Android) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}


const val BASE_URL = "http://10.0.2.2:5260/api"

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarberTechApp() {
    val navController = rememberNavController()

    Scaffold(
        topBar = { TopAppBar(title = { Text("💈 BarberTech") }) },
        bottomBar = { BottomMenu(navController) }
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

// ===================== MENU RETRÁTIL =====================

@Composable
fun BottomMenu(navController: NavHostController) {
    NavigationBar {
        val items = listOf(
            "Home" to "home",
            "Barbeiros" to "barbeiros",
            "Clientes" to "clientes",
            "Serviços" to "servicos",
            "Agendamentos" to "agendamentos"
        )
        items.forEach { (label, route) ->
            NavigationBarItem(
                selected = navController.currentDestination?.route == route,
                onClick = { navController.navigate(route) },
                label = { Text(label) },
                icon = {}
            )
        }
    }
}

// ===================== TELA INICIAL =====================

@Composable
fun HomeScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Bem-vindo ao BarberTech 💈", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))
            Button(onClick = { navController.navigate("agendamentos") }) { Text("Fazer Agendamento") }
        }
    }
}

// ===================== COMPONENTE REUTILIZÁVEL =====================

@Composable
fun CenteredForm(content: @Composable ColumnScope.() -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
            }
        }
    }
}

// ===================== COMPONENTE DE LISTA =====================

@Composable
fun RecordList(title: String, items: List<String>, erro: String?) {
    Spacer(Modifier.height(20.dp))
    Text(title, style = MaterialTheme.typography.titleMedium)
    when {
        erro != null -> Text(erro, color = MaterialTheme.colorScheme.error)
        items.isEmpty() -> Text("Nenhum registro encontrado.")
        else -> LazyColumn {
            items(items) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Text(
                        text = item,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

// ===================== BARBEIRO SCREEN =====================

@Composable
fun BarbeiroScreen() {
    val scope = rememberCoroutineScope()
    var nome by remember { mutableStateOf(TextFieldValue("")) }
    var especialidade by remember { mutableStateOf(TextFieldValue("")) }
    var telefone by remember { mutableStateOf(TextFieldValue("")) }
    var barbeiros by remember { mutableStateOf<List<Barbeiro>>(emptyList()) }
    var erro by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val response = client.get("$BASE_URL/barbeiro").bodyAsText()
            barbeiros = Json.decodeFromString(response)
        } catch (e: Exception) {
            erro = e.message
        }
    }

    CenteredForm {
        Text("Cadastrar Barbeiro", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") })
        OutlinedTextField(value = especialidade, onValueChange = { especialidade = it }, label = { Text("Especialidade") })
        OutlinedTextField(value = telefone, onValueChange = { telefone = it }, label = { Text("Telefone") })
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            scope.launch {
                try {
                    client.post("$BASE_URL/barbeiro") {
                        setBody(Barbeiro(nome = nome.text, especialidade = especialidade.text, telefone = telefone.text))
                    }
                    val response = client.get("$BASE_URL/barbeiro").bodyAsText()
                    barbeiros = Json.decodeFromString(response)
                    erro = null
                } catch (e: Exception) {
                    erro = e.message
                }
            }
        }) { Text("Salvar") }

        RecordList(
            "Barbeiros cadastrados",
            barbeiros.map { "💈 ${it.nome} (${it.especialidade}) - ${it.telefone}" },
            erro
        )
    }
}

// ===================== CLIENTE SCREEN =====================

@Composable
fun ClienteScreen() {
    val scope = rememberCoroutineScope()
    var nome by remember { mutableStateOf(TextFieldValue("")) }
    var telefone by remember { mutableStateOf(TextFieldValue("")) }
    var clientes by remember { mutableStateOf<List<Cliente>>(emptyList()) }
    var erro by remember { mutableStateOf<String?>(null) }

    // 🔹 Carrega a lista inicial
    LaunchedEffect(Unit) {
        try {
            clientes = client.get("$BASE_URL/cliente").body()
        } catch (e: Exception) {
            erro = e.message
        }
    }

    CenteredForm {
        Text("Cadastrar Cliente", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") }
        )

        OutlinedTextField(
            value = telefone,
            onValueChange = { telefone = it },
            label = { Text("Telefone") }
        )

        Spacer(Modifier.height(12.dp))

        Button(onClick = {
            scope.launch {
                try {
                    // 🔹 Envia cliente no formato JSON
                    client.post("$BASE_URL/cliente") {
                        contentType(ContentType.Application.Json)
                        setBody(Cliente(nome = nome.text, telefone = telefone.text))
                    }

                    // 🔹 Atualiza lista
                    clientes = client.get("$BASE_URL/cliente").body()
                    erro = null
                } catch (e: Exception) {
                    erro = e.message
                }
            }
        }) {
            Text("Salvar")
        }

        RecordList("Clientes", clientes.map { "👤 ${it.nome} - ${it.telefone}" }, erro)
    }
}

// ===================== SERVIÇO SCREEN =====================

@Composable
fun ServicoScreen() {
    val scope = rememberCoroutineScope()
    var nome by remember { mutableStateOf(TextFieldValue("")) }
    var preco by remember { mutableStateOf(TextFieldValue("")) }
    var servicos by remember { mutableStateOf<List<Servico>>(emptyList()) }
    var erro by remember { mutableStateOf<String?>(null) }

    // 🔹 Carrega lista inicial
    LaunchedEffect(Unit) {
        try {
            servicos = client.get("$BASE_URL/servico").body()
        } catch (e: Exception) {
            erro = e.message
        }
    }

    CenteredForm {
        Text("Cadastrar Serviço", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome do Serviço") }
        )

        OutlinedTextField(
            value = preco,
            onValueChange = { preco = it },
            label = { Text("Preço (R$)") }
        )

        Spacer(Modifier.height(12.dp))

        Button(onClick = {
            scope.launch {
                try {
                    val servico = Servico(nome = nome.text, preco = preco.text.toDoubleOrNull() ?: 0.0)
                    client.post("$BASE_URL/servico") {
                        contentType(ContentType.Application.Json)
                        setBody(servico)
                    }

                    servicos = client.get("$BASE_URL/servico").body()
                    erro = null
                } catch (e: Exception) {
                    erro = e.message
                }
            }
        }) {
            Text("Salvar")
        }

        RecordList("Serviços", servicos.map { "💈 ${it.nome} - R$ ${it.preco}" }, erro)
    }
}

// ===================== AGENDAMENTO SCREEN =====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendamentoScreen() {
    val scope = rememberCoroutineScope()
    var clientes by remember { mutableStateOf<List<Cliente>>(emptyList()) }
    var barbeiros by remember { mutableStateOf<List<Barbeiro>>(emptyList()) }
    var servicos by remember { mutableStateOf<List<Servico>>(emptyList()) }
    var agendamentos by remember { mutableStateOf<List<AgendamentoResponse>>(emptyList()) }
    var erro by remember { mutableStateOf<String?>(null) }

    var selectedCliente by remember { mutableStateOf<Cliente?>(null) }
    var selectedBarbeiro by remember { mutableStateOf<Barbeiro?>(null) }
    var selectedServico by remember { mutableStateOf<Servico?>(null) }

    var data by remember { mutableStateOf(TextFieldValue("")) }
    var hora by remember { mutableStateOf(TextFieldValue("")) }

    var expandedCliente by remember { mutableStateOf(false) }
    var expandedBarbeiro by remember { mutableStateOf(false) }
    var expandedServico by remember { mutableStateOf(false) }

    // 🔹 Carregar dados iniciais
    LaunchedEffect(Unit) {
        try {
            clientes = client.get("$BASE_URL/cliente").body()
            barbeiros = client.get("$BASE_URL/barbeiro").body()
            servicos = client.get("$BASE_URL/servico").body()
            agendamentos = client.get("$BASE_URL/agendamento").body()
        } catch (e: Exception) {
            erro = e.message
        }
    }

    CenteredForm {
        Text("Novo Agendamento", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(10.dp))

        // 🔹 Seleção de Cliente
        ExposedDropdownMenuBox(expanded = expandedCliente, onExpandedChange = { expandedCliente = !expandedCliente }) {
            OutlinedTextField(
                value = selectedCliente?.nome ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Cliente") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCliente) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandedCliente, onDismissRequest = { expandedCliente = false }) {
                clientes.forEach {
                    DropdownMenuItem(
                        text = { Text(it.nome) },
                        onClick = {
                            selectedCliente = it
                            expandedCliente = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // 🔹 Seleção de Barbeiro
        ExposedDropdownMenuBox(expanded = expandedBarbeiro, onExpandedChange = { expandedBarbeiro = !expandedBarbeiro }) {
            OutlinedTextField(
                value = selectedBarbeiro?.nome ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Barbeiro") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBarbeiro) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandedBarbeiro, onDismissRequest = { expandedBarbeiro = false }) {
                barbeiros.forEach {
                    DropdownMenuItem(
                        text = { Text("${it.nome} - ${it.especialidade}") },
                        onClick = {
                            selectedBarbeiro = it
                            expandedBarbeiro = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // 🔹 Seleção de Serviço
        ExposedDropdownMenuBox(expanded = expandedServico, onExpandedChange = { expandedServico = !expandedServico }) {
            OutlinedTextField(
                value = selectedServico?.nome ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Serviço") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedServico) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandedServico, onDismissRequest = { expandedServico = false }) {
                servicos.forEach {
                    DropdownMenuItem(
                        text = { Text("${it.nome} - R$${it.preco}") },
                        onClick = {
                            selectedServico = it
                            expandedServico = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // 🔹 Data formatada (YYYY-MM-DD)
        OutlinedTextField(
            value = data,
            onValueChange = {
                val filtered = it.text.filter { c -> c.isDigit() || c == '-' }
                data = TextFieldValue(filtered.take(10))
            },
            label = { Text("Data (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        // 🔹 Hora formatada (HH:MM)
        OutlinedTextField(
            value = hora,
            onValueChange = {
                val filtered = it.text.filter { c -> c.isDigit() || c == ':' }
                hora = TextFieldValue(filtered.take(5))
            },
            label = { Text("Hora (HH:MM)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        if (selectedCliente == null || selectedBarbeiro == null || selectedServico == null) {
                            erro = "Selecione cliente, barbeiro e serviço."
                            return@launch
                        }

                        val request = AgendamentoRequest(
                            clienteId = selectedCliente?.id ?: 0,
                            barbeiroId = selectedBarbeiro!!.id!!,
                            servicoId = selectedServico!!.id!!,
                            dataHora = "${data.text}T${hora.text}"
                        )

                        client.post("$BASE_URL/agendamento") {
                            contentType(ContentType.Application.Json)
                            setBody(request)
                        }

                        agendamentos = client.get("$BASE_URL/agendamento").body()
                        erro = null
                    } catch (e: Exception) {
                        erro = e.message
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar Agendamento")
        }

        Spacer(Modifier.height(16.dp))

        RecordList(
            "Agendamentos",
            agendamentos.map {
                "📅 ${it.clienteNome} com ${it.barbeiroNome} (${it.servicoNome}) em ${it.dataHora.replace('T', ' ')}"
            },
            erro
        )
    }
}