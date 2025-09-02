package pl.coderslab.carrental.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewDto {

    private Long id;
    @NotBlank(message = "Field must contain pseudonym")
    @NotNull(message = "Pseudonym must not be null")
    @Size(min = 4, max = 20)
    private String pseudonym;
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be maximum 5")
    private int rating;
    @NotBlank(message = "Description should not be blank")
    @NotNull(message = "Description should not be null")
    @Size(min = 10, max = 150)
    private String description;
    @NotNull(message = "Reservation id must not be null")
    private Long reservationId;
}
