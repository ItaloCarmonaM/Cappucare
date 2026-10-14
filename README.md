# CappuStock

Aplicación móvil Android para el control de stock de productos e insumos médicos de **Cappucare**.

Prototipo funcional desarrollado para la asignatura "Desarrollo de Aplicaciones Móviles".

## Integrantes

| Nombre | Rol en el proyecto                              |
|---|-------------------------------------------------|
| Nombre Apellido | Parte 1 – Ítalo Carmona Matus                   |
| Nombre Apellido | Parte 2 – Pantallas y formularios               |
| Nombre Apellido| Parte 3 – Animaciones, recursos nativos y cierre|

**Profesor:** José Candia Hernandez

## Contexto

Cappucare vende productos e insumos médicos y necesita mantener un control preciso de sus existencias.
Las diferencias entre las cantidades registradas y las reales, y las omisiones en los movimientos de entrada o venta,
dificultan detectar productos cercanos a agotarse. Esta aplicación permite registrar productos, ingresos y salidas,
consultar el historial y recibir avisos cuando un producto llega a su nivel mínimo.

## Alcance

**Incluye:** login simulado con perfiles, gestión de productos, registro de movimientos, historial,
alertas de stock bajo (notificación y vibración) y persistencia local con Room.

**No incluye:** integración con sistemas de ventas, compras o facturación, ni sincronización en línea.

## Perfiles de usuario

| Perfil | Permisos |
|---|---|
| **Encargado** | Crear y editar productos, registrar ingresos y salidas, ver historial y alertas. |
| **Consultor** | Consultar disponibilidad y registrar salidas por venta. |

## Funcionalidades

- RF1. Inicio de sesión simulado con perfil
- RF2. Listado y búsqueda de productos con su estado de stock
- RF3. Creación y edición de productos (solo Encargado)
- RF4. Registro de ingresos de mercadería (solo Encargado)
- RF5. Registro de salidas con motivo
- RF6. Historial de movimientos
- RF7. Vista de productos en stock bajo o agotados
- RF8. Notificación al alcanzar el nivel mínimo
- RF9. Persistencia de datos en el dispositivo (Room)

### Recursos nativos utilizados

1. **Notificaciones:** aviso cuando un producto alcanza su nivel mínimo.
2. **Vibración:** retroalimentación háptica al generarse una alerta de stock bajo.

## Reglas de negocio

| N° | Regla                                                                                                                                        |
|---|----------------------------------------------------------------------------------------------------------------------------------------------|
| RN1 | El código del producto es único y obligatorio.                                                                                               |
| RN2 | La cantidad de un producto no puede ser negativa.                                                                                            |
| RN3 | Una salida no puede superar el stock disponible.                                                                                             |
| RN4 | Ingresos y salidas deben tener una cantidad mayor a 0.                                                                                       |
| RN5 | Toda salida lleva un motivo: venta, producto vencido, producto dañado o corrección de stock.                                                 |
| RN6 | Todo movimiento actualiza el stock y queda en el historial con fecha, tipo, cantidad, motivo y responsable.                                  |
| RN7 | Estado del producto: **Agotado** si la cantidad es 0, **Stock bajo** si es menor o igual al nivel mínimo, **Disponible** en los demás casos. |
| RN8 | Se notifica una sola vez al llegar al nivel mínimo, hasta que el producto se reponga.                                                        |
| RN9 | Solo el Encargado crea o edita productos y registra ingresos.                                                                                |

## Tecnologías

- Kotlin
- Jetpack Compose
- Arquitectura MVVM con `StateFlow`
- Navigation Compose
- Room (persistencia local) con KSP
- Android Studio Rabbit 1 | 2026.2.1 (AGP 9.4)

## Estructura del proyecto

```
app/src/main/java/com/example/cappucare/
├── model/          Entidades Room y enums (Usuario, Producto, Movimiento)
├── data/           DAOs y base de datos
├── repository/     Acceso a datos
├── viewmodel/      Estado de la UI y validaciones
├── navigation/     Rutas y NavHost
├── notification/   Notificaciones y vibración
└── ui/
    ├── screens/    Pantallas
    ├── components/ Componentes reutilizables
    └── utils/      Utilidades (WindowSizeClass)
```

## Cómo ejecutar

1. Instalar **Android Studio Rabbit 1 | 2026.2.1** o superior.
2. Clonar el repositorio:
   ```bash
   git clone https://github.com/ItaloCarmonaM/Cappucare.git
   ```
3. Abrir la carpeta del proyecto en Android Studio y esperar la sincronización de Gradle.
4. Crear un emulador en "Device Manager" con imagen API 28 o superior (o conectar un dispositivo Android).
5. Ejecutar con el botón "Run".


## Organización del trabajo

- **Planificación:** tablero de Trello — _[Enlace aquí](https://trello.com/invite/b/6aa7f36e46dce9a5270d1bae/ATTIbdfcfc8cbb455a7b32befe8ddcb8a426B0654338/desarrollo-aplicaciones-moviles)_
- **Commits:** mensajes técnicos que describen el cambio realizado.
