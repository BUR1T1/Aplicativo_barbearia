package com.example.appbarbearia.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.appbarbearia.components.CenteredForm
import com.example.appbarbearia.models.Cliente
import com.example.appbarbearia.network.BASE_URL
import com.example.appbarbearia.network.client
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.launch

// Função auxiliar permanece fora do Composable, mas ajustada para clareza
suspend fun atualizarListaClientes(onResult: (List<Cliente>) -> Unit) {
    try {
        val lista = client.get("$BASE_URL/cliente").body<List<Cliente>>()
        onResult(lista)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@Composable
fun ClienteScreen() {
    val scope = rememberCoroutineScope()
    var nome by remember { mutableStateOf(TextFieldValue("")) }
    var telefone by remember { mutableStateOf(TextFieldValue("")) }
    var clientes by remember { mutableStateOf<List<Cliente>>(emptyList()) }
    var erro by remember { mutableStateOf<String?>(null) }
    var clienteEditando by remember { mutableStateOf<Cliente?>(null) }

    LaunchedEffect(Unit) {
        atualizarListaClientes { clientes = it }
    }

    // 🧱 Usando Surface para o fundo e padding externo
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 📝 Card para o Formulário de Cadastro/Edição
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
                        if (clienteEditando == null) "Cadastrar Novo Cliente" else "Editar Cliente",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary // Laranja
                    )
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = nome,
                        onValueChange = { nome = it },
                        label = { Text("Nome do Cliente") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = telefone,
                        onValueChange = { telefone = it },
                        label = { Text("Telefone") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // 💾 Botão Salvar / Atualizar
                        Button(
                            onClick = {
                                scope.launch {
                                    try {
                                        val cliente = Cliente(
                                            id = clienteEditando?.id, // Nullable para novo, ID para update
                                            nome = nome.text,
                                            telefone = telefone.text
                                        )

                                        if (clienteEditando == null) {
                                            client.post("$BASE_URL/cliente") {
                                                contentType(ContentType.Application.Json)
                                                setBody(cliente)
                                            }
                                        } else {
                                            client.put("$BASE_URL/cliente/${clienteEditando!!.id}") {
                                                contentType(ContentType.Application.Json)
                                                setBody(cliente)
                                            }
                                            clienteEditando = null
                                        }

                                        atualizarListaClientes { clientes = it }
                                        nome = TextFieldValue("")
                                        telefone = TextFieldValue("")
                                        erro = null
                                    } catch (e: Exception) {
                                        erro = e.message
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(if (clienteEditando == null) "Salvar Cliente" else "Atualizar")
                        }

                        if (clienteEditando != null) {
                            Spacer(Modifier.width(8.dp))
                            // ❌ Botão Cancelar (discreto)
                            OutlinedButton(onClick = {
                                clienteEditando = null
                                nome = TextFieldValue("")
                                telefone = TextFieldValue("")
                            }) {
                                Text("Cancelar")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // 📋 Título da Lista
            Text(
                "Clientes Cadastrados",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Start),
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(12.dp))

            // 🛑 Exibição de Erro
            if (erro != null) {
                Text(
                    "Erro: $erro",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
            }

            // 📜 Lista de Clientes
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Espaçamento entre itens
            ) {
                items(clientes) { cliente ->
                    ClienteListItem(
                        cliente = cliente,
                        onEdit = {
                            nome = TextFieldValue(cliente.nome)
                            telefone = TextFieldValue(cliente.telefone)
                            clienteEditando = cliente
                        },
                        onDelete = {
                            scope.launch {
                                try {
                                    client.delete("$BASE_URL/cliente/${cliente.id}")
                                    atualizarListaClientes { clientes = it }
                                } catch (e: Exception) {
                                    erro = e.message
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

// 👤 Novo Componente para o Item da Lista (Mais moderno)
@Composable
fun ClienteListItem(
    cliente: Cliente,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) // Cinza claro para contraste
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = cliente.nome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = cliente.telefone,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // Ações (Botões de Ícone para visual limpo)
            Row {
                // ✏️ Editar
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // 🗑️ Excluir
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Excluir",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}