# CappuStock

Aplicación móvil Android para el control de existencias de productos e insumos médicos de **Cappucare**.
Prototipo funcional académico desarrollado para la asignatura **DSY1105 – Desarrollo de Aplicaciones Móviles**.

## Integrantes

| Nombre | Rol en el proyecto |
|---|---|
| Ítalo Carmona | Base, datos y lógica: modelos, Room, repositorios, navegación, ViewModels y sesión |
| Isahac Zapata | Productos y acceso: login, lista, detalle y formulario de producto, componentes y diseño adaptable |
| Luciano | Movimientos y recursos nativos: formulario de movimiento, historial, alertas, notificaciones y vibración |

## Contexto

Cappucare vende productos e insumos médicos y necesita mantener un control preciso de sus existencias.
Las diferencias entre las cantidades registradas y las reales, y las omisiones en los movimientos de entrada o venta,
dificultan detectar productos próximos a agotarse. CappuStock permite registrar productos, ingresos y salidas,
consultar el historial y recibir avisos cuando un producto llega a su nivel mínimo.

Todo el trabajo usa **datos ficticios**. No se utilizan bases de datos, credenciales ni datos personales reales.

## Alcance

**Incluye:** Login simulado con perfiles, gestión de productos, registro de movimientos, historial,
alertas de stock bajo (notificación, vibración y aviso en pantalla) y persistencia local con Room.

**No incluye:** integración con sistemas de ventas, compras o facturación, ni sincronización en línea.
Ambas quedan contempladas para una etapa posterior.

## Perfiles de usuario

| Perfil | Permisos |
|---|---|
| **Encargado** | Crear y editar productos, registrar ingresos y salidas, ver historial y alertas. |
| **Consultor** | Consultar disponibilidad y alertas, y registrar salidas por venta. No ve el historial. |

## Usuarios de prueba

| Nombre | Correo | Clave | Perfil |
|---|---|---|---|
| María González | encargado@cappucare.cl | cappu123 | Encargado |
| Pedro Soto | consultor@cappucare.cl | cappu123 | Consultor |

La primera vez que se abre la app se cargan datos ficticios: 8 productos (disponibles, en stock bajo y agotados)
y algunos movimientos de ejemplo.

## Funcionalidades

-  RF1. Inicio de sesión simulado con perfil
-  RF2. Listado y búsqueda de productos con su estado de stock
-  RF3. Creación y edición de productos (solo Encargado)
-  RF4. Registro de ingresos de mercadería (solo Encargado)
-  RF5. Registro de salidas con motivo
-  RF6. Historial de movimientos
-  RF7. Vista de productos en stock bajo o agotados
-  RF8. Notificación al alcanzar el nivel mínimo
-  RF9. Persistencia de datos en el dispositivo (Room)

### Recursos nativos

| Recurso | Uso | Permiso | Medidas aplicadas |
|---|---|---|---|
| **Notificaciones** | Aviso cuando un producto llega al nivel mínimo | `POST_NOTIFICATIONS` (se pide en tiempo de ejecución desde Android 13) | Canal de notificación creado al iniciar la app, verificación del permiso antes de notificar, `PendingIntent` inmutable, un aviso por producto |
| **Vibración** | Retroalimentación háptica al generarse la alerta | `VIBRATE` (permiso normal, sin solicitud al usuario) | Comprueba que el dispositivo tenga vibrador y usa `VibratorManager` en Android 12 o superior |

Si el usuario niega el permiso de notificaciones, la app no se cierra: el aviso se muestra igualmente dentro de la pantalla
e indica cómo activar las notificaciones.

## Reglas de negocio

| N° | Regla |
|---|---|
| RN1 | El código del producto es único y obligatorio. |
| RN2 | La cantidad de un producto nunca es negativa. |
| RN3 | Una salida no puede superar el stock disponible. |
| RN4 | Ingresos y salidas deben tener una cantidad mayor a 0. |
| RN5 | Toda salida lleva un motivo: venta, producto vencido, producto dañado o corrección de stock. |
| RN6 | Todo movimiento actualiza el stock y queda en el historial con fecha, tipo, cantidad, motivo y responsable. |
| RN7 | Estado del producto: **Agotado** si la cantidad es 0, **Stock bajo** si es menor o igual al nivel mínimo, **Disponible** en los demás casos. |
| RN8 | Se notifica una sola vez al llegar al nivel mínimo, hasta que el producto se reponga. |
| RN9 | Solo el Encargado crea o edita productos y registra ingresos. El Consultor solo registra ventas. |

