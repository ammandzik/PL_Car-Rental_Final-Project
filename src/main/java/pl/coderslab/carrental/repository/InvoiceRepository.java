package pl.coderslab.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.coderslab.carrental.model.Invoice;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("""
            SELECT CASE WHEN count(i) > 0 THEN true ELSE false END
            FROM Invoice i WHERE i.reservation.id = :reservationId
            """)
    boolean existsByInvoiceNumberAndReservationId(Long reservationId);

    @Query("""
            SELECT i FROM Invoice i
            WHERE (:start IS NULL OR i.issueDate >= :start)
            AND (:end IS NULL OR i.issueDate <= :end)
            """)
    List<Invoice> findByIssueDateRange(LocalDate start,
                                       LocalDate end);

    @Query("""
            SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Payment p
            WHERE p.reservation.id = :reservationId
            AND p.paymentStatus = "APPROVED"
            """)
    boolean invoiceReservationPaymentIsApproved(Long reservationId);
}
