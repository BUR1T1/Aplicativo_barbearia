package com.example.appbarbearia.ui.theme.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.appbarbearia.models.Servico
import com.example.appbarbearia.network.BASE_URL
import com.example.appbarbearia.network.client
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star

@Composable
fun ServicoScreen() {
    val scope = rememberCoroutineScope()
    var nome by remember { mutableStateOf(TextFieldValue("")) }
    var preco by remember { mutableStateOf(TextFieldValue("")) }
    var servicos by remember { mutableStateOf<List<Servico>>(emptyList()) }
    var erro by remember { mutableStateOf<String?>(null) }
    var editando by remember { mutableStateOf<Servico?>(null) }

    LaunchedEffect(Unit) {
        try {
            servicos = client.get("$BASE_URL/servico").body()
        } catch (e: Exception) {
            erro = e.message
        }
    }

    // Usando Surface para o fundo e cor de tema
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //  Formulário em um Card estilizado
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (editando == null) "Cadastrar Novo Serviço" else "Editar Serviço",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = nome,
                        onValueChange = { nome = it },
                        label = { Text("Nome do Serviço") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = preco,
                        onValueChange = { preco = it },
                        label = { Text("Preço (R$)") },
                        modifier = Modifier.fillMaxWidth(),
                        // Garante que só números possam ser inseridos (se desejado)
                        // keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(Modifier.height(16.dp))

                    //  Botão Salvar / Atualizar com cor de destaque
                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    val precoConvertido = preco.text.replace(",", ".").toDoubleOrNull() ?: 0.0

                                    if (editando == null) {
                                        // CREATE
                                        val servico = Servico(nome = nome.text, preco = precoConvertido)
                                        client.post("$BASE_URL/servico") {
                                            contentType(ContentType.Application.Json)
                                            setBody(servico)
                                        }
                                    } else {
                                        // UPDATE
                                        val servicoAtualizado = Servico(
                                            id = editando!!.id,
                                            nome = nome.text,
                                            preco = precoConvertido
                                        )
                                        client.put("$BASE_URL/servico/${editando!!.id}") {
                                            contentType(ContentType.Application.Json)
                                            setBody(servicoAtualizado)
                                        }
                                        editando = null
                                    }

                                    // Recarrega lista e limpa
                                    servicos = client.get("$BASE_URL/servico").body()
                                    nome = TextFieldValue("")
                                    preco = TextFieldValue("")
                                    erro = null
                                } catch (e: Exception) {
                                    erro = e.message
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(if (editando == null) "Salvar Novo Serviço" else "Atualizar Serviço")
                    }

                    if (editando != null) {
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                editando = null
                                nome = TextFieldValue("")
                                preco = TextFieldValue("")
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Cancelar Edição")
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            //  Título da Lista
            Text(
                "Serviços Cadastrados",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(Modifier.height(12.dp))

            //  Lista de Serviços (usando LazyColumn para eficiência)
            LazyColumn {
                items(servicos) { s ->

                    // Item da lista em Card para visual moderno
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // 👇 REFERÊNCIA RESOLVIDA: Usando Star (ícone padrão)
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Ícone Serviço",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(8.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = s.nome,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "R$ ${"%.2f".format(s.preco)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            // ✏️ Botão Editar
                            OutlinedButton(
                                onClick = {
                                    editando = s
                                    nome = TextFieldValue(s.nome)
                                    preco = TextFieldValue(s.preco.toString())
                                },
                                modifier = Modifier.height(36.dp).padding(horizontal = 4.dp)
                            ) {
                                Text("Editar")
                            }

                            // 🗑️ Botão Excluir
                            Button(
                                onClick = {
                                    scope.launch {
                                        try {
                                            client.delete("$BASE_URL/servico/${s.id}")
                                            servicos = client.get("$BASE_URL/servico").body()
                                        } catch (e: Exception) {
                                            erro = e.message
                                        }
                                    }
                                },
                                modifier = Modifier.height(36.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Excluir")
                            }
                        }
                    }
                }
            }

            if (erro != null) {
                Spacer(Modifier.height(16.dp))
                Text("Erro: $erro", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Start))
            }
        }
    }
}