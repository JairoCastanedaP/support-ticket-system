# Roadmap de entregas y evolución de arquitectura

## 1. Propósito y criterio de numeración

Este roadmap refleja el avance real del Support Ticket System y los temas del curso. Los números de rama representan incrementos del proyecto; no equivalen uno a uno a las sesiones del profesor. Algunas capacidades REST se implementaron temprano para tener un flujo verificable y se podrán profundizar cuando se cubra la sesión correspondiente.

La aplicación evoluciona como un monolito modular en capas. No se crearán copias del proyecto por versión.

## 2. Estado y etapas

| N.º | Rama propuesta | Relación con el curso | Enfoque y resultado | Estado |
|---|---|---|---|---|
| 01 | architecture/01-layered-foundation | Sesiones 1–2 | Esqueleto Spring Boot, capas y CRUD inicial de tickets | Completada |
| 02 | architecture/02-service-layer | Sesión 6, fundamentos | Servicio de negocio entre controlador y repositorio | Completada |
| 03 | architecture/03-dto-validation-mapping-exceptions | Sesiones 7–8, conceptos adelantados | DTOs, MapStruct, validación y manejo centralizado de 404 y 400 | Implementada; integración en develop por confirmar |
| 04 | architecture/04-ticket-domain-model | Sesiones 3–4 | Modelo relacional y relaciones JPA para solicitantes, categorías, agentes y tickets; consultas paginadas | Siguiente |
| 05 | architecture/05-multiple-data-sources | Sesión 5 | Configuración de MySQL y Oracle con propiedad de datos definida, pools y repositorios separados | Planificada; decisiones por validar |
| 06 | architecture/06-business-rules-workflow | Sesiones 6–7 | Asignación, transiciones de estado, comentarios, historial, transacciones y excepciones de negocio | Planificada |
| 07 | architecture/07-external-integrations | Sesión 7 | Consumo de un servicio web útil para soporte, con autenticación si aplica y manejo de fallos | Planificada; servicio por definir |
| 08 | architecture/08-rest-documentation | Sesión 8 | Completar OpenAPI/Swagger, contratos, respuestas personalizadas y HATEOAS si aporta valor | Planificada; CRUD y errores básicos ya existen |
| 09 | architecture/09-packaging-deployment | Sesión 9 | Empaque JAR/WAR y despliegue requerido; Docker/AWS si corresponde | Planificada |
| 10 | architecture/10-course-demo | Sesión 10 | Demo reproducible, explicación de decisiones y retrospectiva | Planificada |

## 3. Flujo de ramas

- main: versión estable y presentable.
- develop: integración de incrementos terminados.
- architecture/NN-*: rama temporal para una mejora enfocada.
- Tags mvp-*: hitos estables integrados en main.

Crear la rama de arquitectura desde develop, implementar y verificar el incremento, integrarlo en develop y etiquetar un MVP cuando esté listo para presentar. Antes de iniciar una etapa, confirmar que la anterior está integrada.

## 4. Alcance de los próximos incrementos

### Etapa 04: modelo del dominio y persistencia

Diseñar el esquema antes de modificar entidades. El modelo inicial candidato incluye Requester, Category, SupportAgent y Ticket. TicketComment y TicketStatusHistory se incorporarán cuando se implementen los casos de uso de comentarios y cambios de estado. No agregar Supplier salvo que se apruebe el flujo de escalamiento a proveedores.

Mantener una sola base durante este incremento para validar el modelo y las relaciones. Aplicar relaciones JPA, restricciones y consultas paginadas.

### Etapa 05: múltiples fuentes de datos

Propuesta pendiente de validar: MySQL almacenaría tickets y datos operativos; Oracle sería fuente de datos maestros para solicitantes y agentes. Cada fuente tendría sus entidades, repositorios y configuración de conexión. Las referencias entre bases usarán identificadores, sin claves foráneas ni relaciones JPA cruzadas. Evitar transacciones que pretendan confirmar cambios atómicos en ambas bases.

### Etapas 06–07: reglas e integración

Implementar asignación a agentes activos, transiciones permitidas, comentarios e historial de estado. Elegir después un servicio web externo con propósito verificable; el catálogo de activos de TI es candidato para incidentes de hardware. Definir autenticación, timeout, respuesta ante indisponibilidad y si el flujo debe continuar cuando la integración falla. Correo y Quartz/@Scheduled se agregan solo si el alcance lo justifica.

## 5. MVPs

- MVP 0: base del proyecto y CRUD de tickets.
- MVP 1: modelo relacional, relaciones y persistencia de referencias.
- MVP 2: flujo de soporte con asignación, comentarios, estados e historial.
- MVP 3: API documentada e integración externa seleccionada.
- MVP 4: empaquetado, despliegue y demo final.

Los MVPs se ajustarán a los criterios del profesor y a la disponibilidad de Oracle y del servicio externo.

## 6. Decisiones pendientes

1. Confirmar con el profesor las versiones de JDK y Spring Boot del entorno de evaluación.
2. Confirmar que se requiere conexión simultánea a MySQL y Oracle y qué datos debe contener cada base.
3. Definir el origen y la administración de solicitantes y agentes.
4. Elegir el servicio externo, su propósito y el comportamiento ante errores.
5. Confirmar si se evalúan autenticación, correo, Quartz, Swagger, HATEOAS, Docker o AWS.
6. Definir la configuración local sin subir credenciales al repositorio.

## 7. Criterios para integrar una etapa

Antes de integrar una rama en develop, registrar qué necesidad del sistema habilita, qué límite o responsabilidad arquitectónica cambia, cómo se verifica y qué queda pendiente. Mantener cada rama enfocada y crear etiquetas mvp-* solo sobre versiones estables integradas en main.
