package com.co.support.it.support_ticket_system.generic.controller;

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

    @GetMapping
    ResponseEntity<CustomResponse<List<RESPONSE>>> findAll();

    @GetMapping(BY_ID)
    ResponseEntity<CustomResponse<RESPONSE>> findById(@PathVariable("id") ID id);

    @PostMapping
    ResponseEntity<CustomResponse<RESPONSE>> save(@Valid @RequestBody REQUEST request);

    @PutMapping(BY_ID)
    ResponseEntity<CustomResponse<RESPONSE>> update(
            @PathVariable("id") ID id,
            @Valid @RequestBody REQUEST request
    );

    @DeleteMapping(BY_ID)
    ResponseEntity<CustomResponse<Void>> delete(@PathVariable("id") ID id);
}
