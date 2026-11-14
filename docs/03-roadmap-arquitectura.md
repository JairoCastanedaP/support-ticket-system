# Roadmap de entregas y evolución de arquitectura

## 1. Propósito

Este roadmap muestra cómo evoluciona el Support Ticket System, etapa por etapa, hasta su entrega final. La aplicación crece como un monolito modular en capas: cada etapa incorpora una decisión arquitectónica y la deja visible en una rama del repositorio. El alcance aprobado está en la sección 12 de `02-analisis-de-requerimientos.md`.

## 2. Flujo de trabajo

- **Una rama por etapa:** `architecture/NN-nombre`, creada desde `develop`.
- Al cerrar una etapa: pruebas en Postman → merge a `develop`. Al final: merge a `main` y tag `mvp-final`.
- **Cada etapa tiene su carpeta de pruebas** en `docs/JavaArchitecture.postman_collection.json`, con el mismo nombre de la rama (por ejemplo `07-rest-foundation`).
- Una etapa se cierra cuando compila, la aplicación arranca con Docker Compose y su carpeta de Postman pasa completa.
- Credenciales, API keys y correos siempre en variables de entorno (`.env`), nunca en el repositorio.

## 3. Etapas completadas

| N.º | Rama | Resultado |
|---|---|---|
| 01 | architecture/01-layered-foundation | Esqueleto Spring Boot, capas y CRUD inicial de tickets |
| 02 | architecture/02-service-layer | Servicio de negocio entre controlador y repositorio |
| 03 | architecture/03-dto-validation-mapping-exceptions | DTOs, MapStruct, validación y manejo centralizado de errores |
| 04 | architecture/04-ticket-domain-model | Modelo relacional, relaciones JPA, filtros, paginación y borrado lógico |
| 05 | architecture/05-feature-generic-architecture | Paquetes por funcionalidad y servicio genérico |
| 06 | architecture/06-multiple-data-sources | MySQL + Oracle con pools separados, auditoría por bandeja de salida (outbox) con relay idempotente a Oracle, auditoría HTTP con correlation-id, Docker Compose y enums `TicketStatus`/`TicketPriority` |
| 07 | architecture/07-rest-foundation | `CustomResponse` en toda la API, controllers con interfaz e implementación, CRUD genérico con borrado lógico, auditoría JPA con `X-User-Id` y excepciones centralizadas |
| 08 | architecture/08-domain-business-rules | Ciclo de vida del ticket con transiciones y `PATCH` de estado, Strategy por estado destino, Chain of Responsibility para validar, número `TCK-año-consecutivo` y pruebas unitarias |

## 4. Etapas de la entrega final

| N.º | Rama | Componentes de arquitectura |
|---|---|---|
| 09 | architecture/09-advanced-persistence | Base de datos, persistencia avanzada |
| 10 | architecture/10-integrations | Servicios de integración, componentes especiales |
| 11 | architecture/11-batch-ticket-history | Tercera base de datos, procesamiento batch |
| 12 | architecture/12-documentation-demo | Documentación y demostración |

### 07 — Base REST

- `CustomResponse<T>` + `ResponseStatus` + constantes de mensajes en todos los endpoints.
- `APIEndPoints` con la versión `/api/v1` y las rutas como constantes.
- `GenericController` (interfaz) + `GenericControllerImpl` (clase base); controllers `XController` + `XControllerImpl`.
- `GenericService` completo (`findAll`, `findById`, `save`, `update`, `delete` lógico) para Category, Requester y SupportAgent.
- `GenericEntity` (`@MappedSuperclass`) con auditoría de creación y actualización (`@EnableJpaAuditing`, actor desde `X-User-Id` o `SYSTEM`).
- Jerarquía `GenericException` → `NotFoundException` / `DuplicateException` / `BusinessRuleException`, y un `@RestControllerAdvice` sin manejadores repetidos (400, 404, 409).
- **Pruebas Postman:** CRUD de los tres catálogos con la nueva respuesta, duplicado → 409, inexistente → 404, validación → 400.

### 08 — Dominio y reglas del ticket

