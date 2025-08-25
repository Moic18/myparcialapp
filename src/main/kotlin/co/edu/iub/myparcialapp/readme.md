# AeroTech Flight Management

Sistema de gestión de reservas de vuelos desarrollado con Spring Boot y Kotlin. Este documento describe de forma detallada los requerimientos, diseño e implementación del proyecto.

## Estructura del Proyecto

```
.
├── Dockerfile
├── .dockerignore
├── postman_collection.json
├── build.gradle.kts
├── settings.gradle.kts
├── src
│   ├── main
│   │   ├── kotlin/co/edu/iub/myparcialapp
│   │   │   ├── config
│   │   │   ├── controllers
│   │   │   ├── dto
│   │   │   ├── entities
│   │   │   ├── exception
│   │   │   ├── repositories
│   │   │   ├── services
│   │   │   └── utils
│   │   └── resources
│   └── test
└── README.md
```

## Requerimientos Funcionales

### Clientes
1. **Búsqueda de vuelos**: los usuarios pueden buscar vuelos por origen, destino y fecha.
2. **Realizar reserva**: seleccionar un vuelo programado y registrar pasajeros.
3. **Consultar reservas**: visualizar las reservas propias con todos sus detalles.
4. **Cancelar reserva**: anular reservas propias hasta dos horas antes de la salida.

### Empleados
1. **Gestión de vuelos**: crear, actualizar y consultar vuelos.
2. **Consulta de reservas**: visualizar todas las reservas del sistema.
3. **Actualizar estado de vuelos**: cambiar estados (programado, retrasado, cancelado, completado).
4. **Gestión de pasajeros**: ver información de pasajeros por vuelo o reserva.

### Administradores
1. **Gestión de usuarios**: crear empleados y consultar usuarios del sistema.
2. **Reportes**: generar reportes de vuelos más reservados e ingresos por período.
3. **Gestión de aeronaves**: registrar aviones y establecer su capacidad.

## Requerimientos No Funcionales
- **Seguridad**: autenticación y autorización basada en roles mediante JWT.
- **Performance**: tiempo de respuesta menor a 2 segundos.
- **Disponibilidad**: servicio diseñado para operar 24/7.
- **Escalabilidad**: la arquitectura permite escalar hasta 1000 usuarios concurrentes.

## Reglas de Negocio
### Reservas
- Sólo se pueden reservar vuelos con estado `PROGRAMADO`.
- Se verifican asientos disponibles antes de confirmar la reserva.
- La cancelación está permitida hasta 2 horas antes del vuelo.

### Vuelos
- El origen debe ser diferente del destino.
- La fecha de llegada debe ser posterior a la fecha de salida.
- El precio del vuelo debe ser mayor a 0.

### Usuarios
- Los clientes sólo pueden ver sus propias reservas.
- Los empleados pueden ver todas las reservas pero no crear administradores.
- Sólo los administradores pueden generar reportes.

## Entidades Principales
- **Usuario**: representa clientes, empleados y administradores.
- **Vuelo**: contiene origen, destino, fechas, precio, aeronave y estado.
- **Reserva**: enlaza vuelos, clientes y pasajeros con fecha y estado.
- **Pasajero**: datos personales del pasajero asociado a una reserva.
- **Aeronave**: modelo de avión con su capacidad.

## API Endpoints

### Públicos
| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/health` | Verificación de estado del servicio. |
| `POST` | `/api/auth/login` | Autentica y devuelve token JWT. |
| `POST` | `/api/usuarios` | Registra un nuevo usuario. |
| `POST` | `/api/vuelos/buscar` | Búsqueda de vuelos por origen, destino y fecha. |

### Cliente
| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/api/reservas` | Crea una reserva para un vuelo. |
| `GET` | `/api/reservas/mis-reservas` | Lista reservas del cliente autenticado. |
| `GET` | `/api/reservas/{id}` | Obtiene detalles de una reserva propia. |
| `DELETE` | `/api/reservas/{id}` | Cancela una reserva existente. |

