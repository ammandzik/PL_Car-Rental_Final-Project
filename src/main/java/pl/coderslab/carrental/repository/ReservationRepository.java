package pl.coderslab.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.coderslab.carrental.model.Reservation;
import pl.coderslab.carrental.model.enum_package.PaymentStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
            SELECT DISTINCT r.car.id
               FROM Reservation r
               WHERE r.dateTo   >= :today
               AND r.confirmed = true
            """)
    Set<Long> findActiveCarIds(LocalDate today);

    @Query("""
            SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END 
            FROM Payment p
            WHERE p.reservation.id = :reservationId
            AND p.paymentStatus = :status
            """)
    boolean paymentExistsForReservationAndStatus(Long reservationId, PaymentStatus status);

    @Query("""
            SELECT r FROM Reservation r
            WHERE r.dateFrom <= :today
            AND r.dateTo > :today
            """)
    List<Reservation> findActiveReservations(LocalDate today);

    @Modifying
    @Query("""
                UPDATE Payment p
                SET p.amount = :amount
                WHERE p.reservation.id = :reservationId
                AND p.paymentStatus = "AWAITING"
            """)
    void updatePaymentTotalPriceForReservation(Long reservationId, Double amount);

    @Query("""
                SELECT NOT EXISTS (
                SELECT 1
                FROM Reservation r
                WHERE r.dateFrom <= :end
                AND r.dateTo >= :start
                )
            """)
    boolean reservationAllowedForNew(LocalDate start, LocalDate end);

    @Query("""
                SELECT CASE WHEN COUNT(r) = 0 THEN TRUE ELSE FALSE END
                FROM Reservation r
                WHERE r.dateFrom <= :end
                  AND r.dateTo >= :start
                  AND NOT (r.id = :id)
            """)
    boolean reservationAllowedForUpdate(LocalDate start, LocalDate end, Long id);

    boolean existsByCarId(Long carId);
    boolean existsByUserId(Long userId);
}
