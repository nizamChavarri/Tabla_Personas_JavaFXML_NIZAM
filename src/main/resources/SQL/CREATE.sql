CREATE DATABASE IF NOT EXISTS Personas_mariadb;
USE Personas_mariadb;

DROP TABLE IF EXISTS personas;

CREATE TABLE personas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE NOT NULL
);

INSERT INTO personas (nombre, apellido, fecha_nacimiento) VALUES
('Ashwin', 'Sharan', '2012-10-11'),
('Advik', 'Sharan', '2012-10-11'),
('Layne', 'Estes', '2011-12-16'),
('Mason', 'Boyd', '2003-04-20'),
('Babalu', 'Sharan', '1980-01-10');