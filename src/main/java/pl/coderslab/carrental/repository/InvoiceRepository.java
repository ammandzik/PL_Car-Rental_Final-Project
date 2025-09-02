package pl.coderslab.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.coderslab.carrental.model.Invoice;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("select count(i) > 0 from Invoice i where i.reservation.id = :reservationId")
    boolean existsByInvoiceNumberAndReservationId(Long reservationId);

    @Query("""
            select i from Invoice i
            where (:start is null or i.issueDate >= :start)
              and (:end   is null or i.issueDate <= :end)
            """)
    List<Invoice> findByIssueDateRange(@Param("start") LocalDate start,
                                       @Param("end") LocalDate end);
}
