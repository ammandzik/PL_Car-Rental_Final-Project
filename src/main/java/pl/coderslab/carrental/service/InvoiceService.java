package pl.coderslab.carrental.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.coderslab.carrental.dto.InvoiceDto;
import pl.coderslab.carrental.exception.InvoiceAlreadyExists;
import pl.coderslab.carrental.exception.PdfImportException;
import pl.coderslab.carrental.mapper.InvoiceMapper;
import pl.coderslab.carrental.mapper.UserMapper;
import pl.coderslab.carrental.model.Utils;
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

        log.info(("Invoked get all invoices method"));

        return invoiceRepository.findAll()
                .stream()
                .map(invoiceMapper::toDto)
                .toList();
    }

    public List<InvoiceDto> getInvoicesFilteredByPeriod(LocalDate start, LocalDate end) {

        log.info(("Invoked get all invoices filtered by period method"));

        return invoiceRepository.findByIssueDateRange(start, end)
                .stream()
                .map(invoiceMapper::toDto)
                .toList();
    }

    public InvoiceDto getInvoice(Long id) {
        log.info(("Invoked get invoice method"));

        if (id != null) {
            var invoice = invoiceRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(String.format(INVOICE_WITH_ID_S_NOT_FOUND, id)));

            log.info("Invoice with id {} found", id);
            return invoiceMapper.toDto(invoice);
        } else {
            throw new IllegalArgumentException("Invoice id is null");
        }
    }

    public InvoiceDto addInvoice(InvoiceDto invoiceDto) {
        log.info(("Invoked save invoice method"));

        if (invoiceDto != null) {

            if (invoiceRepository.existsByInvoiceNumberAndReservationId(invoiceDto.getReservationId())) {
                throw new InvoiceAlreadyExists(String.format("Invoice with reservation id %s already exists", invoiceDto.getReservationId()));
            }

            var reservation = reservationService.getReservationEntityWithComponents(invoiceDto.getReservationId());
            var user = reservation.getUser();

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

    public void deleteInvoice(Long id) {

        log.info(("Invoked delete invoice method"));

        if (id != null) {
            var invoice = invoiceRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(String.format(INVOICE_WITH_ID_S_NOT_FOUND, id)));
            invoiceRepository.delete(invoice);
            log.info("Invoice with id {} deleted", id);
        } else {
            throw new IllegalArgumentException("Cannot remove. Invoice id is null.");
        }
    }

    public InvoiceDto updateInvoice(Long id, InvoiceDto invoiceDto) {

        log.info(("Invoked update invoice method"));

        if (id != null) {
            var invoice = invoiceRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(String.format(INVOICE_WITH_ID_S_NOT_FOUND, id)));

            log.info("Invoice with id {} found", id);

            var reservation = reservationService.getReservationEntityWithComponents(invoice.getReservation().getId());
            var user = userMapper.toUser(userService.findById(invoice.getUser().getId()));

            invoice.setInvoiceNumber(invoiceDto.getInvoiceNumber());
            invoice.setIssueDate(invoiceDto.getIssueDate());
            invoice.setTotalAmount(invoiceDto.getTotalAmount());
            invoice.setUser(user);
            invoice.setReservation(reservation);
            invoiceRepository.save(invoice);
            log.info("Updated invoice saved to database");

            return invoiceMapper.toDto(invoice);
        } else {
            throw new IllegalArgumentException(String.format("Id and/or invoice body is null %s %s", id, invoiceDto));
        }
    }

    public byte[] generateInvoicePdf(Long invoiceId) {

        log.info(("Invoked generate invoice pdf method"));

        try {
            var invoice = invoiceRepository.findById(invoiceId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format(INVOICE_WITH_ID_S_NOT_FOUND, invoiceId)));
            var reservation = invoice.getReservation();
            var user = reservation.getUser();

            return Utils.buildSimpleInvoice(invoiceId, user, reservation);

        } catch (IOException e) {
            throw new PdfImportException(String.format("Cannot generate invoice pdf %s", invoiceId));
        }
    }

    private String uniqueInvoiceNumberGenerator(LocalDate issueDate) {

        var randomId = Utils.randomBase64Url12();
        var sb = new StringBuilder(String.format("%s-%s", issueDate, randomId));

        return sb.toString();
    }

}
