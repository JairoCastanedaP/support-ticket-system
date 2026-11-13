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
- Usar herencia de entidades solo cuando el modelo exprese una relación real. `@MappedSuperclass` puede compartir campos persistentes comunes si existe esa necesidad. Estudiar `SINGLE_TABLE`, `JOINED` y `TABLE_PER_CLASS` como alternativas y documentar sus efectos; no agregar jerarquías artificiales a Requester, SupportAgent, Category o Ticket para demostrar anotaciones.
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
- El histórico de tickets se alimenta con un proceso Spring Batch lanzado a demanda: lectura paginada en MySQL, transformación y escritura idempotente (upsert) en PostgreSQL, procesando por bloques (chunks) y registrando el resultado de cada ejecución.
- Las notificaciones de correo se crearán por eventos de negocio, en particular por transición a estado final del ticket, y se enviarán asíncronamente a destinatarios configurables. Guardar estado e intentos para reintentar sin revertir la operación del ticket. Las credenciales SMTP y correos de destinatarios se configuran fuera del repositorio.
- Consumir servicios externos mediante adaptadores en `integration`, separados del negocio y configurados por propiedades. Definir propósito, autenticación ante el proveedor, tiempos de espera, errores HTTP/de red y comportamiento ante indisponibilidad antes de implementar el cliente. La interfaz del puerto debe expresar las necesidades del negocio; el adaptador traduce el protocolo y los DTOs externos. Usar el identificador recibido en cada operación, tratar respuestas vacías explícitamente y no registrar credenciales ni tokens.

### Reutilización

- No crear abstracciones genéricas antes de tener al menos dos casos equivalentes y comprender sus diferencias.
- Evitar una clase base genérica que oculte reglas particulares del ticket.
- Registrar en las ramas numeradas qué responsabilidad se separó o reutilizó y por qué.

### Configuración y seguridad

- No subir contraseñas, tokens, claves ni datos personales reales al repositorio.
- Usar variables de entorno o configuración local ignorada por Git para credenciales. No imprimir credenciales ni tokens en logs.
- Mantener perfiles de desarrollo y despliegue explícitos y definir timeouts para llamadas externas, junto con el tratamiento de fallos de red y respuestas HTTP no exitosas.
- Distinguir la autenticación de la aplicación ante un proveedor externo (por ejemplo, obtener y usar un token para una API) de la autenticación/autorización de usuarios en la API propia. La API no implementa login de usuarios: el portal de clientes externos se protege con API key y el actor de cada operación se toma del header `X-User-Id`.

## 3. Stack tecnológico

### Componentes

| Área | Selección de trabajo | Uso |
|---|---|---|
| Lenguaje y runtime | Java 25 | Desarrollo y ejecución |
| Framework | Spring Boot 4.1.x | Configuración y arranque de la aplicación |
| Construcción | Maven Wrapper (`mvnw`) | Compilar con Maven sin depender de una instalación local idéntica |
| API | Spring Web MVC | Controladores y endpoints REST |
| Persistencia | Spring Data JPA | Repositorios y operaciones CRUD |
| Validación | Jakarta Bean Validation / Spring Boot Validation | Validación declarativa de DTOs |
| Mapeo | MapStruct | Conversión entre DTOs y entidades |
| Productividad | Lombok, de forma moderada | Reducir código repetitivo |
| Base operativa | MySQL 8.4 | Tickets, categorías, solicitantes, agentes, comentarios, historial, bandeja de salida y notificaciones |
| Base de auditoría central | Oracle Free | Eventos de auditoría de esta aplicación y de otros sistemas |
| Base histórica | PostgreSQL | Histórico de tickets alimentado por Spring Batch |
| Base local de desarrollo | H2 | Ejecuciones rápidas; no sustituye la verificación con Docker |
| Procesos batch | Spring Batch | Volcado masivo de tickets a demanda hacia PostgreSQL |
| Tareas programadas | Quartz y `@Scheduled` | Envío de correos (Quartz) y relay de auditoría (`@Scheduled`) |
| Correo | Spring Mail + Mailpit | Notificación de tickets resueltos; Mailpit captura los correos en desarrollo |
| Cliente HTTP | `RestClient` | Servicio externo de activos (con token) y directorio interno (sin seguridad) |
| Documentación API | springdoc-openapi (Swagger) y Spring REST Docs | Contrato navegable y documentación `.html` generada desde pruebas |
| Pruebas | Dependencias de prueba administradas por Spring Boot | Pruebas del dominio y pruebas de REST Docs |
| Cliente REST | Postman | Una carpeta de pruebas por fase en `docs/JavaArchitecture.postman_collection.json` |
| Contenedores | Docker y Docker Compose | MySQL, Oracle, PostgreSQL, Mailpit y la aplicación |
| Control de versiones | Git y GitHub | Una rama por fase y etiqueta `mvp-final` |

Las versiones concretas de las dependencias se gestionan en `pom.xml`.

### Versiones

**Java 25** y **Spring Boot 4.1.x**, con MapStruct, Lombok y `maven-compiler-plugin` 3.13.0.

## 4. Decisiones técnicas registradas

| Decisión | Estado |
|---|---|
| Estilo arquitectónico | Monolito modular en capas |
| Organización de paquetes | Módulos por funcionalidad en `business/*`; contratos y componentes reutilizables en `generic`; integraciones e infraestructura separadas |
| Abstracciones genéricas | Se adoptan `GenericController`, `GenericService`, `CustomResponse` y la jerarquía de excepciones porque ya hay tres catálogos equivalentes (Category, Requester, SupportAgent). Ticket conserva servicio y reglas propias |
| Bases de datos | MySQL operativa, Oracle para auditoría central, PostgreSQL para el histórico |
| Relaciones entre bases | Sin claves foráneas ni relaciones JPA cruzadas; usar identificadores y consultar la fuente propietaria |
| Integración externa saliente | Catálogo de activos de TI (escuelajs) con login y token Bearer; credenciales en variables de entorno, sin registrar tokens |
| Integración externa entrante | Portal de clientes externos con `X-API-Key`, validada por un interceptor; no es un login de usuarios |
| Integración interna | Directorio de empleados (jsonplaceholder) sin seguridad |
| JDK y Spring Boot | Java 25 y Spring Boot 4.1.x |
| Interfaz de usuario | API REST sin frontend, probada con Postman y Swagger |
| Identificación del actor | Header `X-User-Id`; si no se envía, `SYSTEM` |

El detalle completo de las decisiones de diseño está en la sección 12 de `02-analisis-de-requerimientos.md`.
