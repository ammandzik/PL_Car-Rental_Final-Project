package pl.coderslab.carrental.advice;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import pl.coderslab.carrental.response.ErrorResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalStateException(IllegalStateException exception) {

        log.error("Handling illegal state exception exception: {}", exception.getMessage());

        return createResponse(HttpStatus.BAD_REQUEST, exception);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException exception) {

        log.error("Handling illegal argument exception exception: {}", exception.getMessage());

        return createResponse(HttpStatus.BAD_REQUEST, exception);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException exception) {

        log.error("Handling entity not found exception: {}", exception.getMessage());
        return createResponse(HttpStatus.NOT_FOUND, exception);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {

        log.error("Handling method argument type mismatch exception: {}", exception.getMessage());

        return createResponse(HttpStatus.BAD_REQUEST, exception);
    }

    private ErrorResponse createResponse(HttpStatusCode status, Exception exception) {

        return ErrorResponse.builder()
                .statusCode(status.value())
                .message(exception.getMessage())
                .build();
    }
}

