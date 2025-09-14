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

        var today = LocalDate.now();

        var payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Payment was not found with id %s", id)));

        if (paymentRepository.hasPastDateAndCancelledStatus(payment.getReservation().getId(), today)) {
            throw new PaymentEditionException(String.format("Payment with id %s is already cancelled and reservation date is in the past.", id));
        }

        if (payment.getPaymentStatus().equals(PaymentStatus.APPROVED) && !status.equals(PaymentStatus.FUNDS_BEING_REFUNDED) ||
            payment.getPaymentStatus().equals(PaymentStatus.FUNDS_BEING_REFUNDED) && !status.equals(PaymentStatus.FUNDS_PAID_BACK)) {

            throw new PaymentEditionException(String.format("Payment with id %s is already in %s status and it's status cannot be changed to %s ",
                    id, payment.getPaymentStatus().getDescription(), status.getDescription()));

        } else if (payment.getPaymentStatus().equals(PaymentStatus.APPROVED)) {
            reservationService.updateStatus(payment.getReservation().getId(), false);
            payment.setPaymentStatus(status);
            payment.setRefundDate(today);

        } else if (status.equals(PaymentStatus.APPROVED)) {
            reservationService.updateStatus(payment.getReservation().getId(), true);
            payment.setPaymentStatus(status);
        } else {
            payment.setPaymentStatus(status);
        }
        paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }

    @Transactional
    public PaymentDto save(PaymentDto paymentDto) {

        log.info("Invoked save payment method");

        if (paymentDto != null) {

            var today = LocalDate.now();

            var reservation = reservationService.findById(paymentDto.getReservationId());

            if (paymentRepository.hasPastDateAndCancelledStatus(reservation.getId(), today)) {
                throw new PaymentEditionException("Cannot create new payment. Payment is already cancelled and reservation date is in the past.");
            }

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

        if (paymentRepository.existsByStatusAndReservationId(PaymentStatus.AWAITING, reservationId)) {
            throw new EntityExistsException(String.format("Payment with reservation id %s already exists and has awaiting status. Processing new payment is not allowed.", reservationId));
        }

    }

    private void checkIfAskedForRefund(Long reservationId) {
        if (paymentRepository.existsByStatusAndReservationId(PaymentStatus.FUNDS_BEING_REFUNDED, reservationId)) {
            throw new EntityExistsException(String.format("Payment already exists with reservation id %s and the funds are being returned. " +
                                                          "Therefore, payment cannot be processed again.", reservationId));
        }
    }

    private void checkIfApprovedPaymentAlreadyExists(Long reservationId) {

        if (paymentRepository.existsByStatusAndReservationId(PaymentStatus.APPROVED, reservationId)) {
            throw new EntityExistsException(String.format("Payment with reservation id %s already exists and has been approved. " +
                                                          "Processing another payment is not allowed.", reservationId));
        }
    }

    private void checkIfPaymentApprovedAndChangeReservationStatus(Long reservationId, PaymentDto paymentDto) {
        if (paymentDto.getPaymentStatus().equals(PaymentStatus.APPROVED)) {
            reservationService.updateStatus(reservationId, true);
        }
    }

    @Transactional
    public void updateStatusForAllAskedForRefund() {

        var today = LocalDate.now();
        var todayMinusThree = today.minusDays(3);

        List<Payment> payments = paymentRepository.findByStatusAndRefundDate(PaymentStatus.FUNDS_BEING_REFUNDED, todayMinusThree);

        payments.forEach(payment -> updatePaymentStatus(payment.getId(), PaymentStatus.FUNDS_PAID_BACK));
    }

    @Transactional
    public void updateStatusOfAllWithPastDateAndAwaitingPayment() {
        var today = LocalDate.now();

        List<Payment> payments = paymentRepository.findWithAwaitingStatusAndReservationDateOnOrAfterNow(PaymentStatus.AWAITING, today);
        payments.forEach(payment -> updatePaymentStatus(payment.getId(), PaymentStatus.CANCELLED));
    }
}
