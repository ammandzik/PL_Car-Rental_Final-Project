package pl.coderslab.carrental.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pl.coderslab.carrental.dto.InvoiceDto;
import pl.coderslab.carrental.exception.InvoiceCreationNotAllowed;
import pl.coderslab.carrental.exception.PdfImportException;
import pl.coderslab.carrental.mapper.InvoiceMapper;
import pl.coderslab.carrental.mapper.UserMapper;
import pl.coderslab.carrental.model.Reservation;
import pl.coderslab.carrental.model.Utils;
import pl.coderslab.carrental.model.enum_package.PaymentStatus;
import pl.coderslab.carrental.repository.InvoiceRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InvoiceService {

    private static final String INVOICE_WITH_ID_S_NOT_FOUND = "Invoice with id %s not found";
    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final UserService userService;
    private final ReservationService reservationService;
    private final UserMapper userMapper;

    public List<InvoiceDto> getInvoices() {

        log.info("Invoked get all invoices method");

        return invoiceRepository.findAll()
                .stream()
                .map(invoiceMapper::toDto)
                .toList();
    }

    public List<InvoiceDto> getInvoicesFilteredByPeriod(LocalDate start, LocalDate end) {

        log.info("Invoked get all invoices filtered by period method");

        return invoiceRepository.findByIssueDateRange(start, end)
                .stream()
                .map(invoiceMapper::toDto)
                .toList();
    }

    @Cacheable(value = "invoice", key = "#id")
    public InvoiceDto getInvoice(Long id) {

        log.info("Invoked get invoice method");

        if (id != null) {

            var invoice = invoiceRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(String.format(INVOICE_WITH_ID_S_NOT_FOUND, id)));

            return invoiceMapper.toDto(invoice);
        } else {
            throw new IllegalArgumentException("Invoice id is null");
        }
    }

    @Transactional
    public InvoiceDto addInvoice(InvoiceDto invoiceDto) {

        log.info("Invoked save invoice method");

        if (invoiceDto != null) {

            checkIfInvoiceExistsByReservation(invoiceDto.getReservationId());

            var reservation = reservationService.getReservationEntityWithComponents(invoiceDto.getReservationId());
            var user = reservation.getUser();

            checkIfReservationIsConfirmedAndPaid(reservation);

            var invoice = invoiceMapper.toEntity(invoiceDto);
            var date = LocalDate.now();

            invoice.setIssueDate(date);
            invoice.setInvoiceNumber(uniqueInvoiceNumberGenerator(date));
            invoice.setTotalAmount(reservation.getFinalPrice());
            invoice.setUser(user);
            invoice.setReservation(reservation);

            invoiceRepository.save(invoice);
            log.info("Invoice saved to database");

            return invoiceMapper.toDto(invoice);

        } else {
            throw new IllegalArgumentException("Invoice body is null");
        }
    }

    @CacheEvict(value = "invoice", key = "#id")
    public void deleteInvoice(Long id) {

        log.info("Invoked delete invoice method");

        if (id != null) {

            var invoice = invoiceRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(String.format(INVOICE_WITH_ID_S_NOT_FOUND, id)));

            invoiceRepository.delete(invoice);
            log.info("Invoice with id {} deleted", id);
        } else {
            throw new IllegalArgumentException("Cannot remove. Invoice id is null.");
        }
    }

    @Transactional
    @CacheEvict(value = "invoice", key = "#id")
    public void refreshInvoiceData(Long id) {

        log.info("Invoked update invoice method");

        if (id != null) {

            var invoice = invoiceRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(String.format(INVOICE_WITH_ID_S_NOT_FOUND, id)));

            var reservation = reservationService.getReservationEntityWithComponents(invoice.getReservation().getId());
            var user = userMapper.toUser(userService.findById(invoice.getUser().getId()));

            if (fundsWereWithdrawnForTheInvoiceReservation(reservation.getId())) {
                invoiceRepository.delete(invoice);
                log.info("Invoice with id {} deleted due to cancellation of reservation", id);

            } else {
                invoice.setIssueDate(LocalDate.now());
                invoice.setTotalAmount(reservation.getFinalPrice());
                invoice.setUser(user);
                invoice.setReservation(reservation);

                invoiceRepository.save(invoice);
                log.info("Updated invoice saved to database");
            }

        } else {
            throw new IllegalArgumentException("Id is null");
        }
    }

    @Transactional
    public byte[] generateInvoicePdf(Long invoiceId) {

        log.info("Invoked generate invoice pdf method");

        try {

            refreshInvoiceData(invoiceId);
            var invoice = invoiceRepository.findById(invoiceId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format(INVOICE_WITH_ID_S_NOT_FOUND, invoiceId)));
            var reservation = invoice.getReservation();
            var user = reservation.getUser();

            log.info("Building invoice");

            return Utils.buildSimpleInvoice(invoiceId, user, reservation);

        } catch (IOException e) {
            throw new PdfImportException(String.format("Cannot generate invoice pdf %s", invoiceId));
        }
    }

    private String uniqueInvoiceNumberGenerator(LocalDate issueDate) {

        log.info("Invoked unique invoice number generator method");

        var randomId = Utils.randomBase64Url12();
        var sb = new StringBuilder(String.format("%s-%s", issueDate, randomId));

        return sb.toString();
    }

    private boolean fundsWereWithdrawnForTheInvoiceReservation(Long reservationId) {

        log.info("Invoked funds were withdrawn for the invoice reservation {}", reservationId);

        return invoiceRepository.invoiceReservationPaymentHasStatus(reservationId, PaymentStatus.FUNDS_BEING_REFUNDED)
               || invoiceRepository.invoiceReservationPaymentHasStatus(reservationId, PaymentStatus.FUNDS_PAID_BACK);
    }

    private void checkIfReservationIsConfirmedAndPaid(Reservation reservation) {

        log.info("Invoked checkIfReservationIsConfirmedAndPaid method");

        if (!reservation.isConfirmed() || !invoiceRepository.invoiceReservationPaymentHasStatus(reservation.getId(), PaymentStatus.APPROVED)) {
            throw new InvoiceCreationNotAllowed("Invoice creation not allowed. Reservation has not been confirmed and paid");
        }
    }

    private void checkIfInvoiceExistsByReservation(Long reservationId) {

        log.info("Invoked check if invoice exists by reservation");

        if (invoiceRepository.existsByInvoiceNumberAndReservationId(reservationId)) {
            throw new EntityExistsException(String.format("Invoice with reservation id %s already exists", reservationId));
        }
    }
}
