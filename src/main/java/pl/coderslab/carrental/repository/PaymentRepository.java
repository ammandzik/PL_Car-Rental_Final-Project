package pl.coderslab.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.coderslab.carrental.model.Payment;
import pl.coderslab.carrental.model.enum_package.PaymentMethod;
import pl.coderslab.carrental.model.enum_package.PaymentStatus;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("select p from Payment p where (:paymentStatus IS NULL or p.paymentStatus = :paymentStatus) and (:paymentMethod IS NULL OR p.paymentMethod = :paymentMethod) and (:reservationId IS NULL OR p.reservation.id = :reservationId)")
    List<Payment> getPaymentsByStatusOrMethodOrReservation(@Param("paymentStatus") PaymentStatus paymentStatus, @Param("paymentMethod")
    PaymentMethod paymentMethod, @Param("reservationId") Long reservationId);

    @Query("select count(p) > 0 from Payment p where p.paymentStatus = :paymentStatus and p.reservation.id = :reservationId")
    boolean existsByRefundStatusAndReservationId(PaymentStatus paymentStatus, Long reservationId);
}
