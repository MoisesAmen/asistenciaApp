package com.mss.asistenciaapp.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mss.asistenciaapp.data.network.ApiClient

import com.mss.asistenciaapp.data.models.Trabajador
import kotlinx.coroutines.launch
import kotlin.math.log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarTrabajadorScreen(navController: NavController) {
    var dni by remember { mutableStateOf("") }
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Error messages
    var dniError by remember { mutableStateOf<String?>(null) }
    var nombresError by remember { mutableStateOf<String?>(null) }
    var apellidosError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Agregar Trabajador") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Registrar nuevo trabajador",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = dni,
                onValueChange = {
                    dni = it
                    dniError = if (it.length == 8 && it.all { char -> char.isDigit() }) null else "Debe ser un número de 8 dígitos"
                },
                label = { Text("DNI") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                isError = dniError != null
            )
            dniError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nombres,
                onValueChange = {
                    nombres = it
                    nombresError = if (it.isNotBlank() && it.all { char -> char.isLetter() || char.isWhitespace() }) null else "Solo letras y espacios permitidos"
                },
                label = { Text("Nombres") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                isError = nombresError != null
            )
            nombresError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = apellidos,
                onValueChange = {
                    apellidos = it
                    apellidosError = if (it.isNotBlank() && it.all { char -> char.isLetter() || char.isWhitespace() }) null else "Solo letras y espacios permitidos"
                },
                label = { Text("Apellidos") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                isError = apellidosError != null
            )
            apellidosError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        if (dniError == null && nombresError == null && apellidosError == null) {
                            try {
                                if (dni.isEmpty() || nombres.isEmpty() || apellidos.isEmpty()) {
                                    snackbarHostState.showSnackbar("Todos los campos son obligatorios.")
                                } else {
                                    // Verificar si el DNI ya existe en la base de datos
                                    val existingWorker = ApiClient.apiService.getTrabajadorExiste(dni)

                                    if (existingWorker) {
                                        snackbarHostState.showSnackbar("El DNI ya está registrado.")
                                    } else {
                                        val trabajador = Trabajador(dni, nombres, apellidos)
                                        val response = ApiClient.apiService.addTrabajador(trabajador)
                                        if (response.isSuccessful) {
                                            snackbarHostState.showSnackbar("Trabajador registrado correctamente.")
                                            navController.popBackStack()
                                        } else {
                                            snackbarHostState.showSnackbar("Error al registrar trabajador.")
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error de conexión.")
                            }
                        } else {
                            snackbarHostState.showSnackbar("Corrige los errores en el formulario.")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Registrar")
            }
        }
    }
}
