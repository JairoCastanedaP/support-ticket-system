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
| 09 | architecture/09-advanced-persistence | JPQL, SQL nativo con Projection, vista `vw_ticket_summary` como VO de solo lectura, procedimiento `usp_ticket_indicators` y scripts SQL con datos iniciales |
| 10 | architecture/10-integrations | Catálogo de activos con token Bearer, directorio interno de empleados, portal con API key, correo con Quartz y Mailpit, validadores `@ValidPhone` y `@ValidTicketNumber` |
| 11 | architecture/11-batch-audit-history | PostgreSQL como tercera base; Spring Batch copia la auditoría de Oracle de forma incremental, con bloque parametrizado, auditoría de ejecución en las tablas de Spring Batch y log en archivo |

## 4. Etapas de la entrega final

| N.º | Rama | Componentes de arquitectura |
|---|---|---|
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

Cada técnica atiende una consulta distinta, todas de solo lectura en `TicketQueryService` y `TicketQueryController`:

| Técnica | Uso | Endpoint |
|---|---|---|
| JPQL con `join fetch` | Ticket por número legible con categoría, solicitante y agente en una sola consulta | `GET /tickets/number/{ticketNumber}` |
| SQL nativo + Projection | Búsqueda por texto en título y descripción; devuelve solo id, número, título, estado y prioridad (máximo 50) | `GET /tickets/search?text=` |
| Vista `vw_ticket_summary` (VO) | Listado resumido paginado con columnas calculadas por la base: nombres completos, cantidad de comentarios y horas abierto. Entidad `@Immutable` mapeada con `@Subselect` para que Hibernate no cree una tabla con ese nombre | `GET /tickets/summary` |
| Procedimiento `usp_ticket_indicators` | Cantidad por estado y por categoría (una fila por dimensión) y horas promedio de resolución (parámetro de salida). Declarado `READS SQL DATA` (RF-10) | `GET /tickets/indicators` |

- **Scripts SQL** en `src/main/resources/db/`: `mysql/schema.sql` (vista y procedimiento), `h2/schema.sql` (vista) y `data.sql` (3 categorías, 2 agentes y 2 solicitantes). Se ejecutan en cada arranque después de que Hibernate cree las tablas (`defer-datasource-initialization`), con separador `$$`, y se pueden repetir sin duplicar datos.
- **Perfil local (H2):** la vista, las búsquedas y los datos iniciales funcionan; los indicadores responden `501 Not Implemented` porque H2 no ejecuta procedimientos de MySQL (`app.persistence.stored-procedures-enabled`).
- Un parámetro obligatorio ausente en la URL responde `400`.
- Pruebas unitarias del servicio de consultas: armado de indicadores, perfil sin procedimientos, validación del texto y número inexistente.
- **Pruebas Postman:** búsqueda por número (también en minúsculas) e inexistente → 404, búsqueda por texto con la forma de la proyección, texto corto o ausente → 400, listado resumido filtrado con columnas calculadas, indicadores.

### 10 — Integraciones

| Integración | Seguridad | Endpoint o disparador |
|---|---|---|
| Catálogo de activos de TI (escuelajs), saliente | Login y token `Bearer` en memoria; si el token es rechazado (401) se renueva y se reintenta una vez | `assetId` opcional en el ticket; `GET /tickets/{id}/asset` |
| Directorio de empleados (jsonplaceholder), interna | Sin seguridad | `POST /requesters/from-directory/{employeeId}` |
| Portal de clientes externos, entrante | Header `X-API-Key` validado por `PortalApiKeyInterceptor` (comparación en tiempo constante) | `GET /api/v1/portal/tickets/{ticketNumber}` |
| Correo al administrador (SMTP) | — | Cambio a `RESOLVED` |

