package com.example.appbarbearia.ui.theme.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.appbarbearia.models.*
import com.example.appbarbearia.network.BASE_URL
import com.example.appbarbearia.network.client
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendamentoScreen() {
    val scope = rememberCoroutineScope()

    var clientes by remember { mutableStateOf<List<Cliente>>(emptyList()) }
    var barbeiros by remember { mutableStateOf<List<Barbeiro>>(emptyList()) }
    var servicos by remember { mutableStateOf<List<Servico>>(emptyList()) }
    var agendamentos by remember { mutableStateOf<List<AgendamentoResponse>>(emptyList()) }

    var erro by remember { mutableStateOf<String?>(null) }
    var agendamentoEditando by remember { mutableStateOf<AgendamentoResponse?>(null) }

    var selectedCliente by remember { mutableStateOf<Cliente?>(null) }
    var selectedBarbeiro by remember { mutableStateOf<Barbeiro?>(null) }
    var selectedServico by remember { mutableStateOf<Servico?>(null) }

    val dataAtual = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
    val horaAtual = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

    var data by remember { mutableStateOf(TextFieldValue(dataAtual)) }
    var hora by remember { mutableStateOf(TextFieldValue(horaAtual)) }

    suspend fun fetchAllData() {
        try {
            clientes = client.get("$BASE_URL/cliente").body()
            barbeiros = client.get("$BASE_URL/barbeiro").body()
            servicos = client.get("$BASE_URL/servico").body()
            agendamentos = client.get("$BASE_URL/agendamento").body()
            erro = null
        } catch (e: Exception) {
            erro = "Erro ao carregar dados: ${e.message}"
        }
    }

    LaunchedEffect(Unit) { fetchAllData() }

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            erro?.let {
                Text(text = it, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp))
            }

            // 🔹 Formulário
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        if (agendamentoEditando == null) "Novo Agendamento" else "Editar Agendamento",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(16.dp))

                    // Cliente
                    DropdownField(
                        label = "Cliente",
                        selectedItem = selectedCliente?.nome ?: "",
                        items = clientes.map { it.nome },
                        onItemSelected = { nome -> selectedCliente = clientes.find { it.nome == nome } }
                    )

                    Spacer(Modifier.height(8.dp))

                    // Barbeiro
                    DropdownField(
                        label = "Barbeiro",
                        selectedItem = selectedBarbeiro?.nome ?: "",
                        items = barbeiros.map { it.nome },
                        onItemSelected = { nome -> selectedBarbeiro = barbeiros.find { it.nome == nome } }
                    )

                    Spacer(Modifier.height(8.dp))

                    // Serviço
                    DropdownField(
                        label = "Serviço",
                        selectedItem = selectedServico?.nome ?: "",
                        items = servicos.map { "${it.nome} - R$${"%.2f".format(it.preco)}" },
                        onItemSelected = { nome ->
                            val nomeLimpo = nome.substringBefore(" - ")
                            selectedServico = servicos.find { it.nome == nomeLimpo }
                        }
                    )

                    Spacer(Modifier.height(8.dp))

                    // Data e Hora
                    CampoDataHora(
                        data = data,
                        hora = hora,
                        onDataChange = { data = it },
                        onHoraChange = { hora = it }
                    )

                    Spacer(Modifier.height(16.dp))

                    // Botões
                    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                        Button(onClick = {
                            scope.launch {
                                try {
                                    val request = AgendamentoRequest(
                                        clienteId = selectedCliente?.id ?: 0,
                                        barbeiroId = selectedBarbeiro?.id ?: 0,
                                        servicoId = selectedServico?.id ?: 0,
                                        dataHora = "${data.text}T${hora.text}"
                                    )

                                    if (agendamentoEditando == null) {
                                        client.post("$BASE_URL/agendamento") {
                                            contentType(ContentType.Application.Json)
                                            setBody(request)
                                        }
                                    } else {
                                        client.put("$BASE_URL/agendamento/${agendamentoEditando!!.id}") {
                                            contentType(ContentType.Application.Json)
                                            setBody(request)
                                        }
                                    }

                                    fetchAllData()
                                    agendamentoEditando = null
                                    selectedCliente = null
                                    selectedBarbeiro = null
                                    selectedServico = null
                                    data = TextFieldValue(dataAtual)
                                    hora = TextFieldValue(horaAtual)
                                } catch (e: Exception) {
                                    erro = "Erro ao salvar: ${e.message}"
                                }
                            }
                        }) {
                            Text(if (agendamentoEditando == null) "Salvar" else "Atualizar")
                        }

                        if (agendamentoEditando != null) {
                            Spacer(Modifier.width(8.dp))
                            OutlinedButton(onClick = {
                                agendamentoEditando = null
                                selectedCliente = null
                                selectedBarbeiro = null
                                selectedServico = null
                                data = TextFieldValue(dataAtual)
                                hora = TextFieldValue(horaAtual)
                            }) {
                                Text("Cancelar")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(agendamentos) { ag ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                val clienteNome = clientes.find { it.id == ag.clienteId }?.nome ?: "Cliente desconhecido"
                                val barbeiroNome = barbeiros.find { it.id == ag.barbeiroId }?.nome ?: "Barbeiro desconhecido"
                                val servicoNome = servicos.find { it.id == ag.servicoId }?.nome ?: "Serviço desconhecido"
                                Text("👤 $clienteNome")
                                Text("💈 $barbeiroNome")
                                Text("✂️ $servicoNome")
                                Text("📅 ${ag.dataHora.replace('T', ' ')}")
                            }
                            Row {
                                IconButton(onClick = {
                                    agendamentoEditando = ag
                                    selectedCliente = clientes.find { it.id == ag.clienteId }
                                    selectedBarbeiro = barbeiros.find { it.id == ag.barbeiroId }
                                    selectedServico = servicos.find { it.id == ag.servicoId }
                                    val partes = ag.dataHora.split("T")
                                    data = TextFieldValue(partes.getOrNull(0) ?: dataAtual)
                                    hora = TextFieldValue(partes.getOrNull(1)?.take(5) ?: horaAtual)
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        try {
                                            client.delete("$BASE_URL/agendamento/${ag.id}")
                                            fetchAllData()
                                        } catch (e: Exception) {
                                            erro = "Erro ao excluir: ${e.message}"
                                        }
                                    }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Excluir")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** 🔹 Campo genérico de dropdown */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    selectedItem: String,
    items: List<String>,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedItem,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded, { expanded = false }) {
            items.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onItemSelected(it)
                        expanded = false
                    }
                )
            }
        }
    }
}

