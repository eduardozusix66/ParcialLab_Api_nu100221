package com.example.practica03_nu100221.ui.theme

import android.net.Uri
import coil.compose.AsyncImage

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstudiantesScreen(vm: EstudiantesViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("APP Lista Estudiantes NU100221 Api FastApi CRUD - Custom Api Php para fotos") })
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Formulario
            Text(text = if (state.seleccionadoId == null) "Nuevo estudiante" else "Editar ID ${state.seleccionadoId}",
                style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.nombre,
                onValueChange = vm::setNombre,
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.edad,
                onValueChange = vm::setEdad,
                label = { Text("Edad") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )


            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                vm.setFotoUri(uri)
            }

            Spacer(Modifier.height(8.dp))
            Button(onClick = { launcher.launch("image/*") }) {
                Text("Seleccionar Imagen")
            }

            state.fotoUri?.let { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = "Preview Foto",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(8.dp)
                )
            }

            Spacer(Modifier.height(12.dp))
            val context = LocalContext.current

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.agregar(context) }, enabled = !state.loading) {
                    Text("Agregar")
                }
                Button(
                    onClick = vm::actualizar,
                    enabled = state.seleccionadoId != null && !state.loading
                ) {
                    Text("Actualizar")
                }
                OutlinedButton(onClick = vm::limpiarSeleccion, enabled = !state.loading) {
                    Icon(Icons.Default.Clear, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Limpiar")
                }
                OutlinedButton(onClick = vm::refresh, enabled = !state.loading) {
                    Text("Refrescar")
                }
            }

            if (state.loading) {
                LinearProgressIndicator(Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp))
            }

            state.error?.let {
                Spacer(Modifier.height(8.dp))
                AssistChip(onClick = { }, label = { Text("Error: $it") })
            }
            state.info?.let {
                Spacer(Modifier.height(8.dp))
                AssistChip(onClick = { }, label = { Text(it) })
            }

            Spacer(Modifier.height(16.dp))
            Text("Listado", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1100.dp),
               // verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(state.lista, key = { it.id }) { est ->
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            // Foto del estudiante
                            AsyncImage(
                                model = est.foto_url, // <<<<< usa la URL del backend
                                contentDescription = "Foto estudiante",
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                            )

                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("${est.nombre}", style = MaterialTheme.typography.titleSmall)
                                Text("Edad: ${est.edad}", style = MaterialTheme.typography.bodySmall)
                                Text("ID: ${est.id}", style = MaterialTheme.typography.bodySmall)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(onClick = { vm.seleccionar(est) }, enabled = !state.loading) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                                }
                                IconButton(onClick = { vm.eliminar(est.id) }, enabled = !state.loading) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                }
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(100.dp)) }
            }
        }
    }
}
