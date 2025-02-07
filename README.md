# Sistema de Control de Asistencia - Aplicación Móvil

## Descripción
Esta aplicación móvil desarrollada en Android con Kotlin y Jetpack Compose permite gestionar la asistencia de trabajadores mediante reconocimiento facial y registro fotográfico.

## Características Principales

### 1. Gestión de Trabajadores
- Registro de nuevos trabajadores
- Listado de trabajadores
- Visualización detallada de información por trabajador
- Validación de DNI y datos personales

### 2. Control de Asistencia
- Registro de asistencia mediante fotografía
- Captura de fotos usando la cámara del dispositivo
- Almacenamiento de registros con fecha y hora
- Vista previa de fotos capturadas

### 3. Interfaz de Usuario
- Diseño moderno con Material Design 3
- Navegación intuitiva
- Barra de navegación inferior
- Formularios con validación en tiempo real

## Estructura del Proyecto

### Componentes Principales
- `MainActivity.kt`: Punto de entrada de la aplicación y configuración de navegación
- `ui/`: Contiene todas las pantallas y componentes UI
  - `AddAsistenciaScreen.kt`: Pantalla para registro de asistencia
  - `AgregarTrabajadorScreen.kt`: Formulario de registro de trabajadores
  - `VerTrabajadoresScreen.kt`: Lista de trabajadores
  - `TrabajadorDetalleScreen.kt`: Detalles de trabajador individual
  - `CameraScreen.kt`: Manejo de la cámara para captura de fotos

### Componentes Reutilizables
- `components/`
  - `WorkerCard.kt`: Tarjeta para mostrar información de trabajador
  - `WorkerDropdown.kt`: Selector de trabajadores
  - `PhotoCard.kt`: Visualización de fotos capturadas

## Requisitos Técnicos

### Requisitos del Sistema
- Android SDK 21 o superior
- Kotlin 1.8.0 o superior
- Jetpack Compose 1.4.0 o superior

### Dependencias Principales
- Jetpack Compose UI
- CameraX para manejo de cámara
- Coil para carga de imágenes
- Retrofit para comunicación con API
- Material Design 3

## Configuración del Proyecto

### Pasos de Instalación
1. Clonar el repositorio
2. Abrir el proyecto en Android Studio
3. Sincronizar el proyecto con Gradle
4. Configurar el archivo `local.properties` con las variables necesarias

### Configuración de API
La aplicación requiere configuración de endpoints en `ApiClient.kt`:
```kotlin
BASE_URL = "tu_url_base_api"
```

## Funcionalidades Detalladas

### Registro de Trabajadores
- Validación de DNI (8 dígitos)
- Validación de nombres y apellidos
- Verificación de duplicados
- Mensajes de error descriptivos

### Control de Asistencia
- Captura de foto con preview
- Selección de trabajador mediante búsqueda
- Registro automático de fecha y hora
- Almacenamiento local de fotos

### Visualización de Datos
- Lista paginada de trabajadores
- Filtros de búsqueda
- Historial de asistencias por trabajador
- Visualización de fotos registradas

## Contribución
Para contribuir al proyecto:
1. Fork del repositorio
2. Crear rama para nueva característica
3. Commit con mensajes descriptivos
4. Push a tu fork
5. Crear Pull Request
