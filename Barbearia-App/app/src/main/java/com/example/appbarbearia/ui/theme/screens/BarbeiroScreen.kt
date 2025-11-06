package com.example.appbarbearia.ui.theme.screens

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
import com.example.appbarbearia.models.Barbeiro
import com.example.appbarbearia.network.BASE_URL
import com.example.appbarbearia.network.client
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.launch

// 🛠️ Função auxiliar para carregar e atualizar a lista
suspend fun atualizarListaBarbeiros(onResult: (List<Barbeiro>) -> Unit, onError: (String) -> Unit) {
    try {
        val lista = client.get("$BASE_URL/barbeiro").body<List<Barbeiro>>()
        onResult(lista)
    } catch (e: Exception) {
        onError("Erro ao carregar barbeiros: ${e.message}")
    }
}

@Composable
fun BarbeiroScreen() {
    val scope = rememberCoroutineScope()
    var nome by remember { mutableStateOf(TextFieldValue("")) }
    var especialidade by remember { mutableStateOf(TextFieldValue("")) }
    var telefone by remember { mutableStateOf(TextFieldValue("")) }
    var barbeiros by remember { mutableStateOf<List<Barbeiro>>(emptyList()) }
    var erro by remember { mutableStateOf<String?>(null) }
    var barbeiroEditando by remember { mutableStateOf<Barbeiro?>(null) } // Novo estado para edição

    // 🔹 Carregar lista inicial
    LaunchedEffect(Unit) {
        atualizarListaBarbeiros(
            onResult = { barbeiros = it },
            onError = { erro = it }
        )
    }

    // 🧱 Estrutura Principal
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
                        if (barbeiroEditando == null) "Cadastrar Novo Barbeiro" else "Editar Barbeiro",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary // Laranja
                    )
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = especialidade, onValueChange = { especialidade = it }, label = { Text("Especialidade") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = telefone, onValueChange = { telefone = it }, label = { Text("Telefone") }, modifier = Modifier.fillMaxWidth())

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // 💾 Botão Salvar / Atualizar
                        Button(onClick = {
                            scope.launch {
                                try {
                                    val barbeiro = Barbeiro(
                                        id = barbeiroEditando?.id,
                                        nome = nome.text,
                                        especialidade = especialidade.text,
                                        telefone = telefone.text
                                    )

                                    if (barbeiroEditando == null) {
                                        // CREATE
                                        client.post("$BASE_URL/barbeiro") {
                                            contentType(ContentType.Application.Json)
                                            setBody(barbeiro)
                                        }
                                    } else {
                                        // UPDATE
                                        client.put("$BASE_URL/barbeiro/${barbeiroEditando!!.id}") {
                                            contentType(ContentType.Application.Json)
                                            setBody(barbeiro)
                                        }
                                        barbeiroEditando = null
                                    }

                                    atualizarListaBarbeiros(
                                        onResult = { barbeiros = it },
                                        onError = { erro = it }
                                    )

                                    nome = TextFieldValue("")
                                    especialidade = TextFieldValue("")
                                    telefone = TextFieldValue("")
                                    erro = null
                                } catch (e: Exception) {
                                    erro = e.message
                                }
                            }
                        }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                            Text(if (barbeiroEditando == null) "Salvar Barbeiro" else "Atualizar")
                        }

                        if (barbeiroEditando != null) {
                            Spacer(Modifier.width(8.dp))
                            // ❌ Botão Cancelar
                            OutlinedButton(onClick = {
                                barbeiroEditando = null
                                nome = TextFieldValue("")
                                especialidade = TextFieldValue("")
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
                "Barbeiros Cadastrados",
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

            // 📜 Lista de Barbeiros (Substituindo RecordList)
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(barbeiros) { barbeiro ->
                    BarbeiroListItem(
                        barbeiro = barbeiro,
                        onEdit = {
                            barbeiroEditando = barbeiro
                            nome = TextFieldValue(barbeiro.nome)
                            especialidade = TextFieldValue(barbeiro.especialidade)
                            telefone = TextFieldValue(barbeiro.telefone)
                        },
                        onDelete = {
                            scope.launch {
                                try {
                                    client.delete("$BASE_URL/barbeiro/${barbeiro.id}")
                                    atualizarListaBarbeiros(
                                        onResult = { barbeiros = it },
                                        onError = { erro = it }
                                    )
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

// 👨‍💼 Componente para o Item da Lista
@Composable
fun BarbeiroListItem(
    barbeiro: Barbeiro,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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
                    text = "💈 ${barbeiro.nome}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = barbeiro.especialidade,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary // Destaque para a especialidade
                )
                Text(
                    text = "Tel: ${barbeiro.telefone}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // Ações (Botões de Ícone)
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

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