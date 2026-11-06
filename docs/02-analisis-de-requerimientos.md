# Análisis inicial de requerimientos

## 1. Identificación del proyecto

**Nombre:** Support Ticket System  
**Tipo:** Mesa de ayuda interna de TI  
**Versión de alcance:** Borrador inicial  
**Estado:** Requerimientos por validar con el profesor y ajustar durante el curso

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

### Fuera del primer MVP

- Interfaz web o aplicación móvil.
- Inicio de sesión, SSO y gestión avanzada de permisos.
- Adjuntos e imágenes.
- Chat en vivo.
- Integración con un servicio externo útil para el soporte, pendiente de definir y planificada para una etapa posterior. Si requiere credenciales o tokens, se manejarán de forma segura; esto no habilita por sí mismo login para la API propia.
- Notificaciones y reportes por correo (planificados para etapas posteriores; se incluye el aviso al administrador cuando un ticket alcance su estado final).
- Importación de auditoría desde archivos depositados por otras aplicaciones y consolidación central en Oracle (etapa posterior).
- SLA configurable por cliente o múltiples organizaciones.
- Microservicios y despliegue en AWS.

Se podrán agregar después si los temas del curso y el tiempo disponible lo permiten.

## 4. Actores

| Actor | Responsabilidad |
|---|---|
| Solicitante | Registra un incidente, consulta su evolución y aporta información solicitada |
| Agente de soporte | Clasifica, prioriza, asigna, comenta y resuelve tickets |
| Administrador | Mantiene categorías y datos de referencia del área de soporte |

En el primer corte estos roles describen responsabilidades del sistema, pero no implican que ya exista autenticación. La forma de identificar a la persona que ejecuta cada operación es una decisión pendiente. La autenticación de la aplicación ante un servicio externo es un asunto distinto y se definirá al seleccionar la integración.

## 5. Flujo principal

1. El solicitante registra un ticket con título, descripción, categoría y datos de contacto.
2. El sistema valida la solicitud, genera un identificador único y crea el ticket en estado `OPEN`.
3. El agente revisa el ticket, ajusta su prioridad y lo asigna a un responsable.
4. El responsable cambia el estado y agrega comentarios de seguimiento.
5. Si necesita información del solicitante, pasa a `WAITING_FOR_REQUESTER`; al recibirla, puede continuar el trabajo.
6. Al completar la atención, marca el ticket `RESOLVED` y registra una nota de resolución.
7. El ticket puede pasar a `CLOSED`; los datos e historial quedan consultables.
8. El sistema permite consultar los tickets y obtener indicadores básicos.
9. Cuando el ticket transiciona al estado final acordado, el sistema crea un aviso pendiente y lo envía de forma asíncrona al administrador. Un fallo del correo no revierte el cambio del ticket.
10. Los eventos de auditoría se consolidan en Oracle; el sistema puede importar archivos que otras aplicaciones depositen en una ruta configurada y producir reportes periódicos por correo.

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
| RF-12 | Notificar por correo al administrador cuando un ticket transicione a su estado final; configurar el destinatario fuera del código | Should; etapa de integraciones |
| RF-13 | Eliminar lógicamente un ticket, excluirlo de consultas normales y conservar su registro e historial | Must |
| RF-14 | Registrar eventos de auditoría en un almacén central separado del historial funcional del ticket | Should; etapa de múltiples bases |
| RF-15 | Importar eventos de auditoría de otras aplicaciones desde archivos CSV o JSON Lines en una ruta configurada | Could; etapa batch |
| RF-16 | Consultar la auditoría central con filtros y paginación, con acceso restringido | Could; tras definir autenticación/autorización |
| RF-17 | Generar reportes periódicos agregados y enviarlos por correo a destinatarios configurados | Could; etapa batch |

### Transiciones iniciales propuestas

- `OPEN` → `IN_PROGRESS` o `CLOSED` si se descarta por duplicado/no aplicable.
- `IN_PROGRESS` → `WAITING_FOR_REQUESTER`, `RESOLVED` o `OPEN` por reasignación/revisión.
- `WAITING_FOR_REQUESTER` → `IN_PROGRESS` o `CLOSED` según la respuesta y la política acordada.
- `RESOLVED` → `CLOSED` o `IN_PROGRESS` si se reabre.
- `CLOSED` es terminal para el primer MVP; la reapertura puede definirse como nueva versión del alcance.

Estas transiciones son una propuesta de negocio que deberá confirmarse antes de implementar el servicio de estados.

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
- **Privacidad y seguridad:** usar datos ficticios durante desarrollo, no registrar secretos o datos personales innecesarios y mantener credenciales/tokens de integraciones fuera del repositorio y de los logs. La autenticación/autorización de usuarios en la API propia queda fuera del primer MVP.
- **Rendimiento básico:** listados paginados; consultas de indicadores agregadas en persistencia si el volumen lo requiere.
- **Despliegue:** el sistema deberá poder empaquetarse y arrancarse de forma reproducible al llegar a la etapa de despliegue.

