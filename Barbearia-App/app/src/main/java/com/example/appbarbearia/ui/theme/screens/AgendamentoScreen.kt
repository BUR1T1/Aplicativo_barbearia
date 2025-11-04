package com.example.appbarbearia.ui.theme.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.appbarbearia.components.CenteredForm
import com.example.appbarbearia.components.RecordList

import com.example.appbarbearia.models.AgendamentoRequest
import com.example.appbarbearia.models.AgendamentoResponse
import com.example.appbarbearia.models.Barbeiro
import com.example.appbarbearia.models.Cliente
import com.example.appbarbearia.models.Servico
import com.example.appbarbearia.network.BASE_URL
import com.example.appbarbearia.network.client
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.launch

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
        ExposedDropdownMenuBox(
            expanded = expandedCliente,
            onExpandedChange = { expandedCliente = !expandedCliente }) {
            OutlinedTextField(
                value = selectedCliente?.nome ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Cliente") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCliente) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandedCliente,
                onDismissRequest = { expandedCliente = false }) {
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
        ExposedDropdownMenuBox(
            expanded = expandedBarbeiro,
            onExpandedChange = { expandedBarbeiro = !expandedBarbeiro }) {
            OutlinedTextField(
                value = selectedBarbeiro?.nome ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Barbeiro") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBarbeiro) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandedBarbeiro,
                onDismissRequest = { expandedBarbeiro = false }) {
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
        ExposedDropdownMenuBox(
            expanded = expandedServico,
            onExpandedChange = { expandedServico = !expandedServico }) {
            OutlinedTextField(
                value = selectedServico?.nome ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Serviço") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedServico) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandedServico,
                onDismissRequest = { expandedServico = false }) {
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
                "📅 ${it.clienteNome} com ${it.barbeiroNome} (${it.servicoNome}) em ${
                    it.dataHora.replace(
                        'T',
                        ' '
                    )
                }"
            },
            erro
        )
    }
}