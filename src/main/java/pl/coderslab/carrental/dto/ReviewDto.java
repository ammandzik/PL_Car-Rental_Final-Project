package pl.coderslab.carrental.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewDto {

    private Long id;
    @NotBlank
    @NotNull
    @Size(min = 3, max = 30)
    private String pseudonym;
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be maximum 5")
    private int rating;
    @NotBlank
    @NotNull
    @Size(min = 10, max = 150)
    private String description;
    @NotNull
    private Long reservationId;
}
