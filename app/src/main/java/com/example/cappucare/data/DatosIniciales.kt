package com.example.cappucare.data

import com.example.cappucare.model.MotivoSalida
import com.example.cappucare.model.Movimiento
import com.example.cappucare.model.Perfil
import com.example.cappucare.model.Producto
import com.example.cappucare.model.TipoMovimiento
import com.example.cappucare.model.Usuario

// Datos ficticios que se cargan la primera vez que se abre la app
object DatosIniciales {
    private const val DIA = 24L * 60 * 60 * 1000 // Día en milisegundos

    suspend fun cargar(db: AppDB) {
        // Solo se cargan si la base de datos está vacía
        if (db.usuarioDao().contar() > 0) return

        // Usuarios
        val idEncargado = db.usuarioDao().insertar(
            Usuario(nombre = "María González", correo = "encargado@cappucare.cl", clave = "cappu123", perfil = Perfil.ENCARGADO)
        ).toInt()
        val idConsultor = db.usuarioDao().insertar(
            Usuario(nombre = "Pedro Soto", correo = "consultor@cappucare.cl", clave = "cappu123", perfil = Perfil.CONSULTOR)
        ).toInt()

        // Productos (disponibles, stock bajo y agotados)
        val productos = listOf(
            Producto(codigo = "MED-001", nombre = "Guantes de nitrilo talla M", descripcion = "Caja de 100 unidades", categoria = "Protección", cantidad = 120, unidadMedida = "caja", nivelMinimo = 30, ubicacion = "Bodega A - Estante 1"),
            Producto(codigo = "MED-002", nombre = "Mascarillas quirúrgicas", descripcion = "Caja de 50 unidades", categoria = "Protección", cantidad = 25, unidadMedida = "caja", nivelMinimo = 30, ubicacion = "Bodega A - Estante 1"),
            Producto(codigo = "MED-003", nombre = "Alcohol gel 500 ml", descripcion = "Desinfectante de manos", categoria = "Higiene", cantidad = 60, unidadMedida = "unidad", nivelMinimo = 20, ubicacion = "Bodega A - Estante 2"),
            Producto(codigo = "MED-004", nombre = "Jeringas 5 ml", descripcion = "Caja de 100 unidades", categoria = "Material clínico", cantidad = 0, unidadMedida = "caja", nivelMinimo = 15, ubicacion = "Bodega B - Estante 1"),
            Producto(codigo = "MED-005", nombre = "Gasas estériles 10x10", descripcion = "Paquete de 20 unidades", categoria = "Material clínico", cantidad = 80, unidadMedida = "paquete", nivelMinimo = 25, ubicacion = "Bodega B - Estante 1"),
            Producto(codigo = "MED-006", nombre = "Termómetro digital", descripcion = "Medición de temperatura corporal", categoria = "Equipos", cantidad = 12, unidadMedida = "unidad", nivelMinimo = 10, ubicacion = "Bodega B - Estante 2"),
            Producto(codigo = "MED-007", nombre = "Tensiómetro digital", descripcion = "Medidor de presión de brazo", categoria = "Equipos", cantidad = 5, unidadMedida = "unidad", nivelMinimo = 5, ubicacion = "Bodega B - Estante 2"),
            Producto(codigo = "MED-008", nombre = "Suero fisiológico 500 ml", descripcion = "Solución salina estéril", categoria = "Soluciones", cantidad = 40, unidadMedida = "unidad", nivelMinimo = 15, ubicacion = "Bodega C - Refrigerado")
        )
        val ids = productos.map { db.productoDao().insertar(it).toInt() }

        // Algunos movimientos para que el historial no parta vacío
        val ahora = System.currentTimeMillis()
        db.movimientoDao().insertar(
            Movimiento(productoId = ids[0], usuarioId = idEncargado, tipo = TipoMovimiento.INGRESO, cantidad = 100, fecha = ahora - 5 * DIA)
        )
        db.movimientoDao().insertar(
            Movimiento(productoId = ids[1], usuarioId = idConsultor, tipo = TipoMovimiento.SALIDA, cantidad = 5, motivo = MotivoSalida.VENTA, fecha = ahora - 2 * DIA)
        )
        db.movimientoDao().insertar(
            Movimiento(productoId = ids[4], usuarioId = idEncargado, tipo = TipoMovimiento.SALIDA, cantidad = 10, motivo = MotivoSalida.PRODUCTO_VENCIDO, fecha = ahora - DIA)
        )
    }
}