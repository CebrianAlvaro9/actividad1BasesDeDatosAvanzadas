
/**VERIIFCAR REPETIDOS*/
/*verificar q no haya precios duplicado*/
SELECT id_estacion, tipo_combustible, COUNT(*) AS num_precios
FROM precio
GROUP BY id_estacion, tipo_combustible
HAVING COUNT(*) > 1
ORDER BY num_precios DESC;

/*verificar q no haya estaciones duplicadas*/
SELECT id_empresa,longitud, latitud, COUNT(*) AS num_estaciones
FROM estacion_servicio
GROUP BY longitud, latitud, id_empresa
HAVING COUNT(*) > 1
ORDER BY num_estaciones DESC;

/*verificar q no haya empresas duplicadas*/
SELECT rotulo, COUNT(*) AS num_empresas
FROM empresa
GROUP BY rotulo
HAVING COUNT(*) > 1
ORDER BY num_empresas DESC;

/**Consultas trabajo*/

/**Nombre de la empresa con más estaciones de servicio terrestres. *//
select e.rotulo, COUNT(*) AS estaciones
from empresa e, estacion_servicio s
where e.id_empresa = s.id_empresa
and s.tipo_estacion = 'TERRESTRE'
group by e.rotulo
order by count(*) desc
limit 1;

/**Nombre de la empresa con mas estaciones de servicio maritimas. *//
select e.rotulo, COUNT(*) AS estaciones
from empresa e, estacion_servicio s
where e.id_empresa = s.id_empresa
and s.tipo_estacion = 'MARITIMA'
group by e.rotulo
order by count(*) desc
limit 1;

/**Localización, nombre de empresa y 
margen de la estación con el precio más bajo para el combustible «Gasolina 95 E5» en la Comunidad de Madrid. */

Select e.id_empresa,p.precio ,e.rotulo, s.municipio, s.direccion, s.margen
from empresa e, estacion_servicio s, precio p
where e.id_empresa = s.id_empresa
and s.id_estacion = p.id_estacion
and UPPER(s.municipio) = UPPER('Madrid')
and p.tipo_combustible = 'Gasolina 95 E5'
order by p.precio 
limit 1;

/**Localización, nombre de empresa y margen de la estación con el precio más bajo 
    para el combustible «Gasóleo A» si resido en el centro de Albacete 
    y no quiero desplazarme más de 10 km. *//
/* Rango aproximado para 10 km desde el centro de Albacete:
   latitud entre 38.90 y 39.09
   longitud entre -1.94 y -1.78 */

select e.id_empresa, p.precio, e.rotulo, s.municipio, s.direccion, s.margen
from empresa e
join estacion_servicio s on e.id_empresa = s.id_empresa
join precio p on s.id_estacion = p.id_estacion
where upper(s.municipio) = upper('albacete')
  and p.tipo_combustible = 'gasóleo a'
  and s.latitud between 38.90 and 39.09
  and s.longitud between -1.94 and -1.78
order by p.precio
limit 1;

-- Provincia en la que se encuentre la estación de servicio marítima con el combustible «Gasolina 95 E5» más caro. 

select s.provincia
from estacion_servicio s
join precio p on s.id_estacion = p.id_estacion
where s.tipo_estacion = 'MARITIMA'
and p.tipo_combustible = 'Gasolina 95 E5'
order by p.precio desc
limit 1;
