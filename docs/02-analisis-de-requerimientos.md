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
- Integración con un servicio externo útil para el soporte, pendiente de definir y planificada para una etapa posterior.
- Notificaciones por correo y recordatorios automáticos.
- SLA configurable por cliente o múltiples organizaciones.
- Microservicios y despliegue en AWS.

Se podrán agregar después si los temas del curso y el tiempo disponible lo permiten.

## 4. Actores

| Actor | Responsabilidad |
|---|---|
| Solicitante | Registra un incidente, consulta su evolución y aporta información solicitada |
| Agente de soporte | Clasifica, prioriza, asigna, comenta y resuelve tickets |
| Administrador | Mantiene categorías y datos de referencia del área de soporte |

En el primer corte estos roles describen responsabilidades del sistema, pero no implican que ya exista autenticación. La forma de identificar a la persona que ejecuta cada operación es una decisión pendiente.

## 5. Flujo principal

1. El solicitante registra un ticket con título, descripción, categoría y datos de contacto.
2. El sistema valida la solicitud, genera un identificador único y crea el ticket en estado `OPEN`.
3. El agente revisa el ticket, ajusta su prioridad y lo asigna a un responsable.
4. El responsable cambia el estado y agrega comentarios de seguimiento.
5. Si necesita información del solicitante, pasa a `WAITING_FOR_REQUESTER`; al recibirla, puede continuar el trabajo.
6. Al completar la atención, marca el ticket `RESOLVED` y registra una nota de resolución.
7. El ticket puede pasar a `CLOSED`; los datos e historial quedan consultables.
8. El sistema permite consultar los tickets y obtener indicadores básicos.

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
| RF-12 | Notificar por correo o tarea programada sobre asignaciones y tickets pendientes | Could; etapa posterior |

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
- **Usabilidad para desarrollo:** endpoints documentados y ejemplos reproducibles en Postman.
- **Portabilidad:** configuración externa a los fuentes; JDK, Spring Boot y base de datos fijados en el proyecto.
- **Privacidad:** usar datos ficticios durante desarrollo y no registrar secretos o datos personales innecesarios.
- **Rendimiento básico:** listados paginados; consultas de indicadores agregadas en persistencia si el volumen lo requiere.
- **Despliegue:** el sistema deberá poder empaquetarse y arrancarse de forma reproducible al llegar a la etapa de despliegue.

## 8. Modelo relacional inicial

El siguiente es el modelo lógico propuesto para el dominio de soporte. Durante la etapa de relaciones JPA se implementará primero con una sola base de datos relacional, para validar claves foráneas y cardinalidades. La distribución entre MySQL y Oracle se tratará en una etapa posterior.

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
- Ticket: número legible y único, título, descripción, prioridad, estado y fechas de creación/actualización.
- Priority y status se restringen a valores acordados por el negocio; el catálogo exacto se definirá antes de implementar sus reglas.
- resolution_note se exige al resolver o cerrar el ticket según la política que se confirme.
- TicketComment almacena contenido, fecha, autor y visibilidad. author_type diferencia solicitante y agente; author_id identifica a la persona autora.
- TicketStatusHistory conserva estado anterior/nuevo, fecha, motivo y actor. actor_type y actor_id identifican quién realizó el cambio.
- Los identificadores de autor y actor son referencias polimórficas; su validación corresponde al servicio mientras no exista una entidad común de identidad.

### Persistencia y posible distribución entre bases

Para la etapa de modelo y relaciones se usará inicialmente una sola base, con claves foráneas reales entre las tablas. Para practicar dos fuentes de datos, la propuesta pendiente de validar es:

- MySQL: Category, Ticket, TicketComment y TicketStatusHistory.
- Oracle: Requester y SupportAgent, si esas personas realmente se administran allí.

Al separar las fuentes, requester_id y assigned_agent_id en Ticket serán identificadores externos. No habrá claves foráneas ni relaciones JPA entre MySQL y Oracle; las consultas a cada fuente se realizarán por separado. La aplicación debe definir cómo responde si Oracle no está disponible.

Supplier no forma parte del modelo inicial. Se reconsiderará si se aprueba un caso de escalamiento de tickets a proveedores externos. El TicketEntity actual guarda nombre y correo del solicitante directamente; al implementar Requester se deberá decidir cómo migrar esos campos y ajustar TicketRequestDto y sus mappers.

## 9. Reglas de negocio iniciales

- El título, la descripción, la categoría y un contacto válido son obligatorios para crear el ticket.
- El sistema asigna el estado inicial `OPEN` y la fecha de creación; el cliente no puede elegirlos libremente.
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

## 11. Supuestos y preguntas abiertas

- La primera versión es backend REST, sin frontend.
- El uso simultáneo de MySQL y Oracle se planifica como etapa del curso; confirmar objetivo y disponibilidad de ambas bases.
- Propuesta pendiente: MySQL para tickets y datos operativos; Oracle para datos maestros de solicitantes y agentes. Confirmar con el profesor y definir quién administra esos registros.
- No usar claves foráneas ni relaciones JPA entre las dos bases. Las referencias serán identificadores; definir el comportamiento si alguna fuente no está disponible.
- Confirmar las versiones de Java y Spring Boot para el entorno de clase y evaluación, por la diferencia entre el temario y los ejemplos locales.
- Confirmar si la evaluación exige Docker, despliegue cloud, correo, Quartz o Swagger/OpenAPI.
- Definir si comentarios del solicitante y notas internas del agente tienen visibilidad distinta.
- Aprobar las transiciones de estado, condiciones de cierre y reapertura, y si un cambio de estado exige comentario.
- Definir qué servicio externo se consumirá. Candidato: catálogo de activos de TI para complementar tickets de hardware; especificar autenticación, timeout y comportamiento ante indisponibilidad.
- Definir si el alcance requiere SLA y cómo medir el tiempo de atención.
- Supplier solo se agrega como entidad si se implementa el escalamiento de tickets a proveedores externos.
