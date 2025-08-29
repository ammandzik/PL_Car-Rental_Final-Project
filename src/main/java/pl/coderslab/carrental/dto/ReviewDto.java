package pl.coderslab.carrental.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewDto {

    private Long id;
    private String pseudonym;
    private int rating;
    private String description;
    private Long reservationId;
}
