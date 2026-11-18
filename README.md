# CappuCare

Aplicación móvil Android para el control de existencias de productos e insumos médicos de **Cappucare**.
Prototipo funcional académico desarrollado para la asignatura **DSY1105 – Desarrollo de Aplicaciones Móviles**.
La app se conecta a dos microservicios propios desarrollados en **Spring Boot** y consume una API externa
(**Wikipedia**) para mostrar información de referencia de cada producto.

## Integrantes

| Nombre | Rol en el proyecto                                                                                       |
|---|----------------------------------------------------------------------------------------------------------|
| Ítalo Carmona | Microservicios, capa remota con Retrofit, repositorios y pruebas de repositorios                         |
| Isahac Zapata | Login, lista, detalle y formulario de producto, API externa (Wikipedia) y sus pruebas       |
| Luciano | Formulario de movimiento, historial, alertas, notificaciones, vibración y sus pruebas |

## Contexto

Cappucare vende productos e insumos médicos y necesita mantener un control preciso de sus existencias.
Las diferencias entre las cantidades registradas y las reales, y las omisiones en los movimientos de entrada o venta,
dificultan detectar productos próximos a agotarse. CappuStock permite registrar productos, ingresos y salidas,
consultar el historial y recibir avisos cuando un producto llega a su nivel mínimo.

Todo el trabajo usa **datos ficticios**. No se utilizan bases de datos, credenciales ni datos personales reales.

## Alcance

**Incluye:** Login con perfiles, gestión de productos, registro de movimientos, historial,
alertas de stock bajo (notificación, vibración y aviso en pantalla), microservicios con base de datos
y consulta de información de referencia en Wikipedia.


**No incluye:** Integración con sistemas de ventas, compras o facturación.

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

-  RF1. Inicio de sesión con perfil (microservicio de usuarios)
-  RF2. Listado y búsqueda de productos con su estado de stock
-  RF3. Creación y edición de productos (solo Encargado)
-  RF4. Registro de ingresos de mercadería (solo Encargado)
-  RF5. Registro de salidas con motivo
-  RF6. Historial de movimientos
-  RF7. Vista de productos en stock bajo o agotados
-  RF8. Notificación al alcanzar el nivel mínimo
-  RF9. Persistencia de datos en base de datos mediante microservicios
-  RF10. Información de referencia del producto desde Wikipedia

### Recursos nativos

| Recurso | Uso | Permiso | Medidas aplicadas |
|---|---|---|---|
| **Notificaciones** | Aviso cuando un producto llega al nivel mínimo | `POST_NOTIFICATIONS` (se pide en tiempo de ejecución desde Android 13) | Canal de notificación creado al iniciar la app, verificación del permiso antes de notificar, `PendingIntent` inmutable, un aviso por producto |
| **Vibración** | Retroalimentación háptica al generarse la alerta | `VIBRATE` (permiso normal, sin solicitud al usuario) | Comprueba que el dispositivo tenga vibrador y usa `VibratorManager` en Android 12 o superior |
| **Internet** | Comunicación con los microservicios y con Wikipedia | `INTERNET` (permiso normal) | Tráfico HTTP sin cifrar permitido solo hacia `10.0.2.2` (emulador) mediante `network_security_config` |

## Reglas de negocio

Las reglas RN1 a RN6 y RN8 se validan en el microservicio de inventario. La app también valida los formularios
para avisar al usuario antes de enviar los datos.

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
El proyecto usa el patrón **MVVM** en la app y dos microservicios independientes en el servidor:

```
App Android (Kotlin + Compose)
   Screens → ViewModel → Repository → Retrofit
                                         │
              ┌──────────────────────────┼──────────────────────────┐
              ▼                          ▼                          ▼
       ms-usuarios (8081)        ms-inventario (8082)        API Wikipedia
       Spring Boot + MySQL       Spring Boot + MySQL         (API externa)
```
El proyecto usa el patrón **MVVM**, que separa la interfaz de la lógica y de los datos:

