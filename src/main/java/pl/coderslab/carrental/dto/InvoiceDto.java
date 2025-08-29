package pl.coderslab.carrental.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class InvoiceDto {

    private Long id;
    private String invoiceNumber;
    private LocalDate issueDate;
    private Long totalAmount;
    private Long reservationId;
    private Long userId;
}