## 8. Modelo relacional inicial

El siguiente es el modelo lógico operativo del dominio de soporte, desarrollado por etapas. En la etapa 04 se implementaron las entidades principales, sus relaciones, el número y las fechas del ticket, la paginación y el borrado lógico. Los campos de resolución, autoría y visibilidad se completarán al validar el flujo de negocio. MySQL conservará este modelo; Oracle tendrá un modelo separado de eventos de auditoría central, definido en la etapa 06.

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
- Priority y status se restringen a valores acordados por el negocio; el catálogo exacto se definirá antes de implementar sus reglas.
- `resolution_note` se incorporará en la etapa 06 y se exigirá al resolver o cerrar el ticket según la política que se confirme.
- TicketComment ya almacena ticket, contenido y fecha. `author_type`, `author_id` y `visibility` se incorporarán en la etapa 06, cuando se defina la identidad y visibilidad de comentarios.
- TicketStatusHistory ya conserva ticket, estado anterior/nuevo y fecha. `reason`, `actor_type` y `actor_id` se incorporarán en la etapa 06, cuando se definan las reglas de transición y la identidad de quien ejecuta cada cambio.
- Los identificadores de autor y actor serán referencias polimórficas; su validación corresponderá al servicio mientras no exista una entidad común de identidad.

### Persistencia y distribución entre bases

Durante la etapa 04 se usa una sola base para mantener claves foráneas e integridad referencial dentro del dominio. En la arquitectura objetivo, MySQL conserva las entidades operativas completas: Requester, SupportAgent, Category, Ticket, TicketComment y TicketStatusHistory. Oracle recibe eventos de auditoría de este sistema y de otras aplicaciones. La auditoría originada localmente se escribe primero en una bandeja de salida de MySQL y se consolida en Oracle mediante un proceso reintentable e idempotente; los archivos externos se procesan desde una ruta configurada. No habrá relaciones JPA ni claves foráneas entre MySQL y Oracle.

El evento central puede identificar el sistema origen, entidad/registro, acción, fecha UTC, actor si se conoce, IP confiable cuando esté disponible, correlación y resultado. No almacenar secretos ni detalles personales innecesarios. Los archivos importados deberán tener contrato/versionado, codificación acordada, identificador único por evento y política de archivado y rechazo.

Supplier no forma parte del modelo inicial. Se reconsiderará si se aprueba un caso de escalamiento de tickets a proveedores externos. El TicketEntity actual guarda nombre y correo del solicitante directamente; al implementar Requester se deberá decidir cómo migrar esos campos y ajustar TicketRequestDto y sus mappers.

## 9. Reglas de negocio iniciales

- El título, la descripción, la categoría y un contacto válido son obligatorios para crear el ticket.
- Regla objetivo para la etapa 06: el sistema asignará el estado inicial `OPEN` y el cliente no podrá elegirlo libremente. Actualmente, el contrato de creación aún recibe `status` en la solicitud; se alineará al implementar las reglas del flujo.
- Un ticket asignado debe tener un agente activo.
- No se puede cerrar un ticket sin registrar motivo o nota de resolución, según la transición aprobada.
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

## 11. Supuestos y preguntas abiertas

- La primera versión es backend REST, sin frontend.
- El uso simultáneo de MySQL y Oracle se planifica como etapa del curso; confirmar disponibilidad de ambas bases. La distribución objetivo es MySQL para el dominio operativo y Oracle para auditoría centralizada.
- Acordar el formato de intercambio de archivos, productores externos, estructura y permisos de la carpeta, frecuencia de carga y retención de archivos procesados/rechazados.
- Definir la identidad del actor y el tratamiento de IP. Mientras no exista autenticación, no atribuir acciones a requesterId o supportAgentId por inferencia; usar actor de sistema o desconocido.
- Definir cuál estado dispara el correo (`RESOLVED`, `CLOSED` o ambos), la dirección del administrador y el entorno SMTP.
- Los reportes batch serán agregados por defecto; definir periodicidad, destinatarios, formato y retención antes de implementarlos.
- Confirmar las versiones de Java y Spring Boot para el entorno de clase y evaluación, por la diferencia entre el temario y la configuración técnica usada en clase.
- Confirmar si la evaluación exige Docker, despliegue cloud, correo, Quartz o Swagger/OpenAPI.
- Definir si comentarios del solicitante y notas internas del agente tienen visibilidad distinta.
- Aprobar las transiciones de estado, condiciones de cierre y reapertura, y si un cambio de estado exige comentario.
- Definir qué servicio externo se consumirá. Candidato: catálogo de activos de TI para complementar tickets de hardware; especificar autenticación ante el proveedor, timeout, tratamiento de errores HTTP/de red y comportamiento ante indisponibilidad. Mantener separada esta decisión de habilitar autenticación/autorización en la API propia.
- Definir si el alcance requiere SLA y cómo medir el tiempo de atención.
- Supplier solo se agrega como entidad si se implementa el escalamiento de tickets a proveedores externos.
