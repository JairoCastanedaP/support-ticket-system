package com.co.support.it.support_ticket_system.generic.controller;

import io.swagger.v3.oas.annotations.Operation;

import com.co.support.it.support_ticket_system.generic.controller.response.CustomResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.co.support.it.support_ticket_system.business.shared.constants.APIEndPoints.BY_ID;

/**
 * Contrato REST CRUD genérico. Las implementaciones heredan los mappings declarados aquí.
 */
public interface GenericController<REQUEST, RESPONSE, ID> {

    @Operation(summary = "Listar registros activos", description = "Devuelve los registros activos; los eliminados lógicamente no aparecen. Si no hay registros, status = EMPTY.")
    @GetMapping
    ResponseEntity<CustomResponse<List<RESPONSE>>> findAll();

    @Operation(summary = "Consultar por id", description = "Incluye registros inactivos (eliminados lógicamente). 404 si no existe.")
    @GetMapping(BY_ID)
    ResponseEntity<CustomResponse<RESPONSE>> findById(@PathVariable("id") ID id);

    @Operation(summary = "Crear", description = "Responde 201 con el header Location. 400 si los datos no son válidos; 409 si el valor único (nombre o correo) ya existe.")
    @PostMapping
    ResponseEntity<CustomResponse<RESPONSE>> save(@Valid @RequestBody REQUEST request);

    @Operation(summary = "Actualizar", description = "404 si no existe; 400 si los datos no son válidos; 409 si el valor único ya lo usa otro registro.")
    @PutMapping(BY_ID)
    ResponseEntity<CustomResponse<RESPONSE>> update(
            @PathVariable("id") ID id,
            @Valid @RequestBody REQUEST request
    );

    @Operation(summary = "Eliminar (borrado lógico)", description = "Marca el registro como inactivo y responde 200. Deja de aparecer en el listado.")
    @DeleteMapping(BY_ID)
    ResponseEntity<CustomResponse<Void>> delete(@PathVariable("id") ID id);
}
