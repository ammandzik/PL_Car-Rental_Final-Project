package pl.coderslab.carrental.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class InvoiceDto {

    private Long id;
    private String invoiceNumber;
    @PastOrPresent
    private LocalDate issueDate;
    private Double totalAmount;
    @NotNull
    private Long reservationId;
    @NotNull
    private Long userId;
}