/** 🔹 Campo de data e hora */
@Composable
fun CampoDataHora(
    data: TextFieldValue,
    hora: TextFieldValue,
    onDataChange: (TextFieldValue) -> Unit,
    onHoraChange: (TextFieldValue) -> Unit
) {
    val context = LocalContext.current
    val calendario = Calendar.getInstance()

    val datePicker = DatePickerDialog(
        context,
        { _, ano, mes, dia ->
            val dataSelecionada = "%04d-%02d-%02d".format(ano, mes + 1, dia)
            onDataChange(TextFieldValue(dataSelecionada))
        },
        calendario.get(Calendar.YEAR),
        calendario.get(Calendar.MONTH),
        calendario.get(Calendar.DAY_OF_MONTH)
    )

    val timePicker = TimePickerDialog(
        context,
        { _, horaSelecionada, minuto ->
            val horaFormatada = "%02d:%02d".format(horaSelecionada, minuto)
            onHoraChange(TextFieldValue(horaFormatada))
        },
        calendario.get(Calendar.HOUR_OF_DAY),
        calendario.get(Calendar.MINUTE),
        true
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = data,
            onValueChange = {},
            label = { Text("Data") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth().clickable { datePicker.show() },
            trailingIcon = {
                IconButton(onClick = { datePicker.show() }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Selecionar Data")
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = hora,
            onValueChange = {},
            label = { Text("Hora") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth().clickable { timePicker.show() },
            trailingIcon = {
                IconButton(onClick = { timePicker.show() }) {
                    Icon(Icons.Default.AccessTime, contentDescription = "Selecionar Hora")
                }
            }
        )
    }
}
