package pl.coderslab.carrental.dto;

import pl.coderslab.carrental.model.enum_package.PaymentMethod;
import pl.coderslab.carrental.model.enum_package.PaymentStatus;

public class PaymentDto {

    private Long id;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private Long amount;
    private Long reservationId;
}