- **Clientes HTTP:** un `RestClient` por servicio, con timeouts de 3 s (conexión) y 5 s (lectura). Si el servicio no responde o falla, la API responde `503` con `CustomResponse`.
- **Activo en la cadena de validación:** nuevo eslabón `AssetValidationHandler` al final (solicitante → categoría → agente → activo). Activo inexistente → `404`; las validaciones locales van primero para no llamar al servicio externo si ya hay un error.
- **Directorio:** separa nombre y apellido (sin títulos como "Mrs.") y deja el teléfono solo con dígitos; reutiliza el servicio de solicitantes, así un correo repetido responde `409`.
- **Portal:** respuesta reducida (número, título, estado, categoría, fechas y nota de resolución), sin datos personales, agente ni auditoría. Sin header o con clave incorrecta → `401`.
- **Correo con Quartz:** al resolver, `TicketNotificationService` guarda el correo en `ticket_notifications` dentro de la misma transacción. `TicketNotificationJob` (Quartz, cada 30 s, sin ejecuciones simultáneas) envía los pendientes a `TICKET_ADMIN_EMAIL`, cada uno en su propia transacción; tras 3 intentos fallidos queda en `FAILED`. Quartz usa almacenamiento en memoria porque el estado vive en la tabla. Solo se activa en el perfil docker; en local los correos quedan pendientes.
- **Mailpit** en Docker Compose: recibe por SMTP (1025) y muestra los correos en `http://localhost:8025`.
- **Validadores propios:** `@ValidPhone` (opcional; 7 a 15 dígitos, puede iniciar con +) en solicitantes y agentes, y `@ValidTicketNumber` (`TCK-AAAA-NNNNNN`) en el portal; un formato inválido responde `400` sin consultar la base.
- **Variables de entorno nuevas** (`.env`, ver `.env.example`): `TICKET_ADMIN_EMAIL`, `PORTAL_API_KEY`, `ASSETS_API_EMAIL`, `ASSETS_API_PASSWORD`.
- Pruebas unitarias: validadores, interceptor de API key, envío de correos con reintentos, normalización del directorio y eslabón del activo.
- **Pruebas Postman:** teléfono inválido → 400, alta desde el directorio (repetida → 409, inexistente → 404), ticket con activo real y con activo inexistente → 404, detalle del activo, portal sin header / clave incorrecta → 401, número inválido → 400, inexistente → 404, consulta reducida, y correo visible en Mailpit tras resolver. La variable `portalApiKey` de la colección se completa con la clave del `.env`.

### 11 — Histórico de auditoría con Spring Batch

Los eventos de auditoría recorren las tres bases: MySQL (outbox) → Oracle (auditoría central, relay cada 5 s) → PostgreSQL (histórico, Spring Batch a demanda).

- PostgreSQL en Docker Compose con datasource y pool propios; en el perfil local, una tercera base H2 en memoria.
- **Job por bloques (chunks):** lee de `audit_event` (Oracle) los eventos posteriores al último copiado → clasifica el evento (petición HTTP o evento de negocio) y extrae del JSON sus datos principales → escribe en `audit_event_history` (PostgreSQL) sin duplicar (`ON CONFLICT DO NOTHING`; en H2, `MERGE ... KEY`).
- **Ventana de seguridad:** cada ejecución vuelve a leer los 10 minutos anteriores al último evento copiado, por si el relay entregó eventos con retraso; los que ya están en el histórico se descartan y se informan como "ya copiados".
- **Tamaño de bloque parametrizado** en la tabla `batch_job_config` de PostgreSQL; se lee en cada ejecución, así que cambia sin reiniciar la aplicación.
- **`POST /api/v1/batch/audit-history`** (síncrono): responde con el estado, los eventos leídos y escritos, el tamaño de bloque y la duración. Si ya hay una ejecución en curso responde `409`.
- **`GET /api/v1/batch/audit-history/summary`:** cantidades del histórico por tipo de evento y fecha del último evento copiado.
- **Auditoría de ejecución:** las tablas de metadatos de Spring Batch en PostgreSQL (`BATCH_JOB_EXECUTION`, `BATCH_JOB_EXECUTION_PARAMS`, `BATCH_STEP_EXECUTION`…) registran cada ejecución con quién la pidió, desde qué fecha copió, sus pasos y sus contadores.
- **Log en archivo** `logs/batch/audit-history.log`: inicio, avance por bloque, fin de cada paso y resultado, con rotación diaria. En Docker la carpeta se monta en `./logs` del proyecto.
- Pruebas unitarias: clasificación de eventos, parámetros del job (ventana, bloque por defecto, usuario) y rechazo de una segunda ejecución simultánea.
- **Pruebas Postman:** resumen antes, lanzar el job (leídos = ya copiados + escritos), generar actividad, relanzar (solo copia lo nuevo y la lectura es incremental), resumen después (total = antes + escritos, sin duplicados).

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
| Integración | Activos con token, directorio interno, portal con API key, Quartz + correo, Spring Batch (histórico de auditoría) | 10, 11 |
| Controller | Interfaces + herencia, `APIEndPoints`, `/api/v1`, `@RestControllerAdvice`, `CustomResponse` | 07 |
| Componentes especiales | `@ValidPhone`, `@ValidTicketNumber`, interceptores de auditoría y API key | 06, 07, 10 |
| Documentación | Swagger, REST Docs, Postman por etapa | 12 |
| Valor agregado | Outbox idempotente, auditoría HTTP con correlation-id, Docker Compose con 3 bases + Mailpit | 06, 11, 12 |