### Administrador / Empleado
| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/api/vuelos` | Crea un vuelo. |
| `PUT` | `/api/vuelos/{id}` | Actualiza datos de un vuelo. |
| `PATCH` | `/api/vuelos/{id}/estado` | Cambia el estado de un vuelo. |
| `GET` | `/api/reservas` | Lista todas las reservas del sistema. |
| `GET` | `/api/pasajeros/vuelo/{vueloId}` | Muestra pasajeros asociados a un vuelo. |
| `POST` | `/api/aeronaves` | (Admin) Registra una nueva aeronave. |
| `POST` | `/api/usuarios/empleado` | (Admin) Crea un usuario con rol empleado. |
| `POST` | `/api/reportes/vuelos-mas-reservados` | (Admin) Genera reporte de vuelos más reservados. |

## Parte 1: Análisis y Diseño
### 1.1 Análisis de Requerimientos

- Casos de uso principales: búsqueda de vuelos, creación/cancelación de reservas, gestión de usuarios, generación de reportes.
- Restricciones: reglas de negocio sobre estados de vuelos, límites de cancelación y privilegios por rol.

### 1.2 Diseño de Base de Datos
- **Diagrama ER**: entidades `Usuario`, `Vuelo`, `Reserva`, `Pasajero` y `Aeronave` con relaciones 1‑N y N‑1 según corresponda. (Explicado en documento)
- **Modelo relacional**: tablas con claves primarias autoincrementales y claves foráneas para mantener integridad. (Explicado en documento)
- **Diccionario de datos**: cada campo se define con su tipo (por ejemplo, `precio` decimal, `fechaSalida` `Instant`) y restricciones (`NOT NULL`, `CHECK`). (Explicado en documento)

### 1.3 Diseño de Arquitectura
- **Capas**: Controllers → Services → Repositories → Entities/DTOs.
- **Diagrama de clases**: define atributos y métodos clave para cada entidad y servicio.
- **Patrones aplicados**: patrón Repositorio para acceso a datos, DTOs para separación de capas y manejo de autenticación JWT mediante filtros.

### 1.4 Diseño de APIs
- Documentación de endpoints REST mediante Swagger/OpenAPI (`/swagger-ui.html`).
- Modelos de request/response implementados con DTOs en el paquete `dto`.
- Códigos de estado: `200`, `201`, `204` y mensajes de error apropiados.

## Parte 2: Implementación
### 2.1 Configuración del Proyecto
- Proyecto Spring Boot con dependencias de JPA, Security, Validation y Swagger.
- Base de datos H2 para desarrollo y posibilidad de MySQL para producción.
- Perfiles configurados en `application.properties` y `application-prod.properties`.

### 2.2 Implementación de Entidades
- Entidades JPA en `entities` con anotaciones como `@Entity`, `@ManyToOne`, `@OneToMany`.
- Relaciones entre `Reserva` ↔ `Vuelo` y `Reserva` ↔ `Pasajero`.
- Validaciones con Bean Validation (`@NotBlank`, `@DecimalMin`, etc.).

### 2.3 Capa de Acceso a Datos
- Repositorios Spring Data JPA en `repositories`.
- Consultas personalizadas usando anotaciones `@Query` cuando es necesario.
- Transacciones gestionadas automáticamente por Spring (`@Transactional`).

### 2.4 Capa de Servicios
- Lógica de negocio en `services`, aplicando reglas como verificación de asientos y estados.
- Manejo de excepciones personalizadas y notificaciones por correo.

### 2.5 Capa de Controladores
- Controladores REST en `controllers` que exponen endpoints y validan roles mediante `@PreAuthorize`.
- Manejo global de errores en `GlobalExceptionHandler`.
- Documentación automática de APIs con OpenAPI.

## Criterios de Evaluación
- **Análisis y Diseño**: completitud, corrección técnica, claridad e innovación.
- **Implementación**: cumplimiento funcional, calidad del código y adherencia a la arquitectura.


## Bonus Implementados
- Frontend básico con HTML, JavaScript y CSS en `src/main/resources/static`.
- Notificaciones por correo utilizando `EmailService`.
- Métricas y logging básicos configurados en `logback-spring.xml`.
- Dockerfile para ejecutar la aplicación en contenedores.

---

Para más detalles sobre las rutas de API y ejemplos de requests, consulte `postman_collection.json` incluido en el repositorio.


## Tecnologías Utilizadas

- **Backend**: Spring Boot 3.5.3, Kotlin 1.9.25
- **Base de Datos**: H2 (desarrollo), MySQL (producción)
- **Seguridad**: Spring Security, JWT
- **Documentación**: OpenAPI/Swagger

## Requisitos del Sistema

- Java 17+
- Gradle 8.0+
- MySQL 8.0+ (para producción)

## Instalación y Configuración

1. **Clonar el repositorio**
```bash
git clone https://github.com/tu-usuario/aerotech-flight-management.git
cd aerotech-flight-management
```

2. **Configurar base de datos**
    - Para desarrollo: H2 (configurado por defecto)
    - Para producción: Modificar `application.properties`

3. **Ejecutar la aplicación**
```bash
./gradlew bootRun
```

3.1 **Ejecutar la aplicación con frontend en docker (Terminal)**
```terminal
# Construir la imagen
docker build -t aerotech .
```

```verificar que la imagen existe
# verificar que la imagen existe
docker images
```

```Ejecutar el contenedor
# Ejecutar el contenedor
docker run -d --name aerotech-container -p 8080:8080 aerotech
```

```Comprobar que esta corriendo
# Comprobar que esta corriendo
docker ps
```

```prueba del servicio
# Prueba del servicio
curl http://localhost:8080/api/health
```

```Acceder al Frontend
# Acceder al Frontend
http://localhost:8080/
```


4. **Acceder a la documentación**
    - Swagger UI: http://localhost:8080/swagger-ui.html
    - API Docs: http://localhost:8080/api-docs

## Usuarios por Defecto

Al iniciar la aplicación se crean los siguientes usuarios:

- **Administrador**: admin@aerotech.com / admin123
- **Empleado**: empleado@aerotech.com / empleado123
- **Cliente**: cliente@aerotech.com / cliente123


## Configuración de Producción

1. Configurar MySQL en `application.properties`
2. Establecer variables de entorno para JWT secret
3. Configurar CORS para dominios específicos
4. Habilitar HTTPS


## Soporte

Para soporte técnico, contactar a: soporte@aerotech.com // spenaloza@unibarranquilla.edu.co // 

