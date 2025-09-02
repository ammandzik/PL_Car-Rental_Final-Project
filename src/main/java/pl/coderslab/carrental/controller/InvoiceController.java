package pl.coderslab.carrental.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.carrental.dto.InvoiceDto;
import pl.coderslab.carrental.service.InvoiceService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("api/invoices")
    public List<InvoiceDto> getInvoices() {
        return invoiceService.getInvoices();
    }

    @PostMapping("/admin/invoice")
    public InvoiceDto createInvoice(@RequestBody InvoiceDto invoiceDto) {

        return invoiceService.addInvoice(invoiceDto);
    }

    @GetMapping("/invoices/pdf")
    public ResponseEntity<byte[]> getInvoicePdf(@RequestParam Long id) {

        byte[] pdfBytes = invoiceService.generateInvoicePdf(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=invoice-%d.pdf", id))
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
