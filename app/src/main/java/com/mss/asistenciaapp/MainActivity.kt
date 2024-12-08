package com.mss.asistenciaapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mss.asistenciaapp.ui.AddAsistenciaScreen
import com.mss.asistenciaapp.ui.AgregarTrabajadorScreen
import com.mss.asistenciaapp.ui.TrabajadorDetalleScreen
import com.mss.asistenciaapp.ui.VerTrabajadoresScreen

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "main") {
                composable("main") {
                    MainScreen(navController)
                }
                composable("addAsistencia") {
                    AddAsistenciaScreen(navController)
                }
                composable("verTrabajadores") {
                    VerTrabajadoresScreen(navController)
                }
                composable("agregarTrabajador") {
                    AgregarTrabajadorScreen(navController)
                }
                composable(
                    "trabajadorDetalle/{trabajadorDni}",
                    arguments = listOf(navArgument("trabajadorDni") { type = NavType.StringType })
                ) { backStackEntry ->
                    val trabajadorDni = backStackEntry.arguments?.getString("trabajadorDni")
                    if (trabajadorDni != null) {
                        TrabajadorDetalleScreen(navController, trabajadorDni)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Gestión de Asistencias") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    selected = true,
                    onClick = { /* Mantenerse en la pantalla principal */ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Trabajadores") },
                    label = { Text("Trabajadores") },
                    selected = false,
                    onClick = { navController.navigate("verTrabajadores") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¿Qué deseas hacer?",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Button(
                onClick = { navController.navigate("addAsistencia") },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar asistencia")
                Spacer(Modifier.width(8.dp))
                Text("Agregar nueva asistencia")
            }

            Button(
                onClick = { navController.navigate("verTrabajadores") },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Icon(Icons.Filled.List, contentDescription = "Ver trabajadores")
                Spacer(Modifier.width(8.dp))
                Text("Ver trabajadores")
            }
        }
    }
}
