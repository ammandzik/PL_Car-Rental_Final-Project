package pl.coderslab.carrental.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ReservationDto {

    private Long id;
    private LocalDate date_from;
    private LocalDate date_to;
    private boolean confirmed;
    private Long carId;
    private Long userId;
}
