# Lineamientos y stack tecnológico

## 1. Propósito

`Support Ticket System` es un proyecto académico para construir una mesa de ayuda interna de TI mientras se estudian y refinan los principios de arquitectura en capas con Java y Spring. El sistema evolucionará por incrementos: cada cambio funcional debe servir también para practicar una decisión arquitectónica y dejarla visible en el historial del repositorio.

## 2. Lineamientos de arquitectura

### Estilo y límites

- Mantener una aplicación **monolítica modular**. No introducir microservicios como parte de los MVP iniciales.
- Separar responsabilidades en las capas de **presentación**, **negocio**, **persistencia** y **base de datos**.
- Organizar el código por funcionalidad y mantener visibles las capas dentro de cada funcionalidad. La propuesta de paquetes inicial aparece en la siguiente sección; las preocupaciones transversales viven en `shared/`.
- Permitir dependencias hacia adentro: el controlador de presentación delega en un servicio de negocio; el servicio aplica reglas y coordina la persistencia; los repositorios acceden a la base de datos. El controlador no debe ejecutar consultas JPA ni contener reglas de negocio.
- Mantener Customer/Supplier como referencias de aprendizaje, no como entidades obligatorias del producto. El dominio propio será el de tickets de soporte.

### Organización de referencia

```text
features/
└── ticket/
    ├── presentation/
    │   ├── controller/
    │   └── dto/
    ├── business/
    │   ├── service/
    │   └── domain/
    ├── persistence/
    │   ├── entity/
    │   └── repository/
    └── mapper/
shared/
  config/
  exceptions/
  constants/
```

La estructura puede crecer cuando aparezcan funcionalidades con reglas propias, por ejemplo `category/` o `reporting/`. No es necesario crear todos esos módulos desde el primer commit.

### API y contratos

- Exponer recursos REST bajo una ruta versionada, inicialmente `/api/v1`.
- Usar DTOs para las solicitudes y respuestas HTTP; no exponer entidades JPA directamente.
- Validar formato y campos de entrada en los DTOs, y reglas del proceso en la capa de negocio.
- Centralizar el tratamiento de excepciones y mantener respuestas HTTP coherentes: validación (`400`), ausencia (`404`), conflicto de negocio (`409`) y creación (`201`).
- Documentar endpoints y ejemplos para poder probarlos con Postman.

### Persistencia y datos

- Diseñar primero el modelo relacional y sus restricciones antes de agregar relaciones JPA complejas.
- Usar claves foráneas y restricciones de base de datos para proteger la integridad de los datos.
- Introducir transacciones en operaciones de negocio que actualicen más de un registro.
- Conservar el historial de cambios de estado de un ticket; no borrar físicamente registros de negocio si deben permanecer para auditoría.
- Añadir paginación y ordenamiento a las consultas de listas antes de que crezca el volumen de datos.
- Implementar auditoría automática solo cuando estén configurados y verificados los listeners, la habilitación de auditoría y el origen de usuario/fecha correspondiente.

### Reutilización

- No crear abstracciones genéricas antes de tener al menos dos casos equivalentes y comprender sus diferencias.
- Evitar una clase base genérica que oculte reglas particulares del ticket.
- Registrar en las ramas numeradas qué responsabilidad se separó o reutilizó y por qué.

### Configuración y seguridad

- No subir contraseñas, tokens, claves ni datos personales reales al repositorio.
- Usar variables de entorno o configuración local ignorada por Git para credenciales.
- Mantener perfiles de desarrollo y despliegue explícitos. La autenticación/autorización puede planificarse como ampliación posterior, salvo que el profesor la solicite antes.

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
| Base relacional | MySQL como candidata principal del proyecto | Persistencia integrada del MVP |
| Base auxiliar | H2 para pruebas o ejecuciones locales rápidas, si resulta conveniente | Desarrollo y verificaciones aisladas |
| Pruebas | Dependencias de prueba administradas por Spring Boot | Pruebas unitarias y de integración cuando se implemente cada etapa |
| Cliente REST | Postman | Ejercitar endpoints y guardar colecciones de solicitudes |
| Control de versiones | Git y GitHub | Historial por ramas arquitectónicas y etiquetas de MVP |

Las versiones concretas de las dependencias se gestionan en `pom.xml`; se deben copiar de la versión de clase que el profesor confirme y mantener coherentes entre sí.

### Diferencia de versiones que debe confirmarse

El temario PDF menciona **Spring Boot 3.0** y JDK **11, 20 y 26**. Los ejemplos locales v5.a, v5.b y v5.c declaran **Spring Boot 4.1.1**, **Java 25**, **MapStruct 1.6.3**, **Lombok 1.18.42** y `maven-compiler-plugin` 3.13.0. Estos valores reflejan dos fuentes con versiones distintas; no se deben mezclar sin validar compatibilidad.

**Decisión provisional:** antes de crear el `pom.xml` del sistema, confirmar con el profesor qué JDK y versión de Spring Boot usarán para el desarrollo y evaluación. Si se exige partir de la versión más reciente del repositorio de clase, usar ese `pom.xml` como base, revisando sus dependencias y configuración de compilación.

## 4. Tecnologías del curso para etapas posteriores

No son prerrequisitos para el primer CRUD; se incorporarán cuando el roadmap llegue a ellos:

- **Diseño y SQL:** Oracle SQL Developer Data Modeler; scripts de tablas, claves, índices y datos iniciales; SQL y JPQL.
- **Persistencia avanzada:** relaciones JPA, herencia, `@MappedSuperclass`, paginación, ordenamiento, procedimientos almacenados, triggers, auditoría y pool de conexiones.
- **Negocio e integración:** MapStruct/ModelMapper, consumo de servicios web, tokens, Java Mail, Quartz y `@Scheduled`.
- **REST:** Swagger/OpenAPI, `@ControllerAdvice`, versionamiento de API, respuestas personalizadas y HATEOAS cuando aporte valor.
- **Empaque y despliegue:** JAR/WAR, Apache Tomcat, JBoss EAP/WildFly, Docker, Docker Compose y AWS Elastic Beanstalk.
- **Motores mencionados:** Oracle, MySQL y PostgreSQL. Para este proyecto se elegirá uno como base principal y no se intentará soportarlos todos en el MVP.
- **Herramientas auxiliares:** IDE (IntelliJ IDEA, STS o Eclipse) y JSONLint para revisar JSON cuando sea necesario.

## 5. Decisiones técnicas registradas

| Decisión | Estado inicial |
|---|---|
| Estilo arquitectónico | Monolito modular en capas |
| Organización de paquetes | Por funcionalidad, con subpaquetes por responsabilidad |
| Base de datos | MySQL propuesta; pendiente de confirmar con el profesor |
| JDK y Spring Boot | Pendientes de confirmación por discrepancia entre temario y ejemplos |
| Interfaz de usuario | Fuera de alcance inicial; API REST probada con Postman |
| Autenticación | Fuera del primer MVP; se revisará si se exige en el curso |
| Integraciones externas | Posteriores al flujo principal de tickets |
