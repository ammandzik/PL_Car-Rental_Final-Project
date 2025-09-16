package pl.coderslab.carrental.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.carrental.dto.PaymentDto;
import pl.coderslab.carrental.exception.PaymentEditionException;
import pl.coderslab.carrental.mapper.PaymentMapper;
import pl.coderslab.carrental.mapper.ReservationMapper;
import pl.coderslab.carrental.model.Payment;
import pl.coderslab.carrental.model.enum_package.PaymentMethod;
import pl.coderslab.carrental.model.enum_package.PaymentStatus;
import pl.coderslab.carrental.repository.PaymentRepository;

import java.time.LocalDate;
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

    @Cacheable(value = "payment", key = "#id")
    public PaymentDto getPaymentById(Long id) {

        return paymentRepository.findById(id)
                .map(paymentMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Payment with id %s not found", id)));
    }

    public List<PaymentDto> getPaymentsWithFilters(PaymentStatus status, PaymentMethod method, Long reservation) {

        log.info("Invoked get all payments with filters");

        return paymentRepository.getPaymentsByStatusOrMethodOrReservation(status, method, reservation)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Transactional
    @CachePut(value = "payment", key = "#id")
    public PaymentDto updatePaymentStatus(Long id, PaymentStatus status) {

        log.info("Invoked update payment");

        var today = LocalDate.now();
        var payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Payment was not found with id %s", id)));

        checkIfNotPastDateAndCancelledStatus(payment.getReservation().getId(), today);
        checkIfPaymentStatusCouldBeChanged(payment, status, id);
        changePaymentStatusAccordingly(payment, status, today, id);

        paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }

    @Transactional
    public PaymentDto save(PaymentDto paymentDto) {

        log.info("Invoked save payment method");

        if (paymentDto != null) {

            var today = LocalDate.now();

            var reservation = reservationService.findById(paymentDto.getReservationId());

            checkIfNotPastDateAndCancelledStatus(reservation.getId(), today);
            checkIfPaymentIsAwaiting(reservation.getId());
            checkIfApprovedPaymentAlreadyExists(reservation.getId());
            checkIfAskedForRefund(reservation.getId());

            paymentDto.setAmount(reservation.getFinalPrice());
            var entity = paymentMapper.toEntity(paymentDto);

            checkIfPaymentApprovedAndChangeReservationStatus(reservation.getId(), paymentDto);
            entity.setReservation(reservationMapper.toEntity(reservation));

            return paymentMapper.toDto(paymentRepository.save(entity));
        } else {
            throw new IllegalArgumentException("Payment cannot be null");
        }
    }

    private void checkIfPaymentIsAwaiting(Long reservationId) {

        log.info("Invoked checkIfPaymentIsAwaiting payment method");

        if (paymentRepository.existsByStatusAndReservationId(PaymentStatus.AWAITING, reservationId)) {
            throw new EntityExistsException(String.format("Payment with reservation id %s already exists and has awaiting status. Processing new payment is not allowed.", reservationId));
        }

    }

    private void checkIfAskedForRefund(Long reservationId) {

        log.info("Invoked checkIfAskedForRefund method");

        if (paymentRepository.existsByStatusAndReservationId(PaymentStatus.FUNDS_BEING_REFUNDED, reservationId)
            || paymentRepository.existsByStatusAndReservationId(PaymentStatus.FUNDS_PAID_BACK, reservationId)) {
            throw new EntityExistsException(String.format("Payment already exists with reservation id %s and the funds are being returned. " +
                                                          "Therefore, payment cannot be processed again.", reservationId));
        }
    }

    private void checkIfApprovedPaymentAlreadyExists(Long reservationId) {

        log.info("Invoked checkIfApprovedPaymentAlreadyExists");

        if (paymentRepository.existsByStatusAndReservationId(PaymentStatus.APPROVED, reservationId)) {
            throw new EntityExistsException(String.format("Payment with reservation id %s already exists and has been approved. " +
                                                          "Processing another payment is not allowed.", reservationId));
        }
    }

    private void checkIfPaymentApprovedAndChangeReservationStatus(Long reservationId, PaymentDto paymentDto) {

        log.info("Invoked checkIfPaymentApprovedAndChangeReservationStatus method");

        if (paymentDto.getPaymentStatus().equals(PaymentStatus.APPROVED)) {
            reservationService.updateStatus(reservationId, true);
        }
    }

    private void checkIfNotPastDateAndCancelledStatus(Long reservationId, LocalDate today) {

        log.info("Invoked checkIfNotPastDateAndCancelledStatus method");

        if (paymentRepository.hasPastDateAndCancelledStatus(reservationId, today)) {
            throw new PaymentEditionException(String.format("Payment with reservation id %s is already cancelled and reservation date is in the past.", reservationId));
        }
    }

    private void checkIfPaymentStatusCouldBeChanged(Payment payment, PaymentStatus status, Long id) {

        log.info("Invoked checkIfPaymentStatusCouldBeChanged method");

        if (payment.getPaymentStatus().equals(PaymentStatus.APPROVED) && !status.equals(PaymentStatus.FUNDS_BEING_REFUNDED) ||
            payment.getPaymentStatus().equals(PaymentStatus.FUNDS_BEING_REFUNDED) && !status.equals(PaymentStatus.FUNDS_PAID_BACK) ||
            payment.getPaymentStatus().equals(PaymentStatus.AWAITING) && !(status.equals(PaymentStatus.APPROVED) || status.equals(PaymentStatus.CANCELLED)) ||
            payment.getPaymentStatus().equals(PaymentStatus.FUNDS_PAID_BACK) ||
            payment.getPaymentStatus().equals(PaymentStatus.CANCELLED)) {

            throw new PaymentEditionException(String.format("Payment with id %s is already in %s status and it's status cannot be changed to %s ",
                    id, payment.getPaymentStatus().getDescription(), status.getDescription()));
        }
    }

    private void changePaymentStatusAccordingly(Payment payment, PaymentStatus status, LocalDate today, Long id) {

        log.info("Invoked changePaymentStatusAccordingly method");

        if (payment.getPaymentStatus().equals(PaymentStatus.APPROVED)) {

            log.info("Payment with id {} is approved and about to be cancelled. Changing reservation and payment status.", id);

            reservationService.updateStatus(payment.getReservation().getId(), false);
            payment.setPaymentStatus(status);
            payment.setRefundDate(today);

        } else if (status.equals(PaymentStatus.APPROVED)) {

            log.info("Payment status is being changed to Approved. Changing reservation and payment status.");
            reservationService.updateStatus(payment.getReservation().getId(), true);
            payment.setPaymentStatus(status);

        } else {
            log.info("Changing payment status.");
            payment.setPaymentStatus(status);
        }
    }

    @Transactional
    public void updateStatusOfAllWithPastDateAndAwaitingPayment() {

        log.info("Invoked update status of all payment with past date and awaiting status");

        var today = LocalDate.now();

        List<Payment> payments = paymentRepository.findWithAwaitingStatusAndReservationDateOnOrAfterNow(PaymentStatus.AWAITING, today);
        payments.forEach(payment -> updatePaymentStatus(payment.getId(), PaymentStatus.CANCELLED));
    }
}
