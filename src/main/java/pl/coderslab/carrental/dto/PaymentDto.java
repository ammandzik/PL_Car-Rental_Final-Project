package pl.coderslab.carrental.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import pl.coderslab.carrental.model.enum_package.PaymentMethod;
import pl.coderslab.carrental.model.enum_package.PaymentStatus;

@Data
@Builder
public class PaymentDto {

    private Long id;
    @NotNull
    private PaymentMethod paymentMethod;
    @NotNull
    private PaymentStatus paymentStatus;
    private Double amount;
    @NotNull
    private Long reservationId;
}