- Transiciones declaradas en `TicketStatus` (`canTransitionTo`) y endpoint `PATCH /tickets/{id}/status` con motivo o nota; transición inválida → 409.
- Creación siempre en `OPEN`; se retira `status` del DTO, así que `PUT` ya no cambia el estado. Un ticket `CLOSED` no se edita (409).
- **Strategy:** una estrategia por estado destino con sus reglas y efectos: `IN_PROGRESS` exige agente asignado (409); `RESOLVED` exige nota (400) y guarda `resolutionNote` y `resolvedAt`; `CLOSED` exige motivo si no estaba resuelto (400) y guarda `closedAt`. Un resolver las indexa por estado.
- **Chain of Responsibility:** solicitante → categoría → agente; cada eslabón valida existencia (404) y estado activo (409) en la creación y la edición.
- Historial con `reason` y `changedBy`; evento de auditoría `TICKET_STATUS_CHANGED` por el outbox.
- Número legible `TCK-2026-000123`, asignado con el id después del INSERT. Los tickets anteriores conservan su número.
- Pruebas unitarias (JUnit 5 + Mockito): las 25 combinaciones de transiciones, las estrategias y la cadena.
- **Pruebas Postman:** camino feliz OPEN → IN_PROGRESS → RESOLVED → CLOSED, transición inválida → 409, RESOLVED sin nota → 400, categoría inactiva → 409, historial del ticket.

### 09 — Persistencia avanzada

- Consultas JPQL y SQL nativo en los repositorios.
- Projection para el listado resumido de tickets.
- Vista `vw_ticket_summary` mapeada como entidad de solo lectura (VO).
- Procedimiento `usp_ticket_indicators` y endpoint `GET /tickets/indicators` (RF-10).
- Scripts SQL versionados (procedimiento, vista y datos iniciales).
- **Pruebas Postman:** listado resumido, búsqueda por número, indicadores.

### 10 — Integraciones

- **Saliente con token:** cliente del catálogo de activos de TI (login → Bearer en el header); `assetId` opcional en el ticket; timeouts y errores HTTP controlados.
- **Interna sin seguridad:** directorio de empleados para completar datos del solicitante.
- **Entrante con API key:** `GET /api/v1/portal/tickets/{ticketNumber}` validado con `X-API-Key` por un interceptor; respuesta reducida.
- **Correo con Quartz:** notificación pendiente al pasar a `RESOLVED` y job que la envía a `TICKET_ADMIN_EMAIL`; Mailpit en Docker Compose.
- Anotaciones propias `@ValidPhone` y `@ValidTicketNumber`; utilitarios (`IpUtils`, `JpqlUtils`).
- **Pruebas Postman:** activo de un ticket, portal con API key válida / inválida (401) / sin header, correo visible en Mailpit tras resolver.

### 11 — Volcado histórico con Spring Batch

- PostgreSQL en Docker Compose con datasource y pool propios.
- Job por bloques (chunks): lectura paginada en MySQL → transformación a registro histórico → upsert en PostgreSQL.
- `POST /api/v1/batch/ticket-history` (parámetros opcionales `fromDate`, `toDate`) → `202` con `executionId`.
- `GET /api/v1/batch/ticket-history/{executionId}` → estado y conteos de lectura, escritura y omitidos.
- **Pruebas Postman:** lanzar el job, consultar el estado, relanzarlo sin duplicar registros.

### 12 — Documentación y demo

- Swagger (springdoc-openapi) para toda la API.
- REST Docs (`.html`) para los endpoints de Ticket.
- Colección de Postman final ordenada por etapa.
- README: propósito, arquitectura (diagrama Mermaid de capas y componentes), modelo ER, tres bases y cómo levantar el entorno con Docker Compose.
- Merge a `main` y tag `mvp-final`.

## 5. Componentes de arquitectura y evidencia

| Componente | Evidencia | Etapa |
|---|---|---|
| Base de datos | 6 tablas relacionadas en MySQL + scripts SQL | 04, 09 |
| Persistencia | JPQL, SQL nativo, Projection, vista (VO), procedimiento almacenado; MySQL + Oracle + PostgreSQL | 06, 09, 11 |
| Dominio | Transiciones en `TicketStatus`, Chain of Responsibility (validación), Strategy (reglas por estado destino) | 08 |
| Servicio genérico | `GenericService` parametrizado con interfaces funcionales; servicios especializados (SRP) | 05, 07 |
| Lógica de servicios | Validaciones, reglas del ciclo de vida, mappers, outbox hacia Oracle, servicios externos | 08, 10 |
| Integración | Activos con token, directorio interno, portal con API key, Quartz + correo, Spring Batch | 10, 11 |
| Controller | Interfaces + herencia, `APIEndPoints`, `/api/v1`, `@RestControllerAdvice`, `CustomResponse` | 07 |
| Componentes especiales | `@ValidPhone`, `@ValidTicketNumber`, utilitarios, interceptores de auditoría y API key | 06, 07, 10 |
| Documentación | Swagger, REST Docs, Postman por etapa | 12 |
| Valor agregado | Outbox idempotente, auditoría HTTP con correlation-id, Docker Compose con 3 bases + Mailpit | 06, 11, 12 |
