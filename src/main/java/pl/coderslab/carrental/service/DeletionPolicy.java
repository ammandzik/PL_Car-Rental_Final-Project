package pl.coderslab.carrental.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.coderslab.carrental.repository.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeletionPolicy {

    private final CarRepository carRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final ReviewRepository reviewRepository;
    private final RoleRepository roleRepository;

    boolean canDeleteBrand(Long brandId) {

        log.info("Invoked can delete brand method");

        return !carRepository.existsByBrandId(brandId);
    }

    boolean canDeleteCar(Long carId) {

        log.info("Invoked can delete car method");

        return !reservationRepository.existsByCarId(carId);
    }

    boolean canDeleteReservation(Long reservationId) {

        log.info("Invoked can delete reservation method");

        return !paymentRepository.existsByReservationId(reservationId) || !invoiceRepository.existsByReservationId(reservationId)
               || !reviewRepository.existsByReservationId(reservationId);
    }

    boolean canDeleteRole(Long roleId) {

        log.info("Invoked can delete role method");

        return !roleRepository.roleInUse(roleId);
    }

    boolean canDeleteUser(Long userId) {
        log.info("Invoked can delete user method");

        boolean noInvoices = !invoiceRepository.existsByUserId(userId);
        boolean noReservations = !reservationRepository.existsByUserId(userId);

        return noInvoices && noReservations;
    }
}
