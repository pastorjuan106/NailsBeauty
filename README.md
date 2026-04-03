💅 NAILS BEAUTY

Aplicación web para la gestión de un salón de uñas. Permite administrar usuarios, citas y servicios, ofreciendo una organización eficiente del negocio.

👀 Vista Previa
<img width="1903" height="957" alt="image" src="https://github.com/user-attachments/assets/35ecc67f-d008-4dcf-93bc-a6d7c1357f5c" />
<img width="1912" height="958" alt="image" src="https://github.com/user-attachments/assets/f2eb1821-4ed2-4db2-8beb-611bbd2b99f0" />

✨ Características
Registro e inicio de sesión de usuarios
Gestión de clientes
Administración de servicios
Gestión de citas/reservas
Seguridad con autenticación 
Conexión a base de datos MySQL
Arquitectura basada en Spring Boot

🧩 Tecnologías
Java
Spring Boot
Spring Security
Hibernate / JPA
MySQL
Maven

⚙️ Instalación

Clona el repositorio y ejecuta el proyecto localmente:

git clone https://github.com/pastorjuan106/NailsBeauty.git
cd nailsbeauty

Abre el proyecto en tu IDE y ejecuta:

mvn spring-boot:run

El servidor se iniciará en:

http://localhost:8080

🔐 Configuración

Configura tu base de datos en el archivo:

src/main/resources/application.properties

Ejemplo:

spring.datasource.url=jdbc:mysql://localhost:3306/nailsbeautydb
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD

🗄️ Base de Datos

Asegúrate de tener creada la base de datos en MySQL:

CREATE DATABASE nailsbeautydb;

📌 Estado del proyecto

✅ Proyecto funcional en entorno local
🛠️ En desarrollo (sin deploy en producción)
🚀 Posibles mejoras futuras:
Interfaz frontend (Angular / React)
Deploy en la nube
Sistema de notificaciones
Panel administrativo más completo
👨‍💻 Autor

Desarrollado por Juan Pastor
