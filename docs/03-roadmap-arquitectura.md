# Roadmap de entregas y evolución de arquitectura

## 1. Propósito y criterio de numeración

Este roadmap refleja el avance real del Support Ticket System y los temas del curso. Los números de rama representan incrementos del proyecto; no equivalen uno a uno a las sesiones del profesor. Algunas capacidades REST se implementaron temprano para tener un flujo verificable y se podrán profundizar cuando se cubra la sesión correspondiente.

La aplicación evoluciona como un monolito modular en capas. No se crearán copias del proyecto por versión.

## 2. Estado y etapas

| N.º | Rama propuesta | Relación con el curso | Enfoque y resultado | Estado |
|---|---|---|---|---|
| 01 | architecture/01-layered-foundation | Sesiones 1–2 | Esqueleto Spring Boot, capas y CRUD inicial de tickets | Completada |
| 02 | architecture/02-service-layer | Sesión 6, fundamentos | Servicio de negocio entre controlador y repositorio | Completada |
| 03 | architecture/03-dto-validation-mapping-exceptions | Sesiones 7–8, conceptos adelantados | DTOs, MapStruct, validación y manejo centralizado de errores | Completada e integrada en develop |
| 04 | architecture/04-ticket-domain-model | Sesiones 3–4 | Modelo relacional y relaciones JPA; número y fechas del ticket; filtros, ordenamiento, paginación y eliminación lógica | Completada y verificada manualmente con Postman en la rama actual |
| 05 | architecture/05-feature-generic-architecture | Modularización y reutilización | Paquetes por funcionalidad, separación business/generic, contratos e implementaciones reutilizables | Planificada; revisar contra el dominio antes de migrar |
| 06 | architecture/06-multiple-data-sources | Sesión 5 | Configuración de MySQL y Oracle con propiedad de datos definida, pools y repositorios separados | Planificada; decisiones por validar |
| 07 | architecture/07-business-rules-workflow | Sesiones 6–7 | Asignación, transiciones de estado, comentarios, historial, transacciones y excepciones de negocio | Parcialmente implementada en la rama actual; validar reglas antes de integrar |
| 08 | architecture/08-external-integrations | Sesión 7 | Paquetes de integración, cliente REST, autenticación ante proveedor, correo y tareas programadas según necesidad | Planificada; elegir casos de uso; diferenciar autenticación externa de la seguridad de esta API |
| 09 | architecture/09-rest-documentation | Sesión 8 | Contratos REST, respuestas personalizadas, ControllerAdvice, Swagger/OpenAPI y HATEOAS si aporta valor | Planificada; CRUD y errores básicos ya existen |
| 10 | architecture/10-packaging-deployment | Sesión 9 | Empaque JAR/WAR, configuración por ambiente y despliegue requerido; Docker/AWS si corresponde | Planificada |
| 11 | architecture/11-course-demo | Cierre del curso | Demo reproducible, explicación de decisiones y retrospectiva | Planificada |

## 3. Flujo de ramas

- main: versión estable y presentable.
- develop: integración de incrementos terminados.
- architecture/NN-*: rama temporal para una mejora enfocada.
- Tags mvp-*: hitos estables integrados en main.

Crear la rama de arquitectura desde develop, implementar y verificar el incremento, integrarlo en develop y etiquetar un MVP cuando esté listo para presentar. Antes de iniciar una etapa, confirmar que la anterior está integrada.

## 4. Alcance de los próximos incrementos

### Etapa 04: modelo del dominio y persistencia

La rama actual contiene entidades para Requester, Category, SupportAgent, Ticket, TicketComment y TicketStatusHistory, con sus relaciones JPA principales. Ticket incluye número legible único, fechas de creación/actualización y borrado lógico mediante `active` y `deletedAt`. El listado admite filtros, ordenamiento y paginación. La creación, consulta paginada, modificación de fechas y eliminación lógica se verificaron manualmente con Postman. Los datos de auditoría y las reglas avanzadas del flujo quedan registrados para la etapa 07. Mantener una sola base durante este incremento para validar el modelo y las relaciones. No agregar Supplier salvo que se apruebe el flujo de escalamiento a proveedores.

### Etapa 05: paquetes por funcionalidad y componentes genéricos

Organizar las capacidades del sistema por funcionalidad bajo `business/ticket`, `business/category`, `business/requester` y `business/supportagent`, con las capas necesarias dentro de cada módulo. Mantener `generic` para contratos y componentes transversales cuya reutilización haya sido comprobada. Los componentes específicos pueden implementar o extender contratos genéricos, dejando visibles las reglas propias de tickets. Comparar el diseño actual con el objetivo y justificar interfaces, herencia y límites. `@MappedSuperclass` es candidato para atributos persistentes genuinamente compartidos; estudiar `SINGLE_TABLE`, `JOINED` y `TABLE_PER_CLASS`, pero aplicarlos solo si el dominio tiene una jerarquía real. Hacer la migración por módulos, verificando comportamiento y endpoints en cada paso.

