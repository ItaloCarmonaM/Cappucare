
package com.example.cappucare.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.example.cappucare.model.WikipediaUiState

// Tarjeta "Información de referencia" con el resumen del artículo de Wikipedia
@Composable
fun TarjetaWikipedia(
    estado: WikipediaUiState,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Text("Información de referencia", style = MaterialTheme.typography.titleMedium)
            }

            when (estado) {
                WikipediaUiState.Cargando -> {
                    CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                }

                is WikipediaUiState.Exito -> {
                    val resumen = estado.resumen
                    resumen.title?.let { Text(it, style = MaterialTheme.typography.titleSmall) }
                    resumen.description?.let { Text(it, style = MaterialTheme.typography.labelMedium) }
                    Text(resumen.extract.orEmpty(), style = MaterialTheme.typography.bodyMedium)
                    resumen.urlArticulo?.let { url ->
                        val uriHandler = LocalUriHandler.current
                        TextButton(onClick = { uriHandler.openUri(url) }) {
                            Text("Ver en Wikipedia")
                        }
                    }
                    Text("Fuente: Wikipedia", style = MaterialTheme.typography.labelSmall)
                }

                WikipediaUiState.SinInformacion -> {
                    Text("Sin información disponible", style = MaterialTheme.typography.bodyMedium)
                }

                is WikipediaUiState.Error -> {
                    Text(
                        estado.mensaje,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(onClick = onReintentar) { Text("Reintentar") }
                }
            }
        }
    }
}
