-- Crear base de datos
DROP DATABASE IF EXISTS nailsbeautydb;
CREATE DATABASE IF NOT EXISTS nailsbeautydb CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE nailsbeautydb;

-- =========================================================
-- 1. Tabla: Usuario
-- =========================================================
CREATE TABLE Usuario (
  id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  apellido_paterno VARCHAR(100) NOT NULL,
  apellido_materno VARCHAR(100) NOT NULL,
  correo VARCHAR(100) NOT NULL UNIQUE,  
  celular VARCHAR(15),
  clave VARCHAR(255) NOT NULL,          
  rol ENUM('CLIENTE','ADMIN') DEFAULT 'CLIENTE',
  estado ENUM('ACTIVO','INACTIVO') DEFAULT 'ACTIVO',
  fecha_registro DATETIME DEFAULT NOW()
);

-- =========================================================
-- 2. Tabla: Servicio
-- =========================================================
CREATE TABLE Servicio (
  id_servicio INT AUTO_INCREMENT PRIMARY KEY,
  nombre_servicio VARCHAR(100) NOT NULL,
  descripcion TEXT,
  duracion INT,                        
  precio DECIMAL(10,2) NOT NULL,
  imagen_url VARCHAR(255) NULL,              
  estado ENUM('DISPONIBLE','NO_DISPONIBLE') DEFAULT 'DISPONIBLE'
);

-- =========================================================
-- 3. Tabla: Reserva
-- =========================================================
CREATE TABLE Reserva (
  id_reserva INT AUTO_INCREMENT PRIMARY KEY,
  id_usuario BIGINT NOT NULL,             
  id_servicio INT NOT NULL,
  id_horario INT NOT NULL,
  fecha_reserva DATE NOT NULL,
  estado ENUM('PENDIENTE','ATENDIDA','CANCELADA') DEFAULT 'PENDIENTE',
  observacion VARCHAR(250),
  fecha_registro DATETIME DEFAULT NOW(),
  FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario),
  FOREIGN KEY (id_servicio) REFERENCES Servicio(id_servicio),
  FOREIGN KEY (id_horario) REFERENCES HorarioBase(id_horario)
);

-- =========================================================
-- 4. Tabla: HorarioBase
-- =========================================================
CREATE TABLE HorarioBase (
  id_horario INT AUTO_INCREMENT PRIMARY KEY,
  hora_inicio TIME NOT NULL,
  hora_fin TIME NOT NULL,
  estado ENUM('ACTIVO','INACTIVO') DEFAULT 'ACTIVO'
);

-- =========================================================
-- 5. Tabla: Contacto
-- =========================================================

CREATE TABLE Contacto (
  id_contacto INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  correo VARCHAR(100) NOT NULL,
  telefono VARCHAR(15) NOT NULL,
  asunto VARCHAR(150),
  mensaje TEXT NOT NULL,
  fecha_registro DATETIME DEFAULT NOW()
) ENGINE=InnoDB;