### Etapa 06: múltiples fuentes de datos

Propuesta pendiente de validar: MySQL almacenaría tickets y datos operativos; Oracle sería fuente de datos maestros para solicitantes y agentes. Cada fuente tendría sus propiedades, pool, entidades, repositorios, `EntityManagerFactory` y administrador de transacciones propios, organizados bajo infraestructura de persistencia. Las referencias entre bases usarán identificadores, sin claves foráneas ni relaciones JPA cruzadas. Evitar transacciones que pretendan confirmar cambios atómicos en ambas bases.

### Etapa 07: reglas y flujo de soporte

La rama actual ya implementa asignación, comentarios, cambios de ticket e historial de estado con operaciones transaccionales. Confirmar que las transiciones permitidas y las condiciones de cierre reflejen las reglas aprobadas, y revisar los casos de error antes de marcar la etapa como terminada.

### Etapa 08: integraciones, mensajería y automatización

Separar los adaptadores de sistemas ajenos del negocio propio, por ejemplo en `integration/rest/<proveedor>` para APIs externas y en infraestructura de persistencia para conexiones a bases externas. Un puerto/interfaz expresa la operación requerida por el negocio; una implementación concreta maneja HTTP, DTOs externos, autenticación del proveedor y traducción de errores. Para un proveedor con token, aislar la obtención y el uso del Bearer token, definir timeouts, tratar errores HTTP/de red y especificar qué ocurre ante indisponibilidad. Nunca registrar credenciales o tokens. Esto es distinto de autenticar usuarios de la API propia. El catálogo de activos de TI sigue siendo un candidato de integración si apoya incidentes de hardware. Incorporar Java Mail y Quartz o `@Scheduled` para notificaciones o tareas que tengan un caso de uso aprobado; decidir entre tareas simples en proceso y un scheduler persistente según la necesidad de recuperación y operación.

**Criterios de calidad para esta etapa:** el adaptador REST debe usar el identificador recibido y manejar respuestas vacías explícitamente; evitar registrar objetos de credenciales, encabezados Authorization o tokens; acotar excepciones y configurar timeouts. Para el correo programado, definir idempotencia, destinatarios, registro y recuperación de fallos; evitar `System.out` y centralizar la plantilla del mensaje.

### Etapa 09: presentación REST y documentación

Completar los contratos REST, las respuestas personalizadas y la documentación Swagger/OpenAPI. Mantener `@ControllerAdvice` como frontera de errores HTTP y la versión `/api/v1`. Evaluar HATEOAS para navegación de recursos si mejora el consumo de la API; evitar duplicar DTOs y respuestas que ya existen.

### Etapa 10: empaquetado y despliegue

Comparar JAR ejecutable y WAR para contenedor, preparar configuración por ambiente y documentar el despliegue solicitado. Practicar Tomcat y JBoss EAP/WildFly según disponibilidad; incluir Docker/Docker Compose y AWS Elastic Beanstalk si forman parte de la evaluación. Revisar configuración externa y principios twelve-factor pertinentes.

## 5. MVPs

- MVP 0: base del proyecto y CRUD de tickets.
- MVP 1: modelo relacional, relaciones y persistencia de referencias.
- MVP 2: flujo de soporte con asignación, comentarios, estados e historial.
- MVP 3: arquitectura por funcionalidad y contratos genéricos justificados.
- MVP 4: API documentada e integración externa seleccionada.
- MVP 5: empaquetado, despliegue y demo final.

Los MVPs se ajustarán a los criterios del profesor y a la disponibilidad de Oracle y del servicio externo.

## 6. Decisiones pendientes

1. Confirmar con el profesor las versiones de JDK y Spring Boot del entorno de evaluación.
2. Confirmar que se requiere conexión simultánea a MySQL y Oracle y qué datos debe contener cada base.
3. Definir el origen y la administración de solicitantes y agentes.
4. Elegir el servicio externo y su propósito; definir autenticación frente al proveedor, timeouts, tratamiento de errores y comportamiento ante indisponibilidad.
5. Acordar el alcance de la migración `business/generic` y las abstracciones reutilizables que el dominio justifica.
6. Confirmar si se evalúan autenticación de la API propia, correo, Quartz, Swagger, HATEOAS, Docker o AWS.
7. Definir la configuración local con secretos fuera del repositorio y sin registrarlos en logs.

## 7. Criterios para integrar una etapa

Antes de integrar una rama en develop, registrar qué necesidad del sistema habilita, qué límite o responsabilidad arquitectónica cambia, cómo se verifica y qué queda pendiente. Mantener cada rama enfocada y crear etiquetas mvp-* solo sobre versiones estables integradas en main.
