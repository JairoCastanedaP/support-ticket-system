# Lineamientos y stack tecnológico

## 1. Propósito

`Support Ticket System` es un proyecto académico para construir una mesa de ayuda interna de TI mientras se estudian y refinan los principios de arquitectura en capas con Java y Spring. El sistema evolucionará por incrementos: cada cambio funcional debe servir también para practicar una decisión arquitectónica y dejarla visible en el historial del repositorio.

## 2. Lineamientos de arquitectura

### Estilo y límites

- Mantener una aplicación **monolítica modular**. No introducir microservicios como parte de los MVP iniciales.
- Separar responsabilidades en las capas de **presentación**, **negocio**, **persistencia** y **base de datos**.
- Evolucionar gradualmente desde la estructura actual por capa hacia una organización por funcionalidad (`business/ticket`, `business/category`, `business/requester`, `business/supportagent`), manteniendo las capas dentro de cada módulo cuando sea útil. Los contratos y componentes transversales compartidos pueden vivir en `generic` y `business/shared`; no reorganizar todo en una sola modificación.
- Permitir dependencias hacia adentro: el controlador de presentación delega en un servicio de negocio; el servicio aplica reglas y coordina la persistencia; los repositorios acceden a la base de datos. El controlador no debe ejecutar consultas JPA ni contener reglas de negocio.
- Modelar los conceptos propios del negocio: Requester, SupportAgent, Category y Ticket. Incorporar proveedores solo si se aprueba un flujo de escalamiento que los requiera.

### Organización de paquetes

La aplicación parte de una estructura por capa y puede evolucionar hacia la siguiente organización por funcionalidad:

    com.co.support.it.support_ticket_system
    ├── business
    │   ├── ticket
    │   │   ├── controller
    │   │   ├── dto
    │   │   ├── entity
    │   │   ├── mapper
    │   │   ├── repository
    │   │   └── service
    │   ├── category
    │   ├── requester
    │   ├── supportagent
    │   └── shared
    ├── integration
    │   ├── rest
    │   │   └── <provider>
    │   └── messaging
    ├── infrastructure
    │   └── persistence
    │       └── <datasource>
    └── generic
        ├── controller
        ├── dto
        ├── entity
        ├── exception
        ├── mapper
        ├── repository
        └── service

Este esquema es una dirección objetivo, no una instrucción de migración inmediata ni una obligación de crear todos los paquetes. Las excepciones HTTP globales pueden permanecer en un paquete compartido. La etapa de arquitectura por funcionalidad debe trasladar y revisar módulos gradualmente, preservando contratos y comportamiento.

### Interfaces, genéricos y herencia

- Distinguir `business` específico de `generic` reutilizable, contratos de sus implementaciones y herencia de composición.
- Crear interfaces o clases genéricas cuando haya operaciones equivalentes en más de un módulo y sus variaciones estén identificadas. Mantener servicios, consultas y reglas particulares de Ticket en su módulo; no forzar todo a CRUD genérico.
- Usar herencia de entidades solo cuando el modelo exprese una relación real. `@MappedSuperclass` puede compartir campos persistentes comunes si existe esa necesidad. Estudiar `SINGLE_TABLE`, `JOINED` y `TABLE_PER_CLASS` como alternativas del curso y documentar sus efectos; no agregar jerarquías artificiales a Requester, SupportAgent, Category o Ticket para demostrar anotaciones.
- Registrar qué contratos se reutilizan, qué métodos quedan específicos y por qué. Evitar ciclos de dependencia entre `generic` y `business`.
### API y contratos

- Exponer recursos REST bajo una ruta versionada, inicialmente `/api/v1`.
- Usar DTOs para las solicitudes y respuestas HTTP; no exponer entidades JPA directamente.
- Validar formato y campos de entrada en los DTOs, y reglas del proceso en la capa de negocio.
- Centralizar el tratamiento de excepciones y mantener respuestas HTTP coherentes: validación (`400`), ausencia (`404`), conflicto de negocio (`409`) y creación (`201`).
- Documentar endpoints y ejemplos para poder probarlos con Postman.

### Persistencia y datos

