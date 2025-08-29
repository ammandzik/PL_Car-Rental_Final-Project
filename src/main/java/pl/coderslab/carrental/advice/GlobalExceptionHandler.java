package pl.coderslab.carrental.advice;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import pl.coderslab.carrental.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleEntityNotFoundException(IllegalStateException exception) {

        return createResponse(HttpStatus.BAD_REQUEST, exception);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException exception) {

        return createResponse(HttpStatus.NOT_FOUND, exception);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {

        return createResponse(HttpStatus.BAD_REQUEST, exception);
    }

    private ErrorResponse createResponse(HttpStatusCode status, Exception exception) {

        return ErrorResponse.builder()
                .statusCode(status.value())
                .message(exception.getMessage())
                .build();
    }
}

