package pl.coderslab.carrental.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.carrental.dto.CarDto;
import pl.coderslab.carrental.dto.ReservationDto;
import pl.coderslab.carrental.exception.CarAlreadyRentedException;
import pl.coderslab.carrental.mapper.CarMapper;
import pl.coderslab.carrental.mapper.ReservationMapper;
import pl.coderslab.carrental.mapper.UserMapper;
import pl.coderslab.carrental.model.Car;
import pl.coderslab.carrental.model.Reservation;
import pl.coderslab.carrental.model.enum_package.CarStatus;
import pl.coderslab.carrental.repository.ReservationRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationService {

    private static final String RESERVATION_NOT_FOUND_WITH_ID_S = "Reservation not found with id %s";
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final UserService userService;
    private final CarService carService;
    private final CarMapper carMapper;
    private final UserMapper userMapper;

    public List<ReservationDto> findAll() {

        log.info("Find all reservations method invoked");

        return reservationRepository.findAll()
                .stream()
                .map(reservationMapper::toDto)
                .toList();
    }

    public ReservationDto findById(Long id) {

        log.info("Find reservation by id method invoked");

        return reservationRepository.findById(id)
                .map(reservationMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format(RESERVATION_NOT_FOUND_WITH_ID_S, id)));
    }

    public ReservationDto update(Long id, ReservationDto reservationDto) {
        log.info("Update reservation method invoked");

        if (reservationDto != null && id != null) {

            var reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(String.format(RESERVATION_NOT_FOUND_WITH_ID_S, reservationDto.getId())));
            var car = reservation.getCar();

            reservation.setConfirmed(reservationDto.isConfirmed());
            checkReservationStatusAndChangeCarStatus(reservation, car);

            log.info("Updating reservation with id {}", reservationDto.getId());
            return reservationMapper.toDto(reservationRepository.save(reservation));
        } else {
            throw new IllegalArgumentException("Reservation should not be null");
        }
    }

    public Reservation getReservationEntityWithComponents(Long id) {
        log.info("Get reservation by id method invoked");

        return reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(RESERVATION_NOT_FOUND_WITH_ID_S, id)));
    }

    @Transactional
    public ReservationDto save(ReservationDto reservationDto) {

        log.info("Invoked save reservation method");

        if (reservationDto != null) {

            var userDto = userService.findById(reservationDto.getUserId());
            var carDto = carService.getCarById(reservationDto.getCarId());
            var reservation = reservationMapper.toEntity(reservationDto);

            validateReservationAndCarOrElseThrow(reservationDto, carDto);

            reservation.setConfirmed(false);
            reservation.setUser(userMapper.toUser(userDto));
            reservation.setCar(carMapper.toEntity(carDto));
            reservation.setFinalPrice(getTotalPrice(carDto, reservationDto));

            log.info("Saving new reservation");

            return reservationMapper.toDto(reservationRepository.save(reservation));

        } else {
            throw new IllegalArgumentException("Reservation object is null");
        }
    }

    public void deleteById(Long id) {
        log.info("Delete reservation by id method invoked");

        if (id != null) {

            var reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(String.format(RESERVATION_NOT_FOUND_WITH_ID_S, id)));

            reservationRepository.delete(reservation);
            log.info("Deleting reservation with id {}", id);
        } else {
            throw new IllegalArgumentException("Reservation id is null");
        }
    }

    private void validateReservationAndCarOrElseThrow(ReservationDto reservationDto, CarDto carDto) {

        if (reservationRepository.existsByCarIdWithFutureDate(reservationDto.getCarId(), LocalDate.now())) {
            throw new EntityExistsException(String.format("Reservation with car id %s already exists", reservationDto.getCarId()));
        }

        if (carDto.getCarStatus().equals(CarStatus.RENTED)) {
            throw new CarAlreadyRentedException(String.format("Car with id %s already rented.", carDto.getId()));
        }
    }

    private Long getDays(ReservationDto reservationDto) {

        return ChronoUnit.DAYS.between(reservationDto.getDateFrom(), reservationDto.getDateTo());
    }

    private Double getTotalPrice(CarDto car, ReservationDto reservationDto) {

        return car.getPricePerDay() * getDays(reservationDto);
    }

    private void checkReservationStatusAndChangeCarStatus(Reservation reservation, Car car) {
        if (reservation.isConfirmed()) {
            car.setCarStatus(CarStatus.RENTED);
            carService.updateCar(car.getId(), carMapper.toDto(car));
        } else {
            car.setCarStatus(CarStatus.AVAILABLE);
            carService.updateCar(car.getId(), carMapper.toDto(car));
        }
    }
}