- Diseñar primero el modelo relacional y sus restricciones antes de agregar relaciones JPA complejas.
- Usar claves foráneas y restricciones para proteger la integridad de los datos que viven en una misma base.
- Implementar transacciones en operaciones que actualicen varios registros dentro de una misma base.
- Conservar el historial de cambios de estado cuando sea necesario para auditoría.
- Eliminar tickets de forma lógica: al retirarlos, marcar `active` como `false` y guardar `deletedAt`; mantener ambos valores consistentes, conservar comentarios e historial y excluir los tickets retirados de las consultas normales.
- Añadir paginación y ordenamiento a las consultas de listas.
- Diseñar la captura de auditoría con origen de actor/fecha explícito. Para cambios que requieren publicación a Oracle, persistir primero el evento en una bandeja de salida local dentro de la transacción de negocio; no asumir que un listener por sí solo garantiza entrega central.
- Decisión de trabajo para múltiples bases: MySQL conserva los datos operativos, incluidos solicitantes y agentes; Oracle será el repositorio central de eventos de auditoría, propios y recibidos de otras aplicaciones.
- La escritura de auditoría originada por una operación de negocio se hará primero en una bandeja de salida local en MySQL dentro de la misma transacción. Un proceso reintentable la publicará en Oracle con identificadores idempotentes. No se dependerá de una transacción atómica distribuida entre las bases.
- Cada base tendrá configuración, pool, entidades y repositorios separados. No crear relaciones JPA ni claves foráneas entre bases distintas.
- La tabla central de auditoría será append-only y tendrá campos de origen, evento, entidad/acción, fecha, actor cuando esté disponible, IP confiable cuando corresponda, correlación y resultado. Minimizar los detalles almacenados. El historial de estados del ticket sigue siendo el registro funcional de su ciclo de vida.
- La importación de otras aplicaciones usará archivos CSV o JSON Lines en una ruta configurable fuera del repositorio. Validar formato, detectar duplicados, guardar procesados/rechazados y proteger la carpeta con permisos adecuados. Considerar Spring Batch cuando se necesiten checkpoints, reanudación o procesamiento voluminoso; para cargas pequeñas se puede evaluar Quartz o `@Scheduled`.
- Los eventos de auditoría podrán consultarse mediante endpoints de solo lectura, paginados y sujetos a autorización antes de exponer datos de actor, IP o detalles operativos.
- Las notificaciones de correo se crearán por eventos de negocio, en particular por transición a estado final del ticket, y se enviarán asíncronamente a destinatarios configurables. Guardar estado e intentos para reintentar sin revertir la operación del ticket. Las credenciales SMTP y correos de destinatarios se configuran fuera del repositorio.
- Los reportes programados podrán generar resúmenes agregados de auditoría y enviarlos por correo; definir frecuencia, formato, destinatarios y retención minimizando datos personales.
- Consumir servicios externos mediante adaptadores en `integration`, separados del negocio y configurados por propiedades. Definir propósito, autenticación ante el proveedor, tiempos de espera, errores HTTP/de red y comportamiento ante indisponibilidad antes de implementar el cliente. La interfaz del puerto debe expresar las necesidades del negocio; el adaptador traduce el protocolo y los DTOs externos. Usar el identificador recibido en cada operación, tratar respuestas vacías explícitamente y no registrar credenciales ni tokens.

### Reutilización

- No crear abstracciones genéricas antes de tener al menos dos casos equivalentes y comprender sus diferencias.
- Evitar una clase base genérica que oculte reglas particulares del ticket.
- Registrar en las ramas numeradas qué responsabilidad se separó o reutilizó y por qué.

### Configuración y seguridad

- No subir contraseñas, tokens, claves ni datos personales reales al repositorio.
- Usar variables de entorno o configuración local ignorada por Git para credenciales. No imprimir credenciales ni tokens en logs.
- Mantener perfiles de desarrollo y despliegue explícitos y definir timeouts para llamadas externas, junto con el tratamiento de fallos de red y respuestas HTTP no exitosas.
- Distinguir la autenticación de la aplicación ante un proveedor externo (por ejemplo, obtener y usar un token para una API) de la autenticación/autorización de usuarios en la API propia. Esta última puede planificarse como ampliación posterior, salvo que el profesor la solicite antes.

## 3. Stack tecnológico

### Base del primer MVP

