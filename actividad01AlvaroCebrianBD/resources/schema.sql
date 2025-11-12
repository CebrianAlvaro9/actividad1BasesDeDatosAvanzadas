DROP TABLE IF EXISTS precio;
DROP TABLE IF EXISTS estacion_servicio;
DROP TABLE IF EXISTS empresa;

/*
 * Tabla 'empresa': Almacena el Rótulo (nombre), que es compartido.
 * Es correcto mantenerla.
 */
CREATE TABLE IF NOT EXISTS empresa (
    id_empresa INT AUTO_INCREMENT PRIMARY KEY,
    rotulo VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


/*
 * Tabla 'estacion_servicio': Almacena SOLO los campos comunes.
 * Los campos 'margen', 'toma_datos' y 'tipo_servicio' se han eliminado.
 */
CREATE TABLE IF NOT EXISTS estacion_servicio (
    id_estacion INT AUTO_INCREMENT PRIMARY KEY,
    id_empresa INT NOT NULL,
    
    -- Este campo es METADATA, te recomiendo mantenerlo
    -- para saber de qué CSV vino (terrestre o marítimo).
    tipo_estacion ENUM('TERRESTRE', 'MARITIMA') DEFAULT 'TERRESTRE', 
    
    -- Campos Comunes
    provincia VARCHAR(100),
    municipio VARCHAR(100),
    localidad VARCHAR(100),
    codigo_postal VARCHAR(10),
    direccion VARCHAR(255),
    longitud DECIMAL(10,6),
    latitud DECIMAL(10,6),
    tipo_venta VARCHAR(50),
    rem VARCHAR(100),
    horario TEXT,
    
    FOREIGN KEY (id_empresa) REFERENCES empresa(id_empresa) ON DELETE CASCADE,
    UNIQUE KEY unique_estacion_empresa_latlong (id_empresa, latitud, longitud)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


/*
 * Tabla 'precio': Este diseño ya es flexible y funciona perfecto.
 * Almacena CUALQUIER tipo de combustible (sea terrestre o marítimo)
 * asociado a una estación. Es el modelo correcto.
 */
CREATE TABLE IF NOT EXISTS precio (
    id_precio INT AUTO_INCREMENT PRIMARY KEY,
    id_estacion INT NOT NULL,
    tipo_combustible VARCHAR(100) NOT NULL,
    precio DECIMAL(7,3) NOT NULL,
    FOREIGN KEY (id_estacion) REFERENCES estacion_servicio(id_estacion) ON DELETE CASCADE,
    UNIQUE KEY unique_precio (id_estacion, tipo_combustible)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;