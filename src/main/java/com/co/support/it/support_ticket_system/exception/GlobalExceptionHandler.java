package com.co.support.it.support_ticket_system.exception;

import com.co.support.it.support_ticket_system.business.category.exception.CategoryNotFoundException;
import com.co.support.it.support_ticket_system.business.requester.exception.RequesterNotFoundException;
import com.co.support.it.support_ticket_system.business.supportagent.exception.SupportAgentNotFoundException;
import com.co.support.it.support_ticket_system.business.ticket.exception.TicketNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Valor no permitido en un parámetro de la URL, por ejemplo ?status=APROBADO.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleArgumentTypeMismatch(
            MethodArgumentTypeMismatchException exception
    ) {
        Class<?> requiredType = exception.getRequiredType();
        String message = requiredType != null && requiredType.isEnum()
                ? invalidEnumMessage(exception.getValue(), requiredType)
                : "El valor '" + exception.getValue() + "' no tiene un formato válido";

        return ResponseEntity.badRequest()
                .body(Map.of(exception.getName(), message));
    }

    /**
     * Cuerpo JSON que no se puede leer, por ejemplo "priority": "URGENTE".
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleMessageNotReadable(
            HttpMessageNotReadableException exception
    ) {
        Throwable cause = exception.getCause();

        while (cause != null) {
            if (cause instanceof InvalidFormatException invalidFormat
                    && invalidFormat.getTargetType() != null
                    && invalidFormat.getTargetType().isEnum()) {

                String field = invalidFormat.getPath().isEmpty()
                        ? "body"
                        : invalidFormat.getPath()
                                .get(invalidFormat.getPath().size() - 1)
                                .getPropertyName();

                return ResponseEntity.badRequest().body(Map.of(
                        field,
                        invalidEnumMessage(invalidFormat.getValue(), invalidFormat.getTargetType())
                ));
            }
            cause = cause.getCause();
        }

        return ResponseEntity.badRequest()
                .body(Map.of("message", "El cuerpo de la solicitud no tiene un formato JSON válido"));
    }

    private String invalidEnumMessage(Object value, Class<?> enumType) {
        String allowedValues = Arrays.stream(enumType.getEnumConstants())
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        return "El valor '" + value + "' no es válido. Valores permitidos: " + allowedValues;
    }

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleTicketNotFound(
            TicketNotFoundException exception
    ){
        Map<String,String> response = Map.of(
                "message",
                exception.getMessage()
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidacionErrors(
            MethodArgumentNotValidException exception
    ){
        Map<String, String> errors = new LinkedHashMap<>();

        for(var fieldError: exception.getBindingResult().getFieldErrors()){
            errors.put(fieldError.getField(),fieldError.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCategoryNotFound(
            CategoryNotFoundException exception
    ) {
        Map<String, String> response = Map.of(
                "message",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }
    @ExceptionHandler(RequesterNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleRequesterNotFound(
            RequesterNotFoundException exception
    ) {
        Map<String, String> response = Map.of(
                "message",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }
    @ExceptionHandler(SupportAgentNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleSupportAgentNotFound(
            SupportAgentNotFoundException exception
    ) {
        Map<String, String> response = Map.of(
                "message",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }
}
