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
