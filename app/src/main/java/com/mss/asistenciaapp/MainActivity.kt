package com.mss.asistenciaapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mss.asistenciaapp.ui.theme.AsistenciaAppTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
                    AgregarTrabajadorScreen(navController = navController)
                }
                composable(
                    "trabajadorDetalle/{trabajadorDni}",
                    arguments = listOf(navArgument("trabajadorDni") { type = NavType.StringType })
                ) { backStackEntry ->
                    val trabajadorDni = backStackEntry.arguments?.getString("trabajadorDni")
                    if (trabajadorDni != null) {
                        TrabajadorDetalleScreen(navController = navController, trabajadorDni = trabajadorDni)
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { navController.navigate("addAsistencia") }) {
            Text("Agregar nueva asistencia")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("verTrabajadores") }) {
            Text("Ver trabajadores")
        }
    }
}