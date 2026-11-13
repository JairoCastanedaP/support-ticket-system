# Análisis inicial de requerimientos

## 1. Identificación del proyecto

**Nombre:** Support Ticket System  
**Tipo:** Mesa de ayuda interna de TI  
**Versión de alcance:** Entrega final  
**Estado:** Alcance aprobado. Las decisiones de diseño están en la sección 12.

## 2. Problema y objetivo

Los usuarios de una organización necesitan reportar incidentes o solicitudes de soporte y conocer su estado. El equipo de TI necesita clasificar, priorizar, asignar y resolver esos tickets conservando el historial del trabajo realizado.

El objetivo es construir una aplicación backend que gestione ese proceso de forma trazable. El proyecto también funcionará como caso de estudio para aplicar progresivamente las capas de presentación, negocio y persistencia en Java/Spring.

## 3. Alcance del primer producto

### Incluido en el núcleo

- Crear y consultar tickets de soporte.
- Retirar tickets mediante eliminación lógica, conservando sus comentarios e historial.
- Clasificar tickets por categoría y prioridad.
- Asignar y reasignar un responsable de soporte.
- Registrar comentarios y cambios de estado.
- Consultar el historial de un ticket.
- Listar tickets con filtros, ordenamiento y paginación.
- Consultar indicadores básicos por estado y categoría.
- Probar todas las operaciones mediante API REST y Postman.

### Incluido en la entrega final

- Cambio de estado mediante transiciones controladas, con nota de resolución o motivo cuando se requiera.
- Número de ticket legible (`TCK-2026-000123`) y fecha límite de atención (SLA) calculada según la prioridad.
- Indicadores mediante procedimiento almacenado y listado resumido mediante vista de base de datos.
- Consulta de activos de TI en un servicio externo protegido con token (login + Bearer).
- Consulta de datos del solicitante en un directorio de empleados interno (sin seguridad).
- Portal para clientes externos: consulta de un ticket por su número, protegida con el header `X-API-Key`.
- Notificación por correo al administrador cuando un ticket pasa a `RESOLVED`, enviada por un job de Quartz.
- Volcado masivo de tickets a demanda (Spring Batch) hacia una base histórica en PostgreSQL.
- Auditoría central en Oracle mediante bandeja de salida en MySQL (implementada en la etapa 06).
- Documentación con Swagger, REST Docs (endpoints de Ticket) y colección de Postman por fase.

## 4. Actores

| Actor | Responsabilidad |
|---|---|
| Solicitante | Registra un incidente, consulta su evolución y aporta información solicitada |
| Agente de soporte | Clasifica, prioriza, asigna, comenta y resuelve tickets |
| Administrador | Mantiene categorías y datos de referencia del área de soporte; recibe el correo de tickets resueltos y lanza el volcado histórico |
| Cliente externo | Sistema o persona ajena a la organización que consulta el estado de un ticket mediante el portal con API key |

Estos roles describen responsabilidades del sistema, pero no implican que exista autenticación de usuarios. El actor de cada operación se toma del header opcional `X-User-Id`; si no se envía, se registra `SYSTEM`. La autenticación de la aplicación ante el servicio externo de activos (token Bearer) y la API key del portal son mecanismos distintos de un login de usuarios.

## 5. Flujo principal

1. El solicitante registra un ticket con título, descripción, categoría y datos de contacto.
2. El sistema valida la solicitud, genera un identificador único y crea el ticket en estado `OPEN`.
3. El agente revisa el ticket, ajusta su prioridad y lo asigna a un responsable.
4. El responsable cambia el estado y agrega comentarios de seguimiento.
5. Si necesita información del solicitante, pasa a `WAITING_FOR_REQUESTER`; al recibirla, puede continuar el trabajo.
6. Al completar la atención, marca el ticket `RESOLVED` y registra una nota de resolución.
7. El ticket puede pasar a `CLOSED`; los datos e historial quedan consultables.
8. El sistema permite consultar los tickets y obtener indicadores básicos.
9. Cuando el ticket pasa a `RESOLVED`, el sistema crea una notificación pendiente y un job de Quartz la envía por correo al administrador. Un fallo del correo no revierte el cambio del ticket.
10. Los eventos de auditoría se consolidan en Oracle mediante la bandeja de salida de MySQL.
11. Un cliente externo consulta el estado de su ticket por número en el portal, enviando su API key.
12. El administrador lanza a demanda el volcado de tickets hacia la base histórica en PostgreSQL y consulta el resultado de la ejecución.

