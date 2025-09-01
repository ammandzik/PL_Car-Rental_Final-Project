package pl.coderslab.carrental.dto;

import lombok.Builder;
import lombok.Data;
import pl.coderslab.carrental.model.enum_package.PaymentMethod;
import pl.coderslab.carrental.model.enum_package.PaymentStatus;

@Data
@Builder
public class PaymentDto {

    private Long id;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private Double amount;
    private Long reservationId;
}
