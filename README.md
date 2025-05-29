# Ualá Cities Challenge

Este proyecto es una solución al desafío técnico propuesto por **Ualá** para el puesto de desarrollador Android. La app descarga y visualiza una lista de aproximadamente **200.000 ciudades**, permitiendo:

- **Búsqueda por prefijo** (case-insensitive, optimizada).
- **Marcado como favorito** con persistencia local.
- **Navegación en mapa** usando Google Maps.
- **Pantalla de detalle** por ciudad seleccionada.
- **Soporte completo para orientación vertical y horizontal.**

Toda la interfaz fue desarrollada usando **Jetpack Compose**, siguiendo una arquitectura **MVVM** con enfoque en **Clean Architecture**.

---

## Arquitectura

La estructura del proyecto sigue los principios de Clean Architecture:

- `data`: fuentes remotas y locales, modelos DTO y mappers.
- `domain`: casos de uso y modelos del dominio.
- `presentation`: lógica de UI y estado, utilizando `ViewModel`, `StateFlow` y `Jetpack Compose`.

### Principales decisiones técnicas:

- Se utiliza `Room` para persistir las ciudades marcadas como favoritas.
- El `Repository` fusiona los datos remotos (lista completa de ciudades) con los locales (favoritos) de forma eficiente.
- La lógica de paginación fue implementada manualmente en el `ViewModel` para evitar saturar la UI al mostrar 200k elementos a la vez.

---

## Enfoque para grandes volúmenes de datos

Para manejar eficientemente el listado de 200.000 ciudades:

- Se descarga el JSON una única vez al iniciar la app.
- El filtrado por prefijo se realiza con `asSequence` y `filter` sobre la lista pre-cargada, evitando recomposiciones innecesarias.
- Se implementó **paginación en el `ViewModel`**, cargando de a 1000 ciudades por página para mejorar el rendimiento de la UI.
- Los resultados filtrados se ordenan alfabéticamente por `nombre de ciudad` y `país`.

---


### Uso de ViewModel compartido para el detalle de ciudad

Una decisión técnica clave fue utilizar un `ViewModel` compartido entre pantallas para gestionar el estado de la ciudad seleccionada.

Esto permite:

- **Evitar llamadas innecesarias a la red**: al seleccionar una ciudad desde la lista, se guarda en memoria usando un `StateFlow` (`selectedCity`), permitiendo acceder al detalle sin tener que reconsultar datos.
- **Mantener una navegación eficiente**: el estado fluye directamente desde el `ViewModel`, evitando pasar grandes datos por navegación.
- **Mejorar la performance y UX**: se reduce la carga de red, se evita trabajo adicional del repositorio, y se mejora la experiencia del usuario al navegar.

Ejemplo:

```kotlin
fun selectCity(city: CityModel) {
    _selectedCity.value = city
}

val selectedCity by viewModel.selectedCity.collectAsState()
```

## ViewModel con paginación y conectividad

El `CitiesViewModel`:

- Observa la conectividad (`NetworkConnectivityObserver`) para volver a cargar datos en caso de error si se recupera internet.
- Usa `StateFlow` para exponer los estados de carga (`citiesState`, `searchState`, `favoriteCitiesState`, etc.).
- Contiene lógica para:
  - Paginación (`loadNextPage()`).
  - Búsqueda (`searchCities()`).
  - Favoritos (`toggleFavorite()`).
  - Visibilidad de búsqueda, tabs, y selección de ciudad.


---

## Dependencias utilizadas

```kotlin
// ROOM
implementation(libs.room.runtime)
implementation(libs.room.ktx)
ksp(libs.room.ksp)
testImplementation(libs.room.testing)

// Dagger core
implementation(libs.dagger)
kapt(libs.dagger.compiler)

// Hilt DI
implementation(libs.hilt.android)
kapt(libs.hilt.android.compiler)
implementation(libs.hilt.navigation.compose)

// Retrofit, Moshi y OkHttp
implementation(libs.retrofit)
implementation(libs.retrofit.converter.moshi)
implementation(libs.moshi)
implementation(libs.moshi.kotlin)
ksp(libs.moshi.kotlin.codegen)
implementation(libs.okhttp)

// Unit testing
testImplementation(libs.junit)
testImplementation(libs.kotlinx.coroutines.test)
testImplementation(libs.turbine)

// Mocking
testImplementation(libs.mockk)
testImplementation(libs.truth)

// Mapas
implementation(libs.maps.compose)
implementation(libs.play.services.maps)
