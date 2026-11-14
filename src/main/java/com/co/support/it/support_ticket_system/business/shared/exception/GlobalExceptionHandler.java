package com.co.support.it.support_ticket_system.business.shared.exception;

import com.co.support.it.support_ticket_system.generic.controller.response.CustomResponse;
import com.co.support.it.support_ticket_system.generic.exception.BadRequestException;
import com.co.support.it.support_ticket_system.generic.exception.BusinessRuleException;
import com.co.support.it.support_ticket_system.generic.exception.DuplicateException;
import com.co.support.it.support_ticket_system.generic.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.co.support.it.support_ticket_system.generic.controller.constants.MessageConstants.*;

/**
 * Traduce las excepciones de la aplicación a respuestas HTTP con formato {@link CustomResponse}.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponse<Map<String, String>>> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));

        return build(HttpStatus.BAD_REQUEST, VALIDATION_MESSAGE, errors);
    }

    /**
     * Valor no permitido en un parámetro de la URL, por ejemplo ?status=APROBADO.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CustomResponse<Map<String, String>>> handleArgumentTypeMismatch(
            MethodArgumentTypeMismatchException exception
    ) {
        Class<?> requiredType = exception.getRequiredType();
        String detail = requiredType != null && requiredType.isEnum()
                ? invalidEnumMessage(exception.getValue(), requiredType)
                : "El valor '" + exception.getValue() + "' no tiene un formato válido";

        return build(HttpStatus.BAD_REQUEST, VALIDATION_MESSAGE, Map.of(exception.getName(), detail));
    }

    /**
     * Cuerpo JSON ilegible o con un valor no permitido, por ejemplo "priority": "URGENTE".
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomResponse<Map<String, String>>> handleMessageNotReadable(
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

                return build(HttpStatus.BAD_REQUEST, VALIDATION_MESSAGE, Map.of(
                        field,
                        invalidEnumMessage(invalidFormat.getValue(), invalidFormat.getTargetType())
                ));
            }
            cause = cause.getCause();
        }

        return build(HttpStatus.BAD_REQUEST, INVALID_BODY_MESSAGE, null);
    }

    /**
     * Falta un dato que la operación exige según el contexto, por ejemplo la nota al resolver un ticket.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<CustomResponse<Void>> handleBadRequest(BadRequestException exception) {
        return build(HttpStatus.BAD_REQUEST, exception.getMessage(), null);
    }

    @ExceptionHandler({NotFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<CustomResponse<Void>> handleNotFound(Exception exception) {
        return build(HttpStatus.NOT_FOUND, exception.getMessage(), null);
    }

    @ExceptionHandler({DuplicateException.class, BusinessRuleException.class})
    public ResponseEntity<CustomResponse<Void>> handleConflict(RuntimeException exception) {
        return build(HttpStatus.CONFLICT, exception.getMessage(), null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CustomResponse<Void>> handleMethodNotSupported() {
        return build(HttpStatus.METHOD_NOT_ALLOWED, METHOD_NOT_ALLOWED_MESSAGE, null);
    }

    /**
     * Cualquier error no previsto: se registra completo en el log y se responde sin detalles internos.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse<Void>> handleUnexpected(Exception exception) {
        log.error("Error no controlado", exception);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_ERROR_MESSAGE, null);
    }

    private <T> ResponseEntity<CustomResponse<T>> build(HttpStatus status, String message, T data) {
        return ResponseEntity.status(status).body(CustomResponse.error(message, data));
    }

    private String invalidEnumMessage(Object value, Class<?> enumType) {
        String allowedValues = Arrays.stream(enumType.getEnumConstants())
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        return "El valor '" + value + "' no es válido. Valores permitidos: " + allowedValues;
    }
}