## Arquitectura

El proyecto usa el patrón **MVVM**, que separa la interfaz de la lógica y de los datos:

| Capa | Responsabilidad | Ejemplos |
|---|---|---|
| Model | Datos de la aplicación y tablas de la base | `Producto`, `Movimiento`, `Usuario` |
| Screens y Composables | Dibujan la interfaz y reenvían las acciones del usuario | `ProductosScreen`, `CampoTexto` |
| ViewModel | Estado de la pantalla (`StateFlow`), validaciones y lógica | `ProductoFormViewModel`, `MovimientoFormViewModel` |
| Repository | Único acceso a los datos | `ProductoRepository`, `MovimientoRepository` |
| Room (DAO y base) | Guarda y lee desde SQLite | `ProductoDao`, `AppDB` |

El flujo es unidireccional: la pantalla envía eventos al ViewModel, el ViewModel consulta al repositorio y expone un
estado que la pantalla observa. Las consultas de Room devuelven `Flow`, por lo que las listas se actualizan solas.

## Tecnologías

- Kotlin, Jetpack Compose y Material 3
- MVVM con `StateFlow`
- Navigation Compose (rutas tipadas con `sealed class` y eventos de navegación)
- Room con KSP (persistencia local)
- Window Size Classes (diseño adaptable)
- Notificaciones y vibración de Android
- Android Studio Rabbit 1 | 2026.2.1 (AGP 9.4) · minSdk 28 (Android 9)

## Estructura del proyecto

```
app/src/main/java/com/example/cappucare/
├── CappucareApp.kt   Application: base de datos, repositorios, canal de notificaciones y datos de prueba
├── MainActivity.kt   Actividad única; solicita el permiso de notificaciones
├── model/            Entidades Room, enums y estados de formulario
├── data/             AppDB, DAOs y datos iniciales
├── repository/       Repositorios de usuarios, productos y movimientos
├── viewmodel/        ViewModels, sesión y validaciones (validacion/Validadores)
├── navigation/       Rutas, eventos de navegación y NavHost dividido por módulo
├── notification/     Notificaciones y vibración
└── ui/
    ├── components/   Campos de texto, selector, chip de estado, tarjeta de producto y menú inferior
    ├── screens/      Pantallas de la aplicación
    ├── theme/        Colores, tipografía y tema
    └── utils/        Window Size Classes y textos de enums
```

## Cómo ejecutar

1. Instalar **Android Studio Rabbit 1 | 2026.2.1** o superior.
2. Clonar el repositorio:
   ```bash
   git clone (https://github.com/ItaloCarmonaM/Cappucare.git)
   ```
3. Abrir la carpeta del proyecto en Android Studio y esperar el **Sync** de Gradle.
4. Crear un emulador en **Device Manager** con imagen **API 28** o superior (o conectar un dispositivo Android).
5. Ejecutar con el botón **Run** e ingresar con uno de los usuarios de prueba.

### Probar las alertas de stock

1. Ingresar como Encargado y abrir el detalle de **Termómetro digital** (stock 12, nivel mínimo 10).
2. Pulsar **Registrar movimiento**, elegir **Salida**, cantidad 2 y motivo **Venta**.
3. Al guardar se muestra la confirmación, un aviso en pantalla, la notificación y la vibración.

## Organización del trabajo

- **Planificación:** tablero de Trello —https://trello.com/invite/b/6aa7f36e46dce9a5270d1bae/ATTIbdfcfc8cbb455a7b32befe8ddcb8a426B0654338/desarrollo-aplicaciones-moviles
- **Control de versiones:** trabajo por ramas `feature/...` y unión a `main` mediante Pull Request.
- **Commits:** mensajes técnicos que describen el cambio realizado.

## Posibles mejoras para el futuro

- Sincronización de la información con un servidor mediante conexión a internet.
- Integración con procesos de ventas, compras, facturación o administración comercial.
- Lectura de códigos QR o de barras con la cámara para identificar productos.
