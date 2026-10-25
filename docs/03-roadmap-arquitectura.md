# Roadmap de entregas y evolución de arquitectura

## 1. Propósito

Este roadmap organiza el desarrollo de la mesa de ayuda en entregas demostrables, cada una asociada con una mejora arquitectónica y con las sesiones del curso. La aplicación será un solo sistema que madura; no se crearán copias del código por versión.

Las ramas `architecture/NN-*` son ramas de trabajo temporales: nacen desde `develop`, implementan una mejora concreta y se integran de vuelta. Las versiones estables se marcan con tags cuando un MVP está listo para presentar.

## 2. Flujo de ramas

- **`main`**: estado estable y presentable del proyecto.
- **`develop`**: integración de mejoras terminadas que aún pueden evolucionar.
- **`architecture/NN-*`**: trabajo enfocado en una evolución arquitectónica numerada.
- **Tags `mvp-*`**: identifican hitos estables, por ejemplo `mvp-0.1.0`.

Flujo habitual:

```text
main → develop → architecture/NN-mejora → develop → main + tag al estabilizar un MVP
```

No se requiere una rama permanente por sesión ni una rama `release` para el trabajo individual. Si una rama necesita varios cambios pequeños, se mantienen commits claros dentro de esa rama y se fusiona cuando el incremento sea demostrable.

## 3. Etapas arquitectónicas numeradas

| N.º | Rama propuesta | Sesiones | Enfoque arquitectónico | Resultado verificable |
|---|---|---|---|---|
| 01 | `architecture/01-layered-foundation` | 1–2 | Crear esqueleto Spring Boot; documentar límites de presentación, negocio y persistencia; organizar paquetes por funcionalidad | La aplicación inicia y la estructura/responsabilidades quedan documentadas |
| 02 | `architecture/02-relational-data-model` | 3 | Diseñar ERD y esquema relacional; documentar claves, restricciones e índices; preparar SQL inicial | Modelo y scripts para tickets, categorías, agentes/solicitantes, comentarios e historial |
| 03 | `architecture/03-jpa-persistence` | 4–5 | Entidades JPA, relaciones, repositorios, consultas, paginación y transacciones | Ticket y sus datos asociados se guardan y consultan en la base seleccionada |
| 04 | `architecture/04-business-services` | 6 | Interfaces de servicio, operaciones CRUD, clases utilitarias justificadas y excepciones de negocio | Alta/consulta de tickets pasa por servicios; controladores no contienen reglas ni acceso JPA |
| 05 | `architecture/05-business-rules-integrations` | 7 | Estados, asignaciones, historial, reglas de transición, cálculos y mappers; dejar correo/Quartz para cuando el flujo base esté sólido | Asignar y avanzar tickets respeta reglas y conserva historial; automatizaciones quedan como extensión separada |
| 06 | `architecture/06-rest-contracts-errors` | 8 | DTOs, validación, rutas `/api/v1`, respuestas, manejo global de errores y Swagger/OpenAPI; HATEOAS si aporta valor | API documentada con respuestas coherentes y flujo completo accesible desde Postman |
| 07 | `architecture/07-packaging-deployment` | 9 | Empaquetar JAR/WAR según la evaluación; Docker/Compose; revisar Tomcat/WildFly o AWS EBS si se exige | Otra persona puede levantar el sistema siguiendo instrucciones documentadas |
| 08 | `architecture/08-course-demo` | 10 | Preparar presentación técnica, demo y retrospectiva | Demostración reproducible, arquitectura explicada y backlog de mejoras futuras |

## 4. MVPs y valor funcional

### MVP 0 — Base del proyecto

**Incluye:** estructura inicial, decisiones de arquitectura, configuración del runtime y documentación mínima.  
**Sesiones:** 1–2.  
**Aceptación:** el proyecto compila/arranca según el JDK confirmado y existe una vista clara de capas y paquetes.

### MVP 1 — Persistencia de tickets

**Incluye:** modelo relacional, categoría, ticket, solicitante/agente de referencia, estado inicial y repositorios; crear y consultar tickets.  
**Sesiones:** 3–5.  
**Aceptación:** el ticket se persiste y se recupera con relaciones válidas; los listados ya tienen paginación.

### MVP 2 — Flujo de soporte

**Incluye:** asignación, prioridad, comentarios, transiciones de estado e historial.  
**Sesiones:** 6–7.  
**Aceptación:** un ticket puede recorrer desde `OPEN` hasta `RESOLVED`/`CLOSED`, y las transiciones inválidas se rechazan de forma controlada.

### MVP 3 — API lista para consumir

**Incluye:** contratos request/response, validaciones, códigos HTTP, documentación OpenAPI y colección Postman.  
**Sesión:** 8.  
**Aceptación:** un usuario puede ejecutar el flujo principal solo con la documentación y las solicitudes guardadas.

### MVP 4 — Empaque y despliegue

**Incluye:** artefacto ejecutable, configuración externa, Docker Compose y guía de arranque.  
**Sesión:** 9.  
**Aceptación:** ejecución repetible en un entorno limpio. AWS, WildFly o WAR se consideran si están en los criterios del profesor.

### Cierre — Presentación

**Incluye:** demo, explicación de decisiones, limitaciones conocidas y próximos pasos.  
**Sesión:** 10.

## 5. Primer flujo vertical recomendado

Implementar primero un recorrido completo y pequeño, no todas las entidades posibles:

1. Crear una categoría activa.
2. Registrar un ticket en esa categoría.
3. Consultar el detalle y el listado paginado.
4. Asignar un agente.
5. Agregar un comentario.
6. Cambiar a `IN_PROGRESS` y luego `RESOLVED`.
7. Consultar el historial del ticket.

Cada paso debe atravesar controller → service → repository, con DTOs en el contrato REST cuando se incorpore la etapa de presentación.

## 6. Criterios para fusionar una rama arquitectónica

Antes de integrar `architecture/NN-*` en `develop`, registrar:

- Qué responsabilidad o límite arquitectónico cambió.
- Qué caso de uso del sistema habilita.
- Qué archivos/piezas principales se añadieron.
- Cómo se verifica el resultado de forma reproducible.
- Qué queda deliberadamente pendiente.

Una rama debe ser pequeña, enfocada y fusionable. Si mientras se trabaja aparece una preocupación distinta, anotarla para otra rama en vez de mezclar todos los temas.

## 7. Tags de MVP

Usar etiquetas para marcar estados presentables, no carpetas duplicadas ni ramas permanentes:

- `mvp-0.1.0`: base y persistencia mínima.
- `mvp-0.2.0`: flujo de soporte y reglas.
- `mvp-0.3.0`: API documentada.
- `mvp-1.0.0`: empaque/despliegue y presentación final del curso.

Los números pueden ajustarse a la convención que el profesor solicite. Una etiqueta se crea sobre un commit estable integrado en `main`.

## 8. Decisiones pendientes de confirmar antes de implementar

1. JDK y versión de Spring Boot compatibles con el entorno del profesor.
2. Base de datos primaria y estrategia de configuración local.
3. Si el proyecto requiere autenticación/autorización.
4. Qué alcance de correo, Quartz, HATEOAS, Docker y AWS se calificará.
5. Qué forma de entrega pide la evaluación final.