## 6. Requerimientos funcionales

| ID | Requerimiento | Prioridad inicial |
|---|---|---|
| RF-01 | Registrar un ticket con título, descripción, categoría y datos mínimos del solicitante | Must |
| RF-02 | Generar un identificador legible y único para cada ticket | Must |
| RF-03 | Consultar un ticket por identificador, incluyendo comentarios e historial | Must |
| RF-04 | Listar tickets con filtros por estado, categoría, prioridad y responsable | Must |
| RF-05 | Paginar y ordenar resultados de listados | Must |
| RF-06 | Asignar o reasignar un ticket a un agente activo | Must |
| RF-07 | Cambiar estado de ticket respetando transiciones permitidas | Must |
| RF-08 | Agregar comentarios de seguimiento y nota de resolución | Must |
| RF-09 | Mantener categorías de soporte activas/inactivas | Should |
| RF-10 | Consultar cantidades por estado/categoría y tiempos promedio de resolución | Should |
| RF-11 | Registrar automáticamente fechas de creación y actualización | Should |
| RF-12 | Notificar por correo al administrador cuando un ticket pase a `RESOLVED`; configurar el destinatario fuera del código | Must; etapa 10 |
| RF-13 | Eliminar lógicamente un ticket, excluirlo de consultas normales y conservar su registro e historial | Must |
| RF-14 | Registrar eventos de auditoría en un almacén central separado del historial funcional del ticket | Must; etapa 06 |
| RF-15 | Calcular la fecha límite de atención (SLA) según la prioridad del ticket | Must; etapa 08 |
| RF-16 | Asociar opcionalmente un activo de TI al ticket y consultar su detalle en el servicio externo protegido con token | Must; etapa 10 |
| RF-17 | Completar los datos del solicitante con el directorio de empleados interno | Should; etapa 10 |
| RF-18 | Permitir que un cliente externo consulte un ticket por su número con una API key, recibiendo solo datos no internos | Must; etapa 10 |
| RF-19 | Ejecutar a demanda el volcado masivo de tickets hacia la base histórica (PostgreSQL) y consultar el estado de la ejecución | Must; etapa 11 |

### Transiciones de estado aprobadas

| Desde | Hacia | Condición |
|---|---|---|
| `OPEN` | `IN_PROGRESS` | Un agente toma el ticket |
| `OPEN` | `CLOSED` | Duplicado o no aplica; motivo obligatorio |
| `IN_PROGRESS` | `WAITING_FOR_REQUESTER` | El agente necesita información del solicitante |
| `IN_PROGRESS` | `RESOLVED` | Solucionado; nota de resolución obligatoria |
| `WAITING_FOR_REQUESTER` | `IN_PROGRESS` | El solicitante respondió |
| `WAITING_FOR_REQUESTER` | `CLOSED` | El solicitante no respondió; motivo obligatorio |
| `RESOLVED` | `CLOSED` | El solicitante confirma la solución |
| `RESOLVED` | `IN_PROGRESS` | La solución no funcionó; se reabre |
| `CLOSED` | — | Estado terminal; si el problema reaparece se crea un ticket nuevo |

- El ticket se crea siempre en `OPEN`; el cliente no envía el estado.
- El estado se cambia solo con `PATCH /api/v1/tickets/{id}/status`, indicando el estado destino y el motivo o nota. `PUT` no modifica el estado.
- Una transición no listada responde `409 Conflict` y no guarda cambios.
- Toda transición válida queda registrada en `ticket_status_history`.
- La reasignación de agente no cambia el estado: se hace sin pasar por `OPEN`.

