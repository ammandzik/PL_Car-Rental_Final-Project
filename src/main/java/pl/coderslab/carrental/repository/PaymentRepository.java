package pl.coderslab.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.coderslab.carrental.model.Payment;
import pl.coderslab.carrental.model.enum_package.PaymentMethod;
import pl.coderslab.carrental.model.enum_package.PaymentStatus;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("""
            SELECT p FROM Payment p
            WHERE (:paymentStatus IS NULL OR p.paymentStatus = :paymentStatus) 
            AND (:paymentMethod IS NULL OR p.paymentMethod = :paymentMethod) 
            AND (:reservationId IS NULL OR p.reservation.id = :reservationId)
            """)
    List<Payment> getPaymentsByStatusOrMethodOrReservation(PaymentStatus paymentStatus, PaymentMethod paymentMethod, Long reservationId);

    @Query("""
            SELECT COUNT(p) > 0 FROM Payment p
            WHERE p.paymentStatus = :paymentStatus
            AND p.reservation.id = :reservationId
            """)
    boolean existsByStatusAndReservationId(PaymentStatus paymentStatus, Long reservationId);

    @Query("""
            SELECT p FROM Payment p
            WHERE p.refundDate <= :todayMinusThreeDays
            AND p.paymentStatus = :paymentStatus
            """)
    List<Payment> findByStatusAndRefundDate(PaymentStatus paymentStatus, LocalDate todayMinusThreeDays);

}
