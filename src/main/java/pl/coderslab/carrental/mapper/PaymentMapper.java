package pl.coderslab.carrental.mapper;

import org.springframework.stereotype.Component;
import pl.coderslab.carrental.dto.PaymentDto;
import pl.coderslab.carrental.model.Payment;

@Component
public class PaymentMapper {

    public PaymentDto toDto(Payment payment) {

        return PaymentDto.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .reservationId(payment.getReservation().getId())
                .build();
    }

    public Payment toEntity(PaymentDto paymentDto) {

        return Payment.builder()
                .id(paymentDto.getId())
                .amount(paymentDto.getAmount())
                .paymentMethod(paymentDto.getPaymentMethod())
                .paymentStatus(paymentDto.getPaymentStatus())
                .build();
    }
}
