package com.example.practica03_nu100221.ui.theme

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica03_nu100221.data.EstudiantesRepository
import com.example.practica03_nu100221.data.EstudianteResponse
import com.example.practica03_nu100221.data.RetrofitPhpClient
import com.example.practica03_nu100221.data.UploadResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

data class UiState(
    val lista: List<EstudianteResponse> = emptyList(),
    val nombre: String = "",
    val edad: String = "",
    val seleccionadoId: Int? = null,
    val fotoUri: Uri? = null,   // 👈 para la imagen seleccionada
    val loading: Boolean = false,
    val error: String? = null,
    val info: String? = null
)

class EstudiantesViewModel(
    private val repo: EstudiantesRepository = EstudiantesRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init { refresh() }

    fun setNombre(value: String) { _uiState.value = _uiState.value.copy(nombre = value) }
    fun setEdad(value: String)   { _uiState.value = _uiState.value.copy(edad = value) }
    fun setFotoUri(uri: Uri?)    { _uiState.value = _uiState.value.copy(fotoUri = uri) }

    fun seleccionar(est: EstudianteResponse) {
        _uiState.value = _uiState.value.copy(
            seleccionadoId = est.id,
            nombre = est.nombre,
            edad = est.edad.toString(),
            info = "Seleccionado ID ${est.id}"
        )
    }

    fun limpiarSeleccion() {
        _uiState.value = _uiState.value.copy(
            seleccionadoId = null, nombre = "", edad = "", fotoUri = null, info = "Limpio"
        )
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null, info = null)
            try {
                val data = repo.list()
                _uiState.value = _uiState.value.copy(lista = data, loading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(loading = false, error = e.message)
            }
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)!!
        val file = File(context.cacheDir, "temp_image.jpg")
        file.outputStream().use { output -> inputStream.copyTo(output) }
        return file
    }

    fun agregar(context: Context) {
        val nombre = _uiState.value.nombre.trim()
        val edad = _uiState.value.edad.toIntOrNull()
        val fotoUri = _uiState.value.fotoUri

        if (nombre.isEmpty() || edad == null) {
            _uiState.value = _uiState.value.copy(error = "Nombre y edad válidos requeridos")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null, info = null)
            try {
                var fotoUrl = ""

                // Subir imagen si existe
                if (fotoUri != null) {
                    val file = uriToFile(context, fotoUri)
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                    val uploadResponse: UploadResponse = RetrofitPhpClient.api.uploadFoto(body)
                    if (uploadResponse.success && uploadResponse.foto_url != null) {
                        fotoUrl = uploadResponse.foto_url
                    }
                }

                // Ahora llamamos al CRUD normal (FastAPI)
                repo.add(nombre, edad, fotoUrl)

                refresh()
                _uiState.value = _uiState.value.copy(
                    info = "Insertado",
                    nombre = "",
                    edad = "",
                    fotoUri = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(loading = false, error = e.message)
            }
        }
    }

    fun actualizar() {
        val id = _uiState.value.seleccionadoId ?: run {
            _uiState.value = _uiState.value.copy(error = "Selecciona un estudiante")
            return
        }
        val nombre = _uiState.value.nombre.trim()
        val edad = _uiState.value.edad.toIntOrNull()
        val foto = _uiState.value.nombre.trim()
        if (nombre.isEmpty() || edad == null) {
            _uiState.value = _uiState.value.copy(error = "Nombre y edad válidos requeridos")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null, info = null)
            try {
                repo.update(id, nombre, edad, foto)
                refresh()
                _uiState.value = _uiState.value.copy(info = "Actualizado")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(loading = false, error = e.message)
            }
        }
    }

    fun eliminar(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null, info = null)
            try {
                repo.delete(id)
                refresh()
                _uiState.value = _uiState.value.copy(info = "Eliminado")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(loading = false, error = e.message)
            }
        }
    }
}