| Capa (app) | Responsabilidad | Ejemplos |
|---|---|---|
| Model | Datos de la aplicación | `Producto`, `Movimiento`, `Usuario` |
| Screens y Composables | Dibujan la interfaz y reenvían las acciones del usuario | `ProductosScreen`, `CampoTexto` |
| ViewModel | Estado de la pantalla (`StateFlow`), validaciones y lógica | `ProductoFormViewModel`, `MovimientoFormViewModel` |
| Repository | Único acceso a los datos; convierte los errores de red en `Result` | `ProductoRepository`, `MovimientoRepository` |
| Remote (Retrofit) | Define los endpoints y la configuración de red | `InventarioApiService`, `RetrofitInstance` |

El flujo es unidireccional: la pantalla envía eventos al ViewModel, el ViewModel consulta al repositorio y expone un
estado (cargando, éxito o error) que la pantalla observa.

## Tecnologías

**App móvil**
- Kotlin, Jetpack Compose y Material 3
- MVVM con `StateFlow` y corrutinas
- Retrofit y Gson (consumo de microservicios y API externa)
- Navigation Compose (rutas tipadas con `sealed class` y eventos de navegación)
- Window Size Classes (diseño adaptable)
- Notificaciones y vibración de Android
- Pruebas: Kotest, JUnit 5 y MockK
- Android Studio Rabbit 1 | 2026.2.1 (AGP 9.4) · minSdk 28 (Android 9)

**Microservicios**
- Java y Spring Boot (Spring Web, Spring Data JPA, Validation)
- MySQL

### Microservicio de usuarios (`ms-usuarios`, puerto 8081)

URL base desde el emulador: `http://10.0.2.2:8081/`

| Método | Endpoint | Cuerpo | Respuesta |
|---|---|---|---|
| POST | `/api/auth/login` | `{ "correo", "clave" }` | Usuario |
| GET | `/api/usuarios` | — | Lista de usuarios |
| GET | `/api/usuarios/{id}` | — | Usuario |
| POST | `/api/usuarios` | `{ "nombre", "correo", "clave", "perfil" }` | Usuario |
| PUT | `/api/usuarios/{id}` | `{ "nombre", "correo", "clave", "perfil" }` | Usuario |
| DELETE | `/api/usuarios/{id}` | — | 204 |

```json
{ "id": 1, "nombre": "María González", "correo": "encargado@cappucare.cl", "perfil": "ENCARGADO" }
```

### Microservicio de inventario (`ms-inventario`, puerto 8082)

URL base desde el emulador: `http://10.0.2.2:8082/`

| Método | Endpoint | Cuerpo | Respuesta |
|---|---|---|---|
| GET | `/api/productos?buscar=` | — | Lista de productos (`buscar` es opcional) |
| GET | `/api/productos/stock-bajo` | — | Lista de productos en stock bajo o agotados |
| GET | `/api/productos/{id}` | — | Producto |
| POST | `/api/productos` | Producto (sin `id`) | Producto |
| PUT | `/api/productos/{id}` | Producto | Producto |
| DELETE | `/api/productos/{id}` | — | 204 |
| GET | `/api/movimientos` | — | Lista de movimientos, del más reciente al más antiguo |
| POST | `/api/movimientos` | Movimiento | Producto actualizado y aviso de nivel mínimo |

```json
// Producto
{
  "id": 1, "codigo": "MED-001", "nombre": "Guantes de nitrilo talla M",
  "descripcion": "Caja de 100 unidades", "categoria": "Protección", "cantidad": 120,
  "unidadMedida": "caja", "nivelMinimo": 30, "ubicacion": "Bodega A - Estante 1"
}
 
// POST /api/movimientos (el motivo solo aplica a salidas; la fecha la asigna el servidor)
{
  "productoId": 6, "usuarioId": 1, "usuarioNombre": "María González",
  "tipo": "SALIDA", "cantidad": 2, "motivo": "VENTA"
}
 
// Respuesta de POST /api/movimientos
{ "producto": { "...": "producto actualizado" }, "alcanzoNivelMinimo": true }
 
// Elemento de GET /api/movimientos (fecha en milisegundos)
{
  "movimiento": { "id": 1, "productoId": 6, "usuarioId": 1, "tipo": "SALIDA", "cantidad": 2, "motivo": "VENTA", "fecha": 1763500000000 },
  "productoNombre": "Termómetro digital",
  "usuarioNombre": "María González"
}
```

**Valores permitidos:** `perfil` = `ENCARGADO` | `CONSULTOR` · `tipo` = `INGRESO` | `SALIDA` ·
`motivo` = `VENTA` | `PRODUCTO_VENCIDO` | `PRODUCTO_DANADO` | `CORRECCION_STOCK`.

