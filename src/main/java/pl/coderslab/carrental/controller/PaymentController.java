package pl.coderslab.carrental.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.carrental.dto.PaymentDto;
import pl.coderslab.carrental.model.enum_package.PaymentMethod;
import pl.coderslab.carrental.model.enum_package.PaymentStatus;
import pl.coderslab.carrental.service.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/payments")
    public ResponseEntity<List<PaymentDto>> getAllPayments(@RequestParam(required = false) PaymentStatus paymentStatus,
                                                           @RequestParam(required = false) PaymentMethod paymentMethod,
                                                           @RequestParam(required = false) Long reservationId) {

        if (paymentStatus == null && paymentMethod == null && reservationId == null) {

            return new ResponseEntity<>(paymentService.getAllPayments(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(paymentService.getPaymentsWithFilters(paymentStatus, paymentMethod, reservationId), HttpStatus.OK);
        }
    }

    @PostMapping("/pay")
    public ResponseEntity<PaymentDto> payReservation(@RequestBody PaymentDto paymentDto) {

        return new ResponseEntity<>(paymentService.save(paymentDto), HttpStatus.CREATED);
    }
}