## 7. Requerimientos no funcionales

- **Mantenibilidad:** capas identificables y módulos organizados por funcionalidad.
- **Integridad:** claves foráneas y reglas que eviten comentarios o eventos huérfanos.
- **Consistencia:** cambios relacionados dentro de una transacción.
- **Trazabilidad:** conservar quién/cuándo creó o actualizó un ticket y su historial de estados cuando los datos de identidad estén disponibles.
- **Auditoría central:** distinguir el historial funcional del ticket del registro transversal de acciones; conservar eventos en Oracle como registros append-only e importar con identificadores idempotentes.
- **Procesamiento batch:** validar formato y filas, evitar importaciones duplicadas, separar archivos recibidos, procesados y rechazados, y registrar errores recuperables.
- **Correo:** mantener destinatarios y credenciales SMTP en configuración externa; registrar estado e intentos de envío y limitar el mensaje a los datos necesarios.
- **Usabilidad para desarrollo:** endpoints documentados y ejemplos reproducibles en Postman.
- **Portabilidad:** configuración externa a los fuentes; JDK, Spring Boot y base de datos fijados en el proyecto.
- **Privacidad y seguridad:** usar datos ficticios durante desarrollo, no registrar secretos o datos personales innecesarios y mantener credenciales/tokens de integraciones fuera del repositorio y de los logs. La API no implementa login de usuarios; el portal externo usa API key.
- **Rendimiento básico:** listados paginados; consultas de indicadores agregadas en persistencia si el volumen lo requiere.
- **Despliegue:** el sistema deberá poder empaquetarse y arrancarse de forma reproducible al llegar a la etapa de despliegue.

## 8. Modelo relacional inicial

El siguiente es el modelo lógico operativo del dominio de soporte, desarrollado por etapas. En la etapa 04 se implementaron las entidades principales, sus relaciones, el número y las fechas del ticket, la paginación y el borrado lógico. La etapa 08 completa los campos de resolución, motivo y fecha límite. MySQL conserva este modelo; Oracle tiene un modelo separado de eventos de auditoría (etapa 06) y PostgreSQL el histórico de tickets (etapa 11).

