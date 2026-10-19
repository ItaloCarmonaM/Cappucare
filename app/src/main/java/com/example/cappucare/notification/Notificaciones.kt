package com.example.cappucare.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.cappucare.MainActivity
import com.example.cappucare.model.EstadoProducto
import com.example.cappucare.model.Producto

// Notificaciones de stock bajo
object Notificaciones {

    private const val CANAL_ID = "stock_bajo"

    // Crea el canal de notificaciones
    fun crearCanal(context: Context) {
        val canal = NotificationChannel(
            CANAL_ID,
            "Stock bajo",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Avisa cuando un producto llega al mínimo"
            enableVibration(true)
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }


    private fun tienePermiso(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    context, android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

    // Muestra el aviso de un producto.
    @SuppressLint("MissingPermission")
    fun notificarStockBajo(context: Context, producto: Producto) {
        if (!tienePermiso(context)) return

        val texto = if (producto.estado == EstadoProducto.AGOTADO) {
            "${producto.nombre} está agotado"
        } else {
            "${producto.nombre} llegó a su nivel mínimo (${producto.cantidad} ${producto.unidadMedida})"
        }

        // Al tocar la notificación se abre la app
        val abrirApp = PendingIntent.getActivity(
            context,
            producto.id,
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Alerta de stock bajo")
            .setContentText(texto)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(abrirApp)
            .setAutoCancel(true)
            .build()

        // Se usa el id del producto para no repetir la misma notificación
        NotificationManagerCompat.from(context).notify(producto.id, notificacion)
    }
}
