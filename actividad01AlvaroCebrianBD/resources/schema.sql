drop table if exists precio;
drop table if exists estacion_servicio;
drop table if exists empresa;

/*
 * empresa
 */
create table if not exists empresa (
    id_empresa int auto_increment primary key,
    rotulo varchar(100) not null unique
) engine=innodb default charset=utf8mb4 collate=utf8mb4_unicode_ci;

 /* estacion_servicio*/
create table if not exists estacion_servicio (
    id_estacion int auto_increment primary key,
    id_empresa int not null,
    tipo_estacion enum('terrestre', 'maritima') default 'terrestre', 
    provincia varchar(100),
    municipio varchar(100),
    localidad varchar(100),
    codigo_postal varchar(10),
    direccion varchar(255),
    longitud decimal(10,6) not null,
    latitud decimal(10,6) not null, /*evitar nulos porq son claves unicas*/
    tipo_venta varchar(50),
    rem varchar(100),
    horario text,
    margen varchar(10) null,
    
    foreign key (id_empresa) references empresa(id_empresa) on delete cascade,
    unique key unique_estacion_empresa_latlong (id_empresa, latitud, longitud)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_unicode_ci;

/*
 * 'precio'
 */
create table if not exists precio (
    id_precio int auto_increment primary key,
    id_estacion int not null,
    tipo_combustible varchar(100) not null,
    precio decimal(7,3) not null,
    foreign key (id_estacion) references estacion_servicio(id_estacion) on delete cascade,
    unique key unique_precio (id_estacion, tipo_combustible)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_unicode_ci;