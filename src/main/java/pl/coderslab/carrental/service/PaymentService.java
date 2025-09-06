package pl.coderslab.carrental.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.carrental.dto.PaymentDto;
import pl.coderslab.carrental.dto.ReservationDto;
import pl.coderslab.carrental.mapper.PaymentMapper;
import pl.coderslab.carrental.mapper.ReservationMapper;
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

    public PaymentDto updateCancelledPayment(Long id) {

        log.info("Invoked update payment");

        var payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Payment not found with id %s", id)));

        var reservation = reservationService.findById(payment.getReservation().getId());

        reservation.setConfirmed(false);
        reservationService.update(payment.getReservation().getId(), reservation);

        payment.setPaymentStatus(PaymentStatus.FUNDS_BEING_REFUNDED);
        return paymentMapper.toDto(paymentRepository.save(payment));
    }


    @Transactional
    public PaymentDto save(PaymentDto paymentDto) {

        log.info("Invoked save payment method");

        if (paymentDto != null) {

            var reservation = reservationService.findById(paymentDto.getReservationId());

            checkIfApprovedPaymentAlreadyExists(paymentDto, reservation);
            checkIfAskedForRefund(paymentDto, reservation);

            paymentDto.setAmount(reservation.getFinalPrice());
            var entity = paymentMapper.toEntity(paymentDto);

            checkIfPaymentApprovedAndChangeReservationStatus(reservation.getId(), paymentDto, reservation);
            entity.setReservation(reservationMapper.toEntity(reservation));

            return paymentMapper.toDto(paymentRepository.save(entity));
        } else {
            throw new IllegalArgumentException("Payment cannot be null");
        }
    }

    private void checkIfAskedForRefund(PaymentDto paymentDto, ReservationDto reservation) {
        if(paymentRepository.existsByRefundStatusAndReservationId(PaymentStatus.FUNDS_BEING_REFUNDED, reservation.getId())){
            throw new EntityExistsException(String.format("Payment already exists with reservation id %s and the funds are being returned. " +
                                                          "Therefore, payment cannot be processed again.", paymentDto.getReservationId()));
        }
    }

    private void checkIfApprovedPaymentAlreadyExists(PaymentDto paymentDto, ReservationDto reservation) {
        if (reservation.isConfirmed()) {
            throw new EntityExistsException(String.format("Approved payment already exists with reservation id %s", paymentDto.getReservationId()));
        }
    }

    private void checkIfPaymentApprovedAndChangeReservationStatus(Long reservationId, PaymentDto paymentDto, ReservationDto reservation) {
        if (paymentDto.getPaymentStatus().equals(PaymentStatus.APPROVED)) {
            reservation.setConfirmed(true);
            reservationService.update(reservationId, reservation);
        }
    }
}
