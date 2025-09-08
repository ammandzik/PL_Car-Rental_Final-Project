package pl.coderslab.carrental.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartupListener {

    private final CarService carService;
    private final PaymentService paymentService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {

        log.info("StartupListener onApplicationReady");
        carService.updateCarsAvailabilityByClosedReservationDate();
        log.info(("Updated car availability on application ready"));

        paymentService.updateStatusForAllAskedForRefund();
        log.info(("Updated all payments status asked for funds being refunded more than 3 days ago to funds paid back on application ready"));
    }
}
