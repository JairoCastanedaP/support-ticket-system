package com.co.support.it.support_ticket_system.generic.controller;

import com.co.support.it.support_ticket_system.generic.controller.response.CustomResponse;
import com.co.support.it.support_ticket_system.generic.controller.response.PageResponse;
import com.co.support.it.support_ticket_system.generic.controller.response.ResponseStatus;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;

import static com.co.support.it.support_ticket_system.generic.controller.constants.MessageConstants.*;

/**
 * Clase base de los controllers: construye las respuestas estándar {@link CustomResponse}.
 */
public abstract class GenericControllerImpl {

    protected <T> ResponseEntity<CustomResponse<List<T>>> returnList(List<T> list) {
        if (list.isEmpty()) {
            return ResponseEntity.ok(CustomResponse.empty(EMPTY_MESSAGE));
        }
        return ResponseEntity.ok(CustomResponse.success(SUCCESS_MESSAGE, list));
    }

    protected <T> ResponseEntity<CustomResponse<PageResponse<T>>> returnPage(Page<T> page) {
        if (page.isEmpty()) {
            return ResponseEntity.ok(new CustomResponse<>(
                    ResponseStatus.EMPTY,
                    EMPTY_MESSAGE,
                    PageResponse.of(page)
            ));
        }
        return ResponseEntity.ok(CustomResponse.success(SUCCESS_MESSAGE, PageResponse.of(page)));
    }

    protected <T> ResponseEntity<CustomResponse<T>> returnOk(T data) {
        return ResponseEntity.ok(CustomResponse.success(SUCCESS_MESSAGE, data));
    }

    protected <T> ResponseEntity<CustomResponse<T>> returnCreated(String location, T data) {
        return ResponseEntity.created(URI.create(location))
                .body(CustomResponse.success(CREATED_MESSAGE, data));
    }

    protected <T> ResponseEntity<CustomResponse<T>> returnUpdated(T data) {
        return ResponseEntity.ok(CustomResponse.success(UPDATED_MESSAGE, data));
    }

    protected ResponseEntity<CustomResponse<Void>> returnDeleted() {
        return ResponseEntity.ok(CustomResponse.success(DELETED_MESSAGE, null));
    }
}
