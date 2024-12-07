package com.mss.asistenciaapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mss.asistenciaapp.data.network.ApiClient

import com.mss.asistenciaapp.data.models.Trabajador
import kotlinx.coroutines.launch

@Composable
fun AgregarTrabajadorScreen(navController: NavController) {
    var dni by remember { mutableStateOf("") }
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(scaffoldState = scaffoldState)
        { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Top
            ) {
                Text(text = "Agregar Trabajador", style = MaterialTheme.typography.h6)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = dni,
                    onValueChange = { dni = it },
                    label = { Text("DNI") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nombres,
                    onValueChange = { nombres = it },
                    label = { Text("Nombres") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = apellidos,
                    onValueChange = { apellidos = it },
                    label = { Text("Apellidos") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            // Validación básica
                            if (dni.isEmpty() || nombres.isEmpty() || apellidos.isEmpty()) {
                                scaffoldState.snackbarHostState.showSnackbar("Todos los campos son obligatorios.")
                            } else {
                                // Llamada al backend para registrar el trabajador
                                try {
                                    val trabajador = Trabajador(dni, nombres, apellidos)
                                    val response = ApiClient.apiService.addTrabajador(trabajador)
                                    if (response.isSuccessful) {
                                        scaffoldState.snackbarHostState.showSnackbar("Trabajador registrado correctamente.")
                                        navController.popBackStack() // Regresa a la lista de trabajadores
                                    } else {
                                        scaffoldState.snackbarHostState.showSnackbar("Error al registrar trabajador.")
                                    }
                                } catch (e: Exception) {
                                    scaffoldState.snackbarHostState.showSnackbar("Error de conexión.")
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrar")
                }
            }
    }
}