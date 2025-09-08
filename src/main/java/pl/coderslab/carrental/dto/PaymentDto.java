package pl.coderslab.carrental.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.coderslab.carrental.model.enum_package.PaymentMethod;
import pl.coderslab.carrental.model.enum_package.PaymentStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
