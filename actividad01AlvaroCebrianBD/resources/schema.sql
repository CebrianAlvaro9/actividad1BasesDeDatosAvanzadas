DROP TABLE IF EXISTS precio;
DROP TABLE IF EXISTS estacion_servicio;
DROP TABLE IF EXISTS empresa;

/*
 * empresa
 */
CREATE TABLE IF NOT EXISTS empresa (
    id_empresa INT AUTO_INCREMENT PRIMARY KEY,
    rotulo VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

 /* estacion_servicio*/
CREATE TABLE IF NOT EXISTS estacion_servicio (
    id_estacion INT AUTO_INCREMENT PRIMARY KEY,
    id_empresa INT NOT NULL,
    tipo_estacion ENUM('TERRESTRE', 'MARITIMA') DEFAULT 'TERRESTRE', 
    provincia VARCHAR(100),
    municipio VARCHAR(100),
    localidad VARCHAR(100),
    codigo_postal VARCHAR(10),
    direccion VARCHAR(255),
    longitud DECIMAL(10,6) not null,
    latitud DECIMAL(10,6) not null, /*evitar nulos porq son claves unicas*/
    tipo_venta VARCHAR(50),
    rem VARCHAR(100),
    horario TEXT,
    margen VARCHAR(10) null,
    
    FOREIGN KEY (id_empresa) REFERENCES empresa(id_empresa) ON DELETE CASCADE,
    UNIQUE KEY unique_estacion_empresa_latlong (id_empresa, latitud, longitud)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*
 * 'precio'
 */
CREATE TABLE IF NOT EXISTS precio (
    id_precio INT AUTO_INCREMENT PRIMARY KEY,
    id_estacion INT NOT NULL,
    tipo_combustible VARCHAR(100) NOT NULL,
    precio DECIMAL(7,3) NOT NULL,
    FOREIGN KEY (id_estacion) REFERENCES estacion_servicio(id_estacion) ON DELETE CASCADE,
    UNIQUE KEY unique_precio (id_estacion, tipo_combustible)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;