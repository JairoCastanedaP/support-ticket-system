-- =====================================================================
-- Datos iniciales para la demostración: catálogos mínimos.
-- Cada INSERT se ejecuta solo si el registro no existe, así el script
-- se puede repetir en cada arranque sin duplicar datos.
-- Separador de sentencias: $$
-- =====================================================================

-- categories
INSERT INTO categories (name, description, status, created_at, created_by)
SELECT 'Redes', 'Conectividad, VPN y WiFi', TRUE, CURRENT_TIMESTAMP, 'SYSTEM' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE LOWER(name) = LOWER('Redes'))
$$

INSERT INTO categories (name, description, status, created_at, created_by)
SELECT 'Hardware', 'Equipos de cómputo, impresoras y periféricos', TRUE, CURRENT_TIMESTAMP, 'SYSTEM' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE LOWER(name) = LOWER('Hardware'))
$$

INSERT INTO categories (name, description, status, created_at, created_by)
SELECT 'Software', 'Instalación, licencias y errores de aplicaciones', TRUE, CURRENT_TIMESTAMP, 'SYSTEM' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE LOWER(name) = LOWER('Software'))
$$

-- support_agents
INSERT INTO support_agents (name, surname, email, phone_number, active, created_at, created_by)
SELECT 'Marta', 'Ruiz', 'marta.ruiz@soporte.example.com', '3101112233', TRUE, CURRENT_TIMESTAMP, 'SYSTEM' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM support_agents WHERE LOWER(email) = LOWER('marta.ruiz@soporte.example.com'))
$$

INSERT INTO support_agents (name, surname, email, phone_number, active, created_at, created_by)
SELECT 'Carlos', 'Pardo', 'carlos.pardo@soporte.example.com', '3104445566', TRUE, CURRENT_TIMESTAMP, 'SYSTEM' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM support_agents WHERE LOWER(email) = LOWER('carlos.pardo@soporte.example.com'))
$$

-- requesters
INSERT INTO requesters (name, surname, email, phone_number, active, created_at, created_by)
SELECT 'Ana', 'Pérez', 'ana.perez@empresa.example.com', '3001112233', TRUE, CURRENT_TIMESTAMP, 'SYSTEM' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM requesters WHERE LOWER(email) = LOWER('ana.perez@empresa.example.com'))
$$

INSERT INTO requesters (name, surname, email, phone_number, active, created_at, created_by)
SELECT 'Luis', 'Gómez', 'luis.gomez@empresa.example.com', '3004445566', TRUE, CURRENT_TIMESTAMP, 'SYSTEM' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM requesters WHERE LOWER(email) = LOWER('luis.gomez@empresa.example.com'))
$$
