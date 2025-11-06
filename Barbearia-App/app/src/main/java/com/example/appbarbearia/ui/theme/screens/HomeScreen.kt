package com.seuapp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appagendamento.R // Certifique-se de que R.drawable.barba existe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var menuAberto by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(if (menuAberto) DrawerValue.Open else DrawerValue.Closed)

    LaunchedEffect(drawerState.isClosed) {
        if (drawerState.isClosed) {
            menuAberto = false
        }
    }
    LaunchedEffect(menuAberto) {
        if (menuAberto) {
            drawerState.open()
        } else {
            drawerState.close()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        "Menu BarberTech",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Navegação Rápida",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
                Divider()
                Spacer(Modifier.height(8.dp))
                DrawerItem("🏠 Home") {
                    navController.navigate("home") { popUpTo("home") { inclusive = true } }
                    menuAberto = false
                }
                DrawerItem("🧑‍💼 Barbeiros") {
                    navController.navigate("barbeiros")
                    menuAberto = false
                }
                DrawerItem("👥 Clientes") {
                    navController.navigate("clientes")
                    menuAberto = false
                }
                DrawerItem("✂️ Serviços") {
                    navController.navigate("servicos")
                    menuAberto = false
                }
                DrawerItem("📅 Agendamentos") {
                    navController.navigate("agendamentos")
                    menuAberto = false
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("BarberTech", fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = { menuAberto = !menuAberto }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(top = 1.dp)
            ) {

                val scale by rememberInfiniteTransition(label = "logo_scale").animateFloat(
                    initialValue = 1.0f,
                    targetValue = 1.05f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500, easing = CubicBezierEasing(0.4f, 0.0f, 0.6f, 1.0f)),
                        repeatMode = RepeatMode.Reverse
                    ), label = "logo_scale_animation"
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 🖼️ Imagem/Logo com Efeito de Animação e Sombra
                    Card(
                        modifier = Modifier
                            .size(200.dp)
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                // Linha problemática removida: shadowElevation = 8.dp
                            ),
                        shape = MaterialTheme.shapes.extraLarge,
                        // ✨ Elevação adicionada aqui no componente Card (Tipo Dp correto)
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.barba),
                            contentDescription = "Logo da Barbearia",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .clip(MaterialTheme.shapes.extraLarge)
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        text = "BarberTech",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Cuidando do seu estilo com tecnologia 💈",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Divider(
                        modifier = Modifier.width(80.dp),
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                }

                // 🌟 Botão CTA de Destaque
                Button(
                    onClick = { navController.navigate("agendamentos") },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 24.dp),
                    shape = MaterialTheme.shapes.medium,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text(
                        "Agendar Agora",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DrawerItem(text: String, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = {
            Text(
                text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}