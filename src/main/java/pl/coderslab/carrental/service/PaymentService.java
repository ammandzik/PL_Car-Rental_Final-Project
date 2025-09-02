package pl.coderslab.carrental.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.carrental.dto.CarDto;
import pl.coderslab.carrental.dto.PaymentDto;
import pl.coderslab.carrental.mapper.PaymentMapper;
import pl.coderslab.carrental.mapper.ReservationMapper;
import pl.coderslab.carrental.model.enum_package.CarStatus;
import pl.coderslab.carrental.model.enum_package.PaymentMethod;
import pl.coderslab.carrental.model.enum_package.PaymentStatus;
import pl.coderslab.carrental.repository.PaymentRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationService reservationService;
    private final PaymentMapper paymentMapper;
    private final ReservationMapper reservationMapper;

    public List<PaymentDto> getAllPayments() {

        return paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    public List<PaymentDto> getPaymentsWithFilters(PaymentStatus status, PaymentMethod method, Long reservation) {

        log.info("Invoked get all payments with filters");

        return paymentRepository.getPaymentsByStatusOrMethodOrReservation(status, method, reservation)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }


    @Transactional
    public PaymentDto save(PaymentDto paymentDto) {

        log.info("Invoked save payment method");

        if (paymentDto != null) {

            var reservation = reservationService.findById(paymentDto.getReservationId());

            paymentDto.setAmount(reservation.getFinalPrice());

            var entity = paymentMapper.toEntity(paymentDto);

            if (paymentDto.getPaymentStatus().equals(PaymentStatus.APPROVED)) {
                reservation.setConfirmed(true);
                reservationService.save(reservation);
            }
            entity.setReservation(reservationMapper.toEntity(reservation));

            return paymentMapper.toDto(paymentRepository.save(entity));
        } else {
            throw new IllegalArgumentException("Payment cannot be null");
        }
    }
}
