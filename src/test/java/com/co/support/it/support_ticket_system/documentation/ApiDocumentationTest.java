package com.co.support.it.support_ticket_system.documentation;

import com.co.support.it.support_ticket_system.business.ticket.repository.TicketIndicatorsRepository;
import com.co.support.it.support_ticket_system.business.ticket.repository.TicketIndicatorsRepository.IndicatorRow;
import com.co.support.it.support_ticket_system.business.ticket.repository.TicketIndicatorsRepository.TicketIndicatorsResult;
import com.co.support.it.support_ticket_system.integration.asset.AssetCatalogClient;
import com.co.support.it.support_ticket_system.integration.asset.AssetResponseDto;
import com.co.support.it.support_ticket_system.integration.directory.EmployeeDirectoryClient;
import com.co.support.it.support_ticket_system.integration.directory.EmployeeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Genera la documentación de toda la API con Spring REST Docs (src/docs/asciidoc/index.adoc).
 * Cada prueba ejecuta una petición real contra la aplicación (perfil local, H2) y deja los fragmentos
 * en target/generated-snippets. Solo se simulan los servicios externos y el procedimiento de MySQL.
 */
@SpringBootTest(properties = {
        "app.portal.api-key=demo-portal-key",
        "app.persistence.stored-procedures-enabled=true"
})
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApiDocumentationTest {

    private static final String API = "/api/v1";
    private static final String USER = "X-User-Id";
    private static final String STAMP = String.valueOf(System.currentTimeMillis());

    // Datos compartidos entre pruebas (se crean en orden).
    private static Long categoryId;
    private static Long requesterId;
    private static Long agentId;
    private static Long ticketId;
    private static String ticketNumber;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private AssetCatalogClient assetCatalogClient;

    @MockitoBean
    private EmployeeDirectoryClient employeeDirectoryClient;

    @MockitoBean
    private TicketIndicatorsRepository ticketIndicatorsRepository;

    @BeforeEach
    void stubExternalServices() {
        when(assetCatalogClient.findById(anyLong())).thenReturn(Optional.of(new AssetResponseDto(
                4L, "Portátil Dell Latitude 5440", "Equipo asignado al área contable", "Computadores", new BigDecimal("3200.00"))));
        when(employeeDirectoryClient.findById(anyLong())).thenReturn(Optional.of(new EmployeeDto(
                3L, "Clementine", "Bauch", "clementine." + STAMP + "@empresa.example.com", "14631234447")));
        when(ticketIndicatorsRepository.callIndicatorsProcedure()).thenReturn(new TicketIndicatorsResult(List.of(
                new IndicatorRow("STATUS", "OPEN", 4),
                new IndicatorRow("STATUS", "IN_PROGRESS", 2),
                new IndicatorRow("STATUS", "RESOLVED", 1),
                new IndicatorRow("CATEGORY", "Hardware", 5),
                new IndicatorRow("CATEGORY", "Redes", 2)
        ), new BigDecimal("6.50")));
    }

    // ---------------------------------------------------------------- utilidades

    private static RestDocumentationResultHandler doc(String identifier, Snippet... snippets) {
        return document(identifier,
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint(), modifyHeaders().remove("X-Correlation-ID")),
                snippets);
    }

    /** Sobre común de todas las respuestas (CustomResponse). */
    private static FieldDescriptor[] envelope(String dataDescription) {
        return new FieldDescriptor[]{
                fieldWithPath("status").description("SUCCESS, EMPTY o ERROR"),
                fieldWithPath("message").description("Mensaje legible del resultado"),
                fieldWithPath("timestamp").description("Fecha y hora de la respuesta (dd/MM/yyyy HH:mm:ss)"),
                subsectionWithPath("data").optional().description(dataDescription)
        };
    }

    private String json(Object body) {
        return jsonMapper.writeValueAsString(body);
    }

    private Long id(ResultActions result) throws Exception {
        return jsonMapper.readTree(result.andReturn().getResponse().getContentAsString()).get("data").get("id").asLong();
    }

    private static FieldDescriptor[] personFields() {
        return new FieldDescriptor[]{
                fieldWithPath("name").description("Nombre (obligatorio, máximo 120)"),
                fieldWithPath("surname").description("Apellido (obligatorio, máximo 120)"),
                fieldWithPath("email").description("Correo (obligatorio y único, máximo 150)"),
                fieldWithPath("phoneNumber").optional().type(JsonFieldType.STRING).description("Teléfono opcional: 7 a 15 dígitos, puede iniciar con + (@ValidPhone)")
        };
    }

    private static FieldDescriptor[] ticketFields() {
        return new FieldDescriptor[]{
                fieldWithPath("title").description("Título (obligatorio, máximo 150)"),
                fieldWithPath("description").description("Descripción (obligatoria, máximo 2000)"),
                fieldWithPath("requesterId").description("Id del solicitante (debe existir y estar activo)"),
                fieldWithPath("categoryId").description("Id de la categoría (debe existir y estar activa)"),
                fieldWithPath("supportAgentId").optional().type(JsonFieldType.NUMBER).description("Id del agente asignado (opcional; debe estar activo)"),
                fieldWithPath("priority").description("LOW, MEDIUM, HIGH o CRITICAL"),
                fieldWithPath("assetId").optional().type(JsonFieldType.NUMBER).description("Id del activo de TI en el catálogo externo (opcional)")
        };
    }

    private Map<String, Object> ticketBody(String title) {
        return Map.of("title", title, "description", "El equipo no enciende desde esta mañana",
                "requesterId", requesterId, "categoryId", categoryId, "supportAgentId", agentId,
                "priority", "HIGH", "assetId", 4);
    }

    // ---------------------------------------------------------------- categorías

    @Test
    @Order(1)
    void categoriesCreate() throws Exception {
        ResultActions result = mockMvc.perform(post(API + "/categories").header(USER, "jcastaneda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("name", "Impresoras " + STAMP, "description", "Impresoras y escáneres"))))
                .andExpect(status().isCreated())
                .andDo(doc("categories/create",
                        requestHeaders(headerWithName(USER).optional().description("Usuario para la auditoría; si no se envía, SYSTEM")),
                        requestFields(
                                fieldWithPath("name").description("Nombre (obligatorio y único, máximo 80)"),
                                fieldWithPath("description").optional().type(JsonFieldType.STRING).description("Descripción (máximo 250)")),
                        responseFields(envelope("Categoría creada, con su bloque de auditoría"))));
        categoryId = id(result);
    }

    @Test
    @Order(2)
    void categoriesList() throws Exception {
        mockMvc.perform(get(API + "/categories"))
                .andExpect(status().isOk())
                .andDo(doc("categories/list", responseFields(envelope("Categorías activas"))));
    }

    @Test
    @Order(3)
    void categoriesGet() throws Exception {
        mockMvc.perform(get(API + "/categories/{id}", categoryId))
                .andExpect(status().isOk())
                .andDo(doc("categories/get",
                        pathParameters(parameterWithName("id").description("Id de la categoría")),
                        responseFields(envelope("Categoría (incluye las inactivas)"))));
    }

    @Test
    @Order(4)
    void categoriesUpdate() throws Exception {
        mockMvc.perform(put(API + "/categories/{id}", categoryId).header(USER, "jcastaneda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("name", "Impresión " + STAMP, "description", "Impresoras, escáneres y plotters"))))
                .andExpect(status().isOk())
                .andDo(doc("categories/update",
                        pathParameters(parameterWithName("id").description("Id de la categoría")),
                        requestFields(
                                fieldWithPath("name").description("Nombre (obligatorio y único)"),
                                fieldWithPath("description").optional().type(JsonFieldType.STRING).description("Descripción")),
                        responseFields(envelope("Categoría actualizada"))));
    }

    @Test
    @Order(5)
    void categoriesDelete() throws Exception {
        Long temporary = id(mockMvc.perform(post(API + "/categories").contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("name", "Temporal " + STAMP)))));

        mockMvc.perform(delete(API + "/categories/{id}", temporary))
                .andExpect(status().isOk())
                .andDo(doc("categories/delete",
                        pathParameters(parameterWithName("id").description("Id de la categoría")),
                        responseFields(envelope("Sin datos"))));
    }

    // ---------------------------------------------------------------- solicitantes

    @Test
    @Order(10)
    void requestersCreate() throws Exception {
        ResultActions result = mockMvc.perform(post(API + "/requesters").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("name", "Ana", "surname", "Pérez", "email", "ana." + STAMP + "@empresa.example.com",
                                "phoneNumber", "+573001112233"))))
                .andExpect(status().isCreated())
                .andDo(doc("requesters/create", requestFields(personFields()), responseFields(envelope("Solicitante creado"))));
        requesterId = id(result);
    }

    @Test
    @Order(11)
    void requestersList() throws Exception {
        mockMvc.perform(get(API + "/requesters"))
                .andExpect(status().isOk())
                .andDo(doc("requesters/list", responseFields(envelope("Solicitantes activos"))));
    }

    @Test
    @Order(12)
    void requestersGet() throws Exception {
        mockMvc.perform(get(API + "/requesters/{id}", requesterId))
                .andExpect(status().isOk())
                .andDo(doc("requesters/get",
                        pathParameters(parameterWithName("id").description("Id del solicitante")),
                        responseFields(envelope("Solicitante"))));
    }

    @Test
    @Order(13)
    void requestersUpdate() throws Exception {
        mockMvc.perform(put(API + "/requesters/{id}", requesterId).contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("name", "Ana María", "surname", "Pérez", "email", "ana." + STAMP + "@empresa.example.com",
                                "phoneNumber", "3001112233"))))
                .andExpect(status().isOk())
                .andDo(doc("requesters/update",
                        pathParameters(parameterWithName("id").description("Id del solicitante")),
                        requestFields(personFields()), responseFields(envelope("Solicitante actualizado"))));
    }

    @Test
    @Order(14)
    void requestersDelete() throws Exception {
        Long temporary = id(mockMvc.perform(post(API + "/requesters").contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("name", "Temporal", "surname", "Prueba", "email", "temporal." + STAMP + "@empresa.example.com")))));

        mockMvc.perform(delete(API + "/requesters/{id}", temporary))
                .andExpect(status().isOk())
                .andDo(doc("requesters/delete",
                        pathParameters(parameterWithName("id").description("Id del solicitante")),
                        responseFields(envelope("Sin datos"))));
    }

    @Test
    @Order(15)
    void requestersFromDirectory() throws Exception {
        mockMvc.perform(post(API + "/requesters/from-directory/{employeeId}", 3))
                .andExpect(status().isCreated())
                .andDo(doc("requesters/from-directory",
                        pathParameters(parameterWithName("employeeId").description("Id del empleado en el directorio interno")),
                        responseFields(envelope("Solicitante creado con los datos del directorio"))));
    }

    // ---------------------------------------------------------------- agentes

    @Test
    @Order(20)
    void agentsCreate() throws Exception {
        ResultActions result = mockMvc.perform(post(API + "/support-agents").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("name", "Marta", "surname", "Ruiz", "email", "marta." + STAMP + "@soporte.example.com",
                                "phoneNumber", "3101112233"))))
                .andExpect(status().isCreated())
                .andDo(doc("support-agents/create", requestFields(personFields()), responseFields(envelope("Agente creado"))));
        agentId = id(result);
    }

    @Test
    @Order(21)
    void agentsList() throws Exception {
        mockMvc.perform(get(API + "/support-agents"))
                .andExpect(status().isOk())
                .andDo(doc("support-agents/list", responseFields(envelope("Agentes activos"))));
    }

    @Test
    @Order(22)
    void agentsGet() throws Exception {
        mockMvc.perform(get(API + "/support-agents/{id}", agentId))
                .andExpect(status().isOk())
                .andDo(doc("support-agents/get",
                        pathParameters(parameterWithName("id").description("Id del agente")),
                        responseFields(envelope("Agente"))));
    }

    @Test
    @Order(23)
    void agentsUpdate() throws Exception {
        mockMvc.perform(put(API + "/support-agents/{id}", agentId).contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("name", "Marta", "surname", "Ruiz Gómez", "email", "marta." + STAMP + "@soporte.example.com"))))
                .andExpect(status().isOk())
                .andDo(doc("support-agents/update",
                        pathParameters(parameterWithName("id").description("Id del agente")),
                        requestFields(personFields()), responseFields(envelope("Agente actualizado"))));
    }

    @Test
    @Order(24)
    void agentsDelete() throws Exception {
        Long temporary = id(mockMvc.perform(post(API + "/support-agents").contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("name", "Temporal", "surname", "Prueba", "email", "agente." + STAMP + "@soporte.example.com")))));

        mockMvc.perform(delete(API + "/support-agents/{id}", temporary))
                .andExpect(status().isOk())
                .andDo(doc("support-agents/delete",
                        pathParameters(parameterWithName("id").description("Id del agente")),
                        responseFields(envelope("Sin datos"))));
    }

    // ---------------------------------------------------------------- tickets

    @Test
    @Order(30)
    void ticketsCreate() throws Exception {
        ResultActions result = mockMvc.perform(post(API + "/tickets").header(USER, "ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(ticketBody("Portátil no enciende"))))
                .andExpect(status().isCreated())
                .andDo(doc("tickets/create", requestFields(ticketFields()),
                        responseFields(envelope("Ticket creado en OPEN con su número legible"))));
        ticketId = id(result);
        ticketNumber = jsonMapper.readTree(result.andReturn().getResponse().getContentAsString())
                .get("data").get("ticketNumber").asString();
    }

    @Test
    @Order(31)
    void ticketsList() throws Exception {
        mockMvc.perform(get(API + "/tickets").param("status", "OPEN").param("priority", "HIGH")
                        .param("page", "0").param("size", "5").param("sort", "id,desc"))
                .andExpect(status().isOk())
                .andDo(doc("tickets/list",
                        queryParameters(
                                parameterWithName("status").optional().description("Filtro por estado"),
                                parameterWithName("priority").optional().description("Filtro por prioridad"),
                                parameterWithName("categoryId").optional().description("Filtro por categoría"),
                                parameterWithName("supportAgentId").optional().description("Filtro por agente"),
                                parameterWithName("page").optional().description("Página, desde 0"),
                                parameterWithName("size").optional().description("Tamaño de página (por defecto 20)"),
                                parameterWithName("sort").optional().description("Orden, por ejemplo id,desc")),
                        responseFields(envelope("Página de tickets (content, page, size, totalElements, totalPages, first, last)"))));
    }

    @Test
    @Order(32)
    void ticketsGet() throws Exception {
        mockMvc.perform(get(API + "/tickets/{id}", ticketId))
                .andExpect(status().isOk())
                .andDo(doc("tickets/get",
                        pathParameters(parameterWithName("id").description("Id del ticket")),
                        responseFields(envelope("Ticket con solicitante, categoría, agente, activo, resolución y auditoría"))));
    }

    @Test
    @Order(33)
    void ticketsUpdate() throws Exception {
        mockMvc.perform(put(API + "/tickets/{id}", ticketId).contentType(MediaType.APPLICATION_JSON)
                        .content(json(ticketBody("Portátil no enciende (piso 3)"))))
                .andExpect(status().isOk())
                .andDo(doc("tickets/update",
                        pathParameters(parameterWithName("id").description("Id del ticket")),
                        requestFields(ticketFields()),
                        responseFields(envelope("Ticket actualizado; el estado no cambia"))));
    }

    @Test
    @Order(34)
    void ticketsChangeStatus() throws Exception {
        mockMvc.perform(patch(API + "/tickets/{id}/status", ticketId).header(USER, "marta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", "IN_PROGRESS", "reason", "Tomado por Marta"))))
                .andExpect(status().isOk())
                .andDo(doc("tickets/change-status",
                        pathParameters(parameterWithName("id").description("Id del ticket")),
                        requestFields(
                                fieldWithPath("status").description("Estado destino"),
                                fieldWithPath("reason").optional().type(JsonFieldType.STRING).description("Motivo; obligatorio como nota al pasar a RESOLVED y al cerrar sin resolver")),
                        responseFields(envelope("Ticket con el nuevo estado"))));

        mockMvc.perform(patch(API + "/tickets/{id}/status", ticketId).header(USER, "marta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", "RESOLVED", "reason", "Se reemplazó la batería"))))
                .andExpect(status().isOk());
    }

    @Test
    @Order(35)
    void ticketsInvalidTransition() throws Exception {
        mockMvc.perform(patch(API + "/tickets/{id}/status", ticketId).contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("status", "WAITING_FOR_REQUESTER"))))
                .andExpect(status().isConflict())
                .andDo(doc("errors/conflict", responseFields(envelope("null en los errores de negocio"))));
    }

    @Test
    @Order(36)
    void ticketsHistory() throws Exception {
        mockMvc.perform(get(API + "/tickets/{id}/status-history", ticketId))
                .andExpect(status().isOk())
                .andDo(doc("tickets/status-history",
                        pathParameters(parameterWithName("id").description("Id del ticket")),
                        responseFields(envelope("Cambios de estado con motivo, usuario y fecha"))));
    }

    @Test
    @Order(37)
    void commentsCreate() throws Exception {
        mockMvc.perform(post(API + "/tickets/{ticketId}/comments", ticketId).contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("comment", "Se pidió la batería de repuesto al proveedor"))))
                .andExpect(status().isCreated())
                .andDo(doc("comments/create",
                        pathParameters(parameterWithName("ticketId").description("Id del ticket")),
                        requestFields(fieldWithPath("comment").description("Texto del comentario (obligatorio, máximo 2000)")),
                        responseFields(envelope("Comentario creado"))));
    }

    @Test
    @Order(38)
    void commentsList() throws Exception {
        mockMvc.perform(get(API + "/tickets/{ticketId}/comments", ticketId))
                .andExpect(status().isOk())
                .andDo(doc("comments/list",
                        pathParameters(parameterWithName("ticketId").description("Id del ticket")),
                        responseFields(envelope("Comentarios del ticket"))));
    }

    // ---------------------------------------------------------------- consultas

    @Test
    @Order(40)
    void queriesByNumber() throws Exception {
        mockMvc.perform(get(API + "/tickets/number/{ticketNumber}", ticketNumber))
                .andExpect(status().isOk())
                .andDo(doc("queries/by-number",
                        pathParameters(parameterWithName("ticketNumber").description("Número legible, por ejemplo TCK-2026-000123")),
                        responseFields(envelope("Ticket completo (consulta JPQL con join fetch)"))));
    }

    @Test
    @Order(41)
    void queriesSearch() throws Exception {
        mockMvc.perform(get(API + "/tickets/search").param("text", "portátil"))
                .andExpect(status().isOk())
                .andDo(doc("queries/search",
                        queryParameters(parameterWithName("text").description("Texto a buscar en título y descripción (3 a 100 caracteres)")),
                        responseFields(envelope("Proyección: id, ticketNumber, title, status y priority"))));
    }

    @Test
    @Order(42)
    void queriesSummary() throws Exception {
        mockMvc.perform(get(API + "/tickets/summary").param("priority", "HIGH").param("size", "5"))
                .andExpect(status().isOk())
                .andDo(doc("queries/summary",
                        queryParameters(
                                parameterWithName("status").optional().description("Filtro por estado"),
                                parameterWithName("priority").optional().description("Filtro por prioridad"),
                                parameterWithName("page").optional().description("Página, desde 0"),
                                parameterWithName("size").optional().description("Tamaño de página"),
                                parameterWithName("sort").optional().description("Orden, por ejemplo ticketId,desc")),
                        responseFields(envelope("Página de la vista vw_ticket_summary"))));
    }

    @Test
    @Order(43)
    void queriesIndicators() throws Exception {
        mockMvc.perform(get(API + "/tickets/indicators"))
                .andExpect(status().isOk())
                .andDo(doc("queries/indicators",
                        responseFields(
                                fieldWithPath("status").description("SUCCESS"),
                                fieldWithPath("message").description("Mensaje del resultado"),
                                fieldWithPath("timestamp").description("Fecha y hora de la respuesta"),
                                fieldWithPath("data.totalTickets").description("Total de tickets activos"),
                                subsectionWithPath("data.byStatus").description("Cantidad por estado (todos los estados, en 0 si no hay)"),
                                subsectionWithPath("data.byCategory").description("Cantidad por categoría"),
                                fieldWithPath("data.averageResolutionHours").description("Horas promedio entre la creación y la resolución"))));
    }

    @Test
    @Order(44)
    void queriesAsset() throws Exception {
        mockMvc.perform(get(API + "/tickets/{id}/asset", ticketId))
                .andExpect(status().isOk())
                .andDo(doc("queries/asset",
                        pathParameters(parameterWithName("id").description("Id del ticket")),
                        responseFields(
                                fieldWithPath("status").description("SUCCESS"),
                                fieldWithPath("message").description("Mensaje del resultado"),
                                fieldWithPath("timestamp").description("Fecha y hora de la respuesta"),
                                fieldWithPath("data.id").description("Id del activo en el catálogo externo"),
                                fieldWithPath("data.name").description("Nombre del activo"),
                                fieldWithPath("data.description").description("Descripción"),
                                fieldWithPath("data.category").description("Categoría del activo"),
                                fieldWithPath("data.referencePrice").description("Precio de referencia"))));
    }

    // ---------------------------------------------------------------- portal

    @Test
    @Order(50)
    void portalTicket() throws Exception {
        mockMvc.perform(get(API + "/portal/tickets/{ticketNumber}", ticketNumber).header("X-API-Key", "demo-portal-key"))
                .andExpect(status().isOk())
                .andDo(doc("portal/ticket",
                        requestHeaders(headerWithName("X-API-Key").description("Clave del portal (PORTAL_API_KEY)")),
                        pathParameters(parameterWithName("ticketNumber").description("Número legible (@ValidTicketNumber)")),
                        responseFields(
                                fieldWithPath("status").description("SUCCESS"),
                                fieldWithPath("message").description("Mensaje del resultado"),
                                fieldWithPath("timestamp").description("Fecha y hora de la respuesta"),
                                fieldWithPath("data.ticketNumber").description("Número del ticket"),
                                fieldWithPath("data.title").description("Título"),
                                fieldWithPath("data.status").description("Estado"),
                                fieldWithPath("data.categoryName").description("Categoría"),
                                fieldWithPath("data.createdAt").description("Fecha de creación"),
                                fieldWithPath("data.resolvedAt").optional().description("Fecha de resolución"),
                                fieldWithPath("data.closedAt").optional().description("Fecha de cierre"),
                                fieldWithPath("data.resolutionNote").optional().description("Nota de resolución"))));
    }

    @Test
    @Order(51)
    void portalWithoutKey() throws Exception {
        mockMvc.perform(get(API + "/portal/tickets/{ticketNumber}", ticketNumber))
                .andExpect(status().isUnauthorized())
                .andDo(doc("errors/unauthorized", responseFields(envelope("null"))));
    }

    // ---------------------------------------------------------------- errores comunes

    @Test
    @Order(60)
    void validationError() throws Exception {
        mockMvc.perform(post(API + "/requesters").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("name", "", "surname", "Pérez", "email", "no-es-correo", "phoneNumber", "300-ABC"))))
                .andExpect(status().isBadRequest())
                .andDo(doc("errors/validation", responseFields(envelope("Mapa campo → mensaje de error"))));
    }

    @Test
    @Order(61)
    void notFound() throws Exception {
        mockMvc.perform(get(API + "/tickets/{id}", 999999))
                .andExpect(status().isNotFound())
                .andDo(doc("errors/not-found", responseFields(envelope("null"))));
    }

    // ---------------------------------------------------------------- batch

    @Test
    @Order(70)
    void batchRun() throws Exception {
        mockMvc.perform(post(API + "/batch/audit-history").header(USER, "jcastaneda"))
                .andExpect(status().isOk())
                .andDo(doc("batch/run",
                        responseFields(
                                fieldWithPath("status").description("SUCCESS"),
                                fieldWithPath("message").description("Mensaje del resultado"),
                                fieldWithPath("timestamp").description("Fecha y hora de la respuesta"),
                                fieldWithPath("data.executionId").description("Id de la ejecución en las tablas de Spring Batch"),
                                fieldWithPath("data.status").description("COMPLETED o FAILED"),
                                fieldWithPath("data.requestedBy").description("Usuario que lanzó el volcado (X-User-Id)"),
                                fieldWithPath("data.fromOccurredAt").description("Fecha desde la que se leyeron eventos"),
                                fieldWithPath("data.chunkSize").description("Tamaño de bloque (tabla batch_job_config)"),
                                fieldWithPath("data.readCount").description("Eventos leídos de Oracle"),
                                fieldWithPath("data.alreadyCopiedCount").description("Eventos descartados porque ya estaban en el histórico"),
                                fieldWithPath("data.writeCount").description("Eventos nuevos escritos en PostgreSQL"),
                                fieldWithPath("data.chunkCount").description("Bloques procesados"),
                                fieldWithPath("data.durationMillis").description("Duración en milisegundos"))));
    }

    @Test
    @Order(71)
    void batchSummary() throws Exception {
        mockMvc.perform(get(API + "/batch/audit-history/summary"))
                .andExpect(status().isOk())
                .andDo(doc("batch/summary",
                        responseFields(
                                fieldWithPath("status").description("SUCCESS"),
                                fieldWithPath("message").description("Mensaje del resultado"),
                                fieldWithPath("timestamp").description("Fecha y hora de la respuesta"),
                                fieldWithPath("data.totalEvents").description("Eventos en el histórico"),
                                fieldWithPath("data.lastOccurredAt").optional().description("Fecha del último evento copiado"),
                                subsectionWithPath("data.byCategory").description("Cantidad por categoría (HTTP_REQUEST, BUSINESS_EVENT)"),
                                subsectionWithPath("data.byEventType").description("Cantidad por tipo de evento"))));
    }
}
