package pl.coderslab.carrental.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ReservationDto {

    private Long id;
    @NotNull
    @FutureOrPresent
    private LocalDate dateFrom;
    @NotNull
    @Future
    private LocalDate dateTo;
    private Double finalPrice;
    private boolean confirmed;
    @NotNull
    private Long carId;
    @NotNull
    private Long userId;
}