| Área | Selección de trabajo | Uso |
|---|---|---|
| Lenguaje y runtime | Java, alineado con el JDK solicitado por el profesor | Desarrollo y ejecución |
| Framework | Spring Boot, con versión tomada del repositorio actualizado de clase | Configuración y arranque de la aplicación |
| Construcción | Maven Wrapper (`mvnw`) | Compilar con Maven sin depender de una instalación local idéntica |
| API | Spring Web MVC | Controladores y endpoints REST |
| Persistencia | Spring Data JPA | Repositorios y operaciones CRUD |
| Validación | Jakarta Bean Validation / Spring Boot Validation | Validación declarativa de DTOs |
| Mapeo | MapStruct | Conversión entre DTOs y entidades cuando se incorpore esa capa |
| Productividad | Lombok, de forma moderada | Reducir código repetitivo |
| Base operativa candidata | MySQL | Tickets, categorías, comentarios e historial |
| Base de auditoría central | Oracle | Eventos de auditoría de esta aplicación y de otros sistemas |
| Base local de desarrollo | H2, si resulta conveniente | Ejecuciones rápidas; no sustituye la verificación de MySQL y Oracle |
| Pruebas | Dependencias de prueba administradas por Spring Boot | Pruebas unitarias y de integración cuando se implemente cada etapa |
| Cliente REST | Postman | Ejercitar endpoints y guardar colecciones de solicitudes |
| Control de versiones | Git y GitHub | Historial por ramas arquitectónicas y etiquetas de MVP |

Las versiones concretas de las dependencias se gestionan en `pom.xml`; se deben copiar de la versión de clase que el profesor confirme y mantener coherentes entre sí.

### Diferencia de versiones que debe confirmarse

El temario y la configuración de los materiales de clase presentan discrepancias de versiones: el temario menciona Spring Boot 3.0 y JDK 11, 20 y 26, mientras que la configuración técnica revisada usa Spring Boot 4.1.1 y Java 25. MapStruct 1.6.3, Lombok 1.18.42 y `maven-compiler-plugin` 3.13.0 también aparecen en esa configuración. No mezclar versiones sin validar compatibilidad con el entorno de evaluación.

**Decisión provisional:** antes de crear el `pom.xml` del sistema, confirmar con el profesor qué JDK y versión de Spring Boot usarán para el desarrollo y evaluación. Alinear las dependencias y la configuración de compilación con el entorno requerido, validando su compatibilidad.

## 4. Tecnologías del curso para etapas posteriores

No son prerrequisitos para el primer CRUD; se incorporarán cuando el roadmap llegue a ellos:

- **Diseño y SQL:** Oracle SQL Developer Data Modeler; scripts de tablas, claves, índices y datos iniciales; SQL y JPQL.
- **Persistencia avanzada:** relaciones JPA, herencia, `@MappedSuperclass`, paginación, ordenamiento, procedimientos almacenados, triggers, auditoría centralizada y pools de conexiones a MySQL y Oracle.
- **Negocio e integración:** MapStruct/ModelMapper, consumo de servicios web, tokens, Java Mail, importación de auditoría desde archivos, Oracle como repositorio central, Quartz/`@Scheduled` y reportes batch.
- **REST:** Swagger/OpenAPI, `@ControllerAdvice`, versionamiento de API, respuestas personalizadas y HATEOAS cuando aporte valor.
- **Empaque y despliegue:** JAR/WAR, Apache Tomcat, JBoss EAP/WildFly, Docker, Docker Compose y AWS Elastic Beanstalk.
- Motores mencionados: Oracle, MySQL y PostgreSQL. Para el caso de dos bases se propone inicialmente MySQL y Oracle; PostgreSQL queda fuera de este alcance salvo que el profesor indique otra cosa.
- **Herramientas auxiliares:** IDE (IntelliJ IDEA, STS o Eclipse) y JSONLint para revisar JSON cuando sea necesario.

## 5. Decisiones técnicas registradas

| Decisión | Estado inicial |
|---|---|
| Estilo arquitectónico | Monolito modular en capas |
| Organización de paquetes | Iniciar por capas y migrar gradualmente a módulos de negocio por funcionalidad; reservar `generic` para contratos y componentes con reutilización comprobada. Mantener integraciones y configuración de infraestructura separadas |
| Bases de datos | MySQL para datos operativos; Oracle para auditoría centralizada. La importación de auditoría puede provenir de archivos depositados por otros sistemas |
| Integración externa | Caso de uso y servicio proveedor pendientes de definir; candidato: consulta de información de activos de TI |
| Autenticación externa | Aplicar solo si el proveedor elegido la requiere; gestionar credenciales fuera del repositorio y no registrar tokens |
| Relaciones entre bases | Sin claves foráneas ni relaciones JPA cruzadas; usar identificadores y consultar la fuente propietaria |
| JDK y Spring Boot | Pendientes de confirmación por discrepancia entre temario y ejemplos |
| Interfaz de usuario | Fuera de alcance inicial; API REST probada con Postman |
| Autenticación | Fuera del primer MVP; se revisará si se exige en el curso |
| Integraciones externas | Posteriores al flujo principal de tickets |
