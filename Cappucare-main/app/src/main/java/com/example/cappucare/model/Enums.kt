package com.example.cappucare.model

// Perfiles de usuario de la aplicación
enum class Perfil { ENCARGADO, CONSULTOR }

// Tipo de movimiento que modifica las existencias
enum class TipoMovimiento { INGRESO, SALIDA }

enum class MotivoSalida {
    VENTA,                // Producto vendido a un cliente
    PRODUCTO_VENCIDO,     // Retirado por fecha de vencimiento
    PRODUCTO_DANADO,      // Retirado por daño o mal estado
    CORRECCION_STOCK      // Corrección por diferencia entre lo registrado y lo real
}

// Estado del producto según sus existencias
enum class EstadoProducto { DISPONIBLE, STOCK_BAJO, AGOTADO }