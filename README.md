# 💅 Nails Beauty

Aplicación web para la gestión integral de un salón de manicure/pedicure. Permite administrar usuarios, reservas, servicios, facturas y contactos, ofreciendo una organización eficiente del negocio con un panel administrativo completo.

---

## 👀 Vista Previa

<img width="1903" height="957" alt="image" src="https://github.com/user-attachments/assets/35ecc67f-d008-4dcf-93bc-a6d7c1357f5c" />
<img width="1912" height="958" alt="image" src="https://github.com/user-attachments/assets/f2eb1821-4ed2-4db2-8beb-611bbd2b99f0" />

---

## ✨ Características

- **Registro e inicio de sesión** de usuarios con roles (ADMIN / CLIENTE)
- **Seguridad con Spring Security 6** (CSRF, sesiones, BCrypt, autorización por roles)
- **Gestión de clientes** (crear, editar, buscar, exportar CSV)
- **Administración de servicios** del salón (CRUD completo)
- **Gestión de reservas** con selección de fecha, horario y servicio
- **Sistema de facturación** con generación de PDF, envío por correo y control de estados
- **Calendario de reservas** con vista interactiva (FullCalendar)
- **Mensajes de contacto** de clientes
- **Persistencia con MySQL** y Spring Data JPA
- **Panel administrativo** con dashboard y estadísticas

---

## 🧩 Tecnologías

| Tecnología | Versión | Uso |
|-----------|---------|-----|
| Java | 17 | Lenguaje de programación |
| Spring Boot | 3.5.6 | Framework principal |
| Spring Security | 6 | Autenticación y autorización |
| Spring Data JPA | - | Persistencia con Hibernate |
| Thymeleaf | - | Motor de plantillas server-side |
| MySQL | 8 | Base de datos relacional |
| Maven | - | Gestión de dependencias |
| Lombok | - | Reducción de código boilerplate |
| iText PDF | 8.0.3 | Generación de facturas en PDF |
| Bootstrap | 5.3 | Framework CSS del frontend |
| FullCalendar | - | Calendario interactivo |

---

## ⚙️ Requisitos Previos

- **Java 17** o superior
- **MySQL 8** o superior
- **Maven** (incluido en el proyecto con `mvnw`)
- **IDE** (IntelliJ IDEA, Eclipse o VS Code)

---

## 🚀 Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/pastorjuan106/NailsBeauty.git
cd NailsBeauty
```

### 2. Crear la base de datos

```sql
CREATE DATABASE nailsbeautydb;
```

### 3. Configurar la conexión

Editar `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/nailsbeautydb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD
```

### 4. Ejecutar el proyecto

```bash
mvn spring-boot:run
```

El servidor se iniciará en: **http://localhost:8080**

### 5. Crear el primer usuario ADMIN

Registrar un usuario desde `/register`, luego actualizar su rol a `ADMIN` desde la base de datos:

```sql
UPDATE usuario SET rol = 'ADMIN' WHERE correo = 'tu_correo@email.com';
```

---

## 🗄️ Modelo de Datos

```
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│   usuario    │       │   reserva    │       │   factura    │
├─────────────┤       ├─────────────┤       ├─────────────┤
│ id_usuario   │◄──┐   │ id_reserva  │◄──┐   │ id_factura  │
│ nombre       │   │   │ fecha       │   │   │ numero      │
│ apellido_pat │   │   │ observacion │   │   │ reserva_id  │──┘
│ apellido_mat │   │   │ estado      │   │   │ cliente_nom │
│ correo       │   └───│ usuario_id  │   │   │ cliente_cor │
│ clave        │       │ servicio_id │───┘   │ servicio    │
│ rol          │       │ horario_id  │       │ precio      │
│ estado       │       └─────────────┘       │ descuento   │
│ fecha_registro│                             │ igv         │
└─────────────┘                             │ total       │
                                            │ metodo_pago │
┌─────────────┐       ┌─────────────┐       │ estado      │
│   servicio   │       │   horario    │       └─────────────┘
├─────────────┤       ├─────────────┤
│ id_servicio  │       │ id_horario  │
│ nombre       │       │ hora_inicio │
│ descripcion  │       │ hora_fin    │
│ precio       │       │ estado      │
│ estado       │       └─────────────┘
└─────────────┘

┌─────────────┐
│   contacto   │
├─────────────┤
│ id_contacto  │
│ nombre       │
│ correo       │
│ mensaje      │
│ fecha_envio  │
└─────────────┘
```

**Enums:**
| Enum | Valores |
|------|---------|
| `RolUsuario` | `CLIENTE`, `ADMIN` |
| `EstadoUsuario` | `ACTIVO`, `INACTIVO` |
| `EstadoReserva` | `PENDIENTE`, `ATENDIDA`, `CANCELADA` |
| `EstadoFactura` | `PENDIENTE_PAGO`, `PAGADA`, `ANULADA` |
| `MetodoPago` | `EFECTIVO`, `YAPE`, `PLIN`, `TARJETA` |

---

## 🔐 Seguridad

### Autenticación con Spring Security

| Concepto | Descripción |
|----------|-------------|
| **Login** | `POST /login` con campos `correo` y `clave` |
| **Logout** | `POST /logout` invalida sesión y elimina cookie `JSESSIONID` |
| **Password** | Encriptado con `BCryptPasswordEncoder` |
| **Sesiones** | Timeout de 1 hora, máximo 1 sesión activa por usuario |
| **CSRF** | Deshabilitado (proyecto monolito sin API externa) |

### Roles y Permisos

| Rol | Acceso |
|-----|--------|
| **ADMIN** | Panel admin completo: dashboard, usuarios, servicios, reservas, facturas, contactos, agenda |
| **CLIENTE** | Página principal, perfil propio, crear/cancelar reservas, formulario de contacto |

### Rutas Protegidas

```
Públicas:     /, /index, /login, /register, /css/**, /js/**, /img/**
Admin:        /admin/** → requiere ROLE_ADMIN
Autenticado:  /perfil, /reservas/** → requiere cualquier rol
```

---

## 📌 Estructura del Proyecto

```
src/main/java/pe/nailsbeauty/
├── NailsBeautyApplication.java          # Clase principal
├── config/
│   ├── SecurityConfig.java              # Configuración Spring Security
│   ├── CustomUserDetailsService.java    # Puente BD → Security
│   ├── CustomAuthenticationSuccessHandler.java  # Redirección por rol
│   ├── GlobalControllerAdvice.java      # Inyecta usuarioLogeado en vistas
│   └── WebConfig.java                   # Configuración Thymeleaf y recursos
├── controller/
│   ├── IndexController.java             # Página principal
│   ├── UsuarioController.java           # Login, registro, perfil
│   ├── ReservaController.java           # CRUD reservas cliente
│   ├── ContactoController.java          # Formulario de contacto
│   ├── AdminController.java             # Dashboard admin
│   ├── AdminUsuarioController.java      # Gestión de usuarios
│   ├── AdminServicioController.java     # Gestión de servicios
│   ├── AdminReservaController.java      # Gestión de reservas
│   ├── AdminFacturaController.java      # Gestión de facturas
│   └── AgendaController.java            # Calendario FullCalendar
├── entity/                              # Entidades JPA
├── repository/                          # Repositorios Spring Data
├── service/                             # Interfaces de servicio
│   └── impl/                            # Implementaciones
└── dto/                                 # (eliminado - no se usa)

src/main/resources/
├── application.properties               # Configuración principal
├── static/
│   ├── css/                             # Estilos CSS
│   ├── js/                              # JavaScript (calendario, carrusel, reservas)
│   └── img/                             # Imágenes
└── templates/                           # Plantillas Thymeleaf
    ├── login.html, register.html        # Autenticación
    ├── index.html                       # Página principal
    ├── perfil.html                      # Perfil de usuario
    ├── formContacto.html                # Formulario contacto
    ├── admin/                           # Panel administrativo
    │   ├── dashboard.html
    │   ├── usuarios/
    │   ├── servicios/
    │   ├── reservas/
    │   ├── facturas/
    │   └── contactos/
    └── reservas/                        # Formularios de reserva
```

---

## 🔧 Configuración de Correo

El envío de facturas por correo está configurado con Gmail SMTP. Configurar en `application.properties`:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=TU_CORREO@gmail.com
spring.mail.password=TU_APP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

> **Nota:** Se requiere una App Password de Google (no la contraseña normal de la cuenta).

---

## 📌 Estado del Proyecto

| Estado | Descripción |
|--------|-------------|
| ✅ Funcional | Proyecto funcional en entorno local |
| 🔐 Seguridad | Spring Security con BCrypt, roles, sesiones |
| 🛠️ En desarrollo | Sin deploy en producción |

### Mejoras Futuras

- Deploy en la nube (AWS, Railway, Render)
- Sistema de notificaciones por SMS
- Interfaz frontend con React/Angular (SPA)
- API REST con JWT para integración móvil
- Tests unitarios y de integración

---

## 👨‍💻 Autor

Desarrollado por **Juan Pastor**