### API externa: Wikipedia

Se usa en el detalle del producto para mostrar una ficha de referencia (tarjeta "Información de referencia").
Si el producto no tiene artículo, se muestra "Sin información disponible". Esta consulta es independiente de los
microservicios: si falla, el resto de la pantalla sigue funcionando.

| Método | Endpoint | Ejemplo |
|---|---|---|
| GET | `https://es.wikipedia.org/api/rest_v1/page/summary/{titulo}` | `.../summary/Termómetro` |

## Estructura del proyecto

```
app/src/main/java/com/example/cappucare/
├── CappucareApp.kt   Application: repositorios y canal de notificaciones
├── MainActivity.kt   Actividad única; solicita el permiso de notificaciones
├── model/            Modelos de datos, enums y estados de formulario
├── data/remote/      Retrofit: servicios de la API, solicitudes, manejo de errores y RetrofitInstance
├── repository/       Repositorios de usuarios, productos, movimientos y Wikipedia
├── viewmodel/        ViewModels, sesión y validaciones (validacion/Validadores)
├── navigation/       Rutas, eventos de navegación y NavHost dividido por módulo
├── notification/     Notificaciones y vibración
└── ui/
    ├── components/   Campos de texto, selector, chip de estado, tarjeta de producto y menú inferior
    ├── screens/      Pantallas de la aplicación
    ├── theme/        Colores, tipografía y tema
    └── utils/        Window Size Classes y textos de enums
 
app/src/test/java/    Pruebas unitarias (Kotest, JUnit 5 y MockK)
```

## Cómo ejecutar

### Requisitos

- Android Studio **Rabbit 1 | 2026.2.1** o superior
- JDK 17 o superior
- MySQL en ejecución
- Emulador con imagen **API 28** o superior (o un dispositivo Android)
### Pasos

1. Clonar el repositorio:
```bash
git clone https://github.com/ItaloCarmonaM/Cappucare.git
```
2. Crear las bases de datos en MySQL:
```sql
CREATE DATABASE cappustock_usuarios; POR CONFIRMAR
CREATE DATABASE cappustock_inventario; POR CONFIRMAR
```
3. Revisar el usuario y la clave de MySQL en `src/main/resources/application.properties` de cada microservicio.
4. Iniciar `ms-usuarios` (puerto 8081) y luego `ms-inventario` (puerto 8082). La primera vez cargan los datos de prueba.
5. Abrir la carpeta de la app en Android Studio y esperar el **Sync** de Gradle.
6. Crear un emulador en **Device Manager** con imagen **API 28** o superior.
7. Ejecutar con el botón **Run** e ingresar con uno de los usuarios de prueba.
> El emulador accede al computador mediante `10.0.2.2`. Si se usa un dispositivo físico, hay que cambiar esa IP por la del
> computador en `RetrofitInstance` y en `network_security_config.xml`.

### Probar las alertas de stock

1. Ingresar como Encargado y abrir el detalle de **Termómetro digital** (stock 12, nivel mínimo 10).
2. Pulsar **Registrar movimiento**, elegir **Salida**, cantidad 2 y motivo **Venta**.
3. Al guardar se muestra la confirmación, un aviso en pantalla, la notificación y la vibración.
## Pruebas unitarias

Las pruebas cubren la lógica de validaciones, repositorios y ViewModels, usando **Kotest**, **JUnit 5** y **MockK**
con `kotlinx-coroutines-test`.

Para ejecutarlas: clic derecho en la carpeta `app/src/test/java` > **Run 'Tests in ...'**, o desde la terminal:

```bash
./gradlew test
```

## Organización del trabajo
- **Planificación:** tablero de Trello —https://trello.com/invite/b/6aa7f36e46dce9a5270d1bae/ATTIbdfcfc8cbb455a7b32befe8ddcb8a426B0654338/desarrollo-aplicaciones-moviles
- **Commits:** mensajes técnicos que describen el cambio realizado.

## Posibles mejoras para el futuro

- Autenticación con token (JWT) en los microservicios.
- Integración con procesos de ventas, compras, facturación o administración comercial.
- Lectura de códigos QR o de barras con la cámara para identificar productos.
