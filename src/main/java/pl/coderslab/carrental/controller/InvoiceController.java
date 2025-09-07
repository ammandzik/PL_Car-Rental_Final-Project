package pl.coderslab.carrental.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.carrental.dto.InvoiceDto;
import pl.coderslab.carrental.service.InvoiceService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/invoices")
    public List<InvoiceDto> getInvoices(@RequestParam(required = false) LocalDate start, @RequestParam(required = false) LocalDate end) {

        if (start == null && end == null) {
            return invoiceService.getInvoices();
        } else {
            return invoiceService.getInvoicesFilteredByPeriod(start, end);
        }
    }

    @PostMapping("/admin/invoice")
    public ResponseEntity<InvoiceDto> createInvoice(@RequestBody InvoiceDto invoiceDto) {

        return new ResponseEntity<>(invoiceService.addInvoice(invoiceDto), HttpStatus.CREATED);
    }

    @GetMapping("/invoices/pdf")
    public ResponseEntity<byte[]> getInvoicePdf(@RequestParam Long id) {

        byte[] pdfBytes = invoiceService.generateInvoicePdf(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=invoice-%d.pdf", id))
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @DeleteMapping("/admin/invoice")
    public ResponseEntity<String> deleteInvoice(@RequestParam Long id) {

        invoiceService.deleteInvoice(id);

        return new ResponseEntity<>("Invoice removed", HttpStatus.NO_CONTENT);
    }

    @PutMapping("/admin/invoice")
    public ResponseEntity<InvoiceDto> refreshInvoiceData(@RequestParam Long id) {

        return new ResponseEntity<>(invoiceService.refreshInvoiceData(id), HttpStatus.OK);
    }
}
