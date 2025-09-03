package pl.coderslab.carrental.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupListener {

    private final CarService carService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        carService.updateCarsAvailabilityByClosedReservationDate();
    }
}
