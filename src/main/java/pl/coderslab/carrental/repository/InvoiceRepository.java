package pl.coderslab.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.coderslab.carrental.model.Invoice;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByUserId(Long userId);
    Invoice findByInvoiceNumber(String invoiceNumber);
    boolean existsByInvoiceNumberAndReservationId(String invoiceNumber, Long reservationId);
}
