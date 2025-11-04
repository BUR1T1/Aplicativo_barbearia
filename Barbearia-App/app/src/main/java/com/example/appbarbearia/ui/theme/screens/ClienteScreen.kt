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
import com.example.appbarbearia.models.Cliente
import com.example.appbarbearia.network.BASE_URL
import com.example.appbarbearia.network.client
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.launch
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