~~~mermaid
erDiagram
    REQUESTER ||--o{ TICKET : registra
    CATEGORY ||--o{ TICKET : clasifica
    SUPPORT_AGENT o|--o{ TICKET : atiende
    TICKET ||--o{ TICKET_COMMENT : tiene
    TICKET ||--o{ TICKET_STATUS_HISTORY : conserva

    REQUESTER {
        bigint requester_id PK
        varchar name
        varchar email
        boolean active
    }

    SUPPORT_AGENT {
        bigint support_agent_id PK
        varchar name
        varchar email
        boolean active
    }

    CATEGORY {
        bigint category_id PK
        varchar name UK
        varchar description
        boolean active
    }

    TICKET {
        bigint ticket_id PK
        varchar ticket_number UK
        varchar title
        text description
        varchar priority
        varchar status
        datetime created_at
        datetime updated_at
        boolean is_active
        datetime deleted_at
        text resolution_note
        bigint requester_id FK
        bigint category_id FK
        bigint assigned_agent_id FK
    }

    TICKET_COMMENT {
        bigint comment_id PK
        bigint ticket_id FK
        varchar author_type
        bigint author_id
        text content
        varchar visibility
        datetime created_at
    }

    TICKET_STATUS_HISTORY {
        bigint history_id PK
        bigint ticket_id FK
        varchar previous_status
        varchar new_status
        varchar reason
        varchar actor_type
        bigint actor_id
        datetime changed_at
    }
~~~

### Cardinalidades y claves

| Relación | Cardinalidad | Regla |
|---|---|---|
| Requester–Ticket | Un solicitante registra cero o muchos tickets; cada ticket tiene un solicitante | requester_id es obligatorio en Ticket |
| Category–Ticket | Una categoría clasifica cero o muchos tickets; cada ticket tiene una categoría | category_id es obligatorio en Ticket; una categoría inactiva no se asigna a tickets nuevos |
| SupportAgent–Ticket | Un agente atiende cero o muchos tickets; un ticket puede no estar asignado | assigned_agent_id es opcional; solo se asignan agentes activos |
| Ticket–TicketComment | Un ticket tiene cero o muchos comentarios; cada comentario pertenece a un ticket | ticket_id es clave foránea |
| Ticket–TicketStatusHistory | Un ticket conserva cero o muchos cambios de estado; cada cambio pertenece a un ticket | ticket_id es clave foránea |

### Campos y restricciones de negocio

- Category: nombre obligatorio y único, descripción opcional y estado activo/inactivo.
- Ticket: número legible y único, título, descripción, prioridad, estado y fechas de creación/actualización. `is_active` y `deleted_at` registran la eliminación lógica: activo implica `is_active = true` y `deleted_at = null`; eliminado implica `is_active = false` y una fecha en `deleted_at`.
- Priority y status se restringen a los enums `TicketPriority` y `TicketStatus` (etapa 06).
- `resolution_note` se incorpora en la etapa 08 y es obligatoria al pasar a `RESOLVED`.
- TicketComment almacena ticket, contenido y fecha.
- TicketStatusHistory conserva ticket, estado anterior/nuevo y fecha; en la etapa 08 incorpora `reason` y el actor que ejecuta el cambio.
- El actor se registra como texto (`X-User-Id` o `SYSTEM`), sin relación con una entidad de identidad.

### Persistencia y distribución entre bases

MySQL conserva las entidades operativas completas: Requester, SupportAgent, Category, Ticket, TicketComment y TicketStatusHistory. Oracle recibe eventos de auditoría de este sistema y de otras aplicaciones: la auditoría originada localmente se escribe primero en una bandeja de salida de MySQL y se consolida en Oracle mediante un proceso reintentable e idempotente. PostgreSQL recibe el volcado histórico de tickets mediante Spring Batch. No hay relaciones JPA ni claves foráneas entre bases.

El evento central puede identificar el sistema origen, entidad/registro, acción, fecha UTC, actor si se conoce, IP confiable cuando esté disponible, correlación y resultado. No almacenar secretos ni detalles personales innecesarios.


## 9. Reglas de negocio iniciales

- El título, la descripción, la categoría y un contacto válido son obligatorios para crear el ticket.
- El sistema asigna el estado inicial `OPEN`; el contrato de creación deja de recibir `status` (etapa 08).
- Estado y prioridad se restringen a los enums `TicketStatus` (`OPEN`, `IN_PROGRESS`, `WAITING_FOR_REQUESTER`, `RESOLVED`, `CLOSED`) y `TicketPriority` (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`); un valor distinto responde `400 Bad Request`.
- Un ticket asignado debe tener un agente activo.
- Pasar a `RESOLVED` exige nota de resolución; cerrar desde `OPEN` o `WAITING_FOR_REQUESTER` exige motivo.
- La fecha límite (SLA) se calcula al crear el ticket según la prioridad y se recalcula si la prioridad cambia.
- Todo cambio de estado debe generar un registro de historial.
- Un comentario debe pertenecer a un ticket existente y tener contenido no vacío.
- Una categoría inactiva no se puede asignar a tickets nuevos, pero no debe borrar el historial anterior.
- Las transiciones inválidas deben producir un conflicto de negocio controlado.

## 10. Criterios de aceptación del primer flujo vertical

El primer incremento funcional estará listo cuando:

1. Se pueda crear un ticket válido por REST y obtener `201 Created` con su identificador.
2. Una solicitud inválida devuelva un error de validación legible y no persista datos.
3. Se pueda consultar el ticket recién creado y verlo en estado `OPEN`.
4. El ticket pueda filtrarse y aparecer en un listado paginado.
5. El ticket pueda asignarse a un agente activo y el cambio quede registrado.
6. Una transición no permitida devuelva un conflicto controlado, sin dejar datos parcialmente actualizados.
7. Los ejemplos de uso estén guardados en una colección de Postman o documentados en el repositorio.
8. Al eliminar un ticket, la API responda `204 No Content`, lo excluya del listado normal y responda `404 Not Found` al consultarlo, modificarlo o agregarle comentarios; sus datos e historial se conservan en la base.

## 11. Supuestos

- El sistema es backend REST, sin frontend; se prueba con Postman y Swagger.
- Se usan tres bases relacionales: MySQL (operativa), Oracle (auditoría central) y PostgreSQL (histórico de tickets). No hay claves foráneas ni relaciones JPA entre bases.
- Se usan datos ficticios. Credenciales, API keys y destinatarios de correo se configuran con variables de entorno, fuera del repositorio.

## 12. Decisiones de diseño

| # | Tema | Decisión |
|---|---|---|
| A1 | Estado inicial | El sistema crea el ticket en `OPEN`; se retira `status` del DTO de creación |
| A2 | Cambio de estado | Endpoint propio `PATCH /tickets/{id}/status` con motivo; `PUT` no cambia el estado |
| A3 | Transiciones | Las de la tabla de la sección 6 |
| A4 | Notas obligatorias | Nota de resolución al pasar a `RESOLVED`; motivo al cerrar sin resolver |
| A5 | Número legible | `TCK-<año>-<consecutivo de 6 dígitos>`, por ejemplo `TCK-2026-000123` |
| A6 | Patrones de dominio | Chain of Responsibility para validar la creación (solicitante, categoría activa, agente activo); Strategy para calcular el SLA según la prioridad; transiciones declaradas en el enum `TicketStatus` |
| A7 | Actor | Header opcional `X-User-Id`; si falta, `SYSTEM` |
| B1 | Procedimiento almacenado | `usp_ticket_indicators` en MySQL: cantidades por estado y categoría y tiempo promedio de resolución (RF-10) |
| B2 | VO | Vista `vw_ticket_summary` en MySQL mapeada a una entidad de solo lectura |
| B3 | Projections | Interfaz de proyección para el listado resumido de tickets |
| B4 | Esquema | Scripts SQL para procedimiento, vista y datos iniciales; las tablas siguen gestionadas por Hibernate (`update`) |
| B5 | Tercera base | PostgreSQL como histórico de tickets, alimentado por Spring Batch |
| C1 | Integración externa con token | Saliente: catálogo de activos de TI en escuelajs (login + Bearer). Entrante: portal de clientes externos protegido con `X-API-Key` |
| C2 | Integración interna sin seguridad | Directorio de empleados (jsonplaceholder `users`) para completar datos del solicitante |
| C3 | Correo | Al pasar a `RESOLVED`: notificación pendiente en la misma transacción y envío a `TICKET_ADMIN_EMAIL`; Mailpit en Docker para la demo |
| C4 | Jobs | Quartz para el envío de correos; el relay de auditoría sigue con `@Scheduled` |
| C5 | Mensajería | Notificaciones por correo desacopladas mediante tabla de pendientes y job |
| C6 | Proceso batch | Spring Batch a demanda: `POST /api/v1/batch/ticket-history` (respuesta `202` con `executionId`) y consulta de estado; lectura paginada de MySQL y escritura idempotente (upsert) en PostgreSQL |
| D1 | Respuesta estándar | `CustomResponse` en todos los endpoints |
| D2 | Controllers | `GenericController` + implementación para Category, Requester y SupportAgent (CRUD con borrado lógico); Ticket con controller propio que hereda la clase base |
| D3 | Documentación | Swagger para toda la API; REST Docs para los endpoints de Ticket |
| D4 | Anotaciones propias | `@ValidPhone` (formato colombiano) y `@ValidTicketNumber` |
| E1 | Ramas | Una rama por fase → `develop` → `main` con tag `mvp-final` |
| E2 | Despliegue | Docker Compose |
| E3 | Diagramas | Mermaid en el README |
| E4 | Pruebas | Colección de Postman por fase en `docs/`; pruebas automáticas del dominio y las que exige REST Docs |
