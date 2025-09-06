package pl.coderslab.carrental.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.carrental.dto.ReservationDto;
import pl.coderslab.carrental.service.ReservationService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationDto>> getAllReservations() {

        return new ResponseEntity<>(reservationService.findAll(), HttpStatus.OK);
    }

    @PostMapping("/reservation")
    public ResponseEntity<ReservationDto> createReservation(@Valid @RequestBody ReservationDto reservationDto) {

        return new ResponseEntity<>(reservationService.save(reservationDto), HttpStatus.CREATED);
    }

    @PutMapping("/reservation")
    public ResponseEntity<ReservationDto> updateReservation(@RequestParam Long id, @RequestBody ReservationDto reservationDto) {

        return new ResponseEntity<>(reservationService.update(id, reservationDto), HttpStatus.OK);
    }

    @DeleteMapping("/reservation")
    public ResponseEntity<String> deleteInactiveReservations(@RequestParam Long id) {

        reservationService.deleteById(id);
        return new ResponseEntity<>("Deleted reservation", HttpStatus.NO_CONTENT);
    }

}
