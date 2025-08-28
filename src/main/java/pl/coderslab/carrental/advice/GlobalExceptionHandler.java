package pl.coderslab.carrental.advice;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.coderslab.carrental.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ErrorResponse createResponse(HttpStatusCode status, Exception exception) {

        return ErrorResponse.builder()
                .status(status.value())
                .message(exception.getMessage())
                .build();
    }
}

