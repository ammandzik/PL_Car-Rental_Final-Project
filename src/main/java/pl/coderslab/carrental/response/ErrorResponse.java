package pl.coderslab.carrental.response;

import lombok.Builder;

@Builder
public record ErrorResponse(
        int statusCode,
        String message
) {}
