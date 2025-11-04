package com.example.appbarbearia.ui.theme.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
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
import com.example.appbarbearia.models.Barbeiro
import com.example.appbarbearia.network.BASE_URL
import com.example.appbarbearia.network.client
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

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
