package pl.coderslab.carrental.mapper;

import org.springframework.stereotype.Component;
import pl.coderslab.carrental.dto.InvoiceDto;
import pl.coderslab.carrental.model.Invoice;

@Component
public class InvoiceMapper {

    public InvoiceDto toDto(Invoice invoice) {

        return InvoiceDto.builder()
                .id(invoice.getId())
                .userId(invoice.getUser().getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .issueDate(invoice.getIssueDate())
                .reservationId(invoice.getReservation().getId())
                .totalAmount(invoice.getTotalAmount())
                .build();
    }

    public Invoice toEntity(InvoiceDto invoiceDto) {

        return Invoice.builder()
                .id(invoiceDto.getId())
                .invoiceNumber(invoiceDto.getInvoiceNumber())
                .issueDate(invoiceDto.getIssueDate())
                .totalAmount(invoiceDto.getTotalAmount())
                .build();
    }
}
