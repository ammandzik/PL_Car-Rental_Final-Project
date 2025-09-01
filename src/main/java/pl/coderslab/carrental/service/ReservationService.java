package pl.coderslab.carrental.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.carrental.dto.CarDto;
import pl.coderslab.carrental.dto.ReservationDto;
import pl.coderslab.carrental.exception.CarAlreadyRentedException;
import pl.coderslab.carrental.mapper.CarMapper;
import pl.coderslab.carrental.mapper.ReservationMapper;
import pl.coderslab.carrental.mapper.UserMapper;
import pl.coderslab.carrental.model.enum_package.CarStatus;
import pl.coderslab.carrental.repository.ReservationRepository;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final UserService userService;
    private final CarService carService;
    private final CarMapper carMapper;
    private final UserMapper userMapper;

    public ReservationService(ReservationRepository reservationRepository, ReservationMapper reservationMapper, UserService userService, CarService carService, CarMapper carMapper, UserMapper userMapper) {
        this.reservationRepository = reservationRepository;
        this.reservationMapper = reservationMapper;
        this.userService = userService;
        this.carService = carService;
        this.carMapper = carMapper;
        this.userMapper = userMapper;
    }

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
                .orElseThrow(() -> new EntityNotFoundException(String.format("Reservation not found with id %s", id)));
    }

    public void update(ReservationDto reservationDto) {
        log.info("Update reservation method invoked");

        if (reservationDto != null) {
            log.info("Updating reservation with id {}", reservationDto.getId());
            var reservation = reservationRepository.findById(reservationDto.getId()).get();

            reservation.setConfirmed(reservationDto.isConfirmed());
            reservation.setDateFrom(reservationDto.getDateFrom());
            reservation.setDateTo(reservationDto.getDateTo());
            reservation.setFinalPrice(reservationDto.getFinalPrice());

            reservationRepository.save(reservation);
        } else {
            throw new IllegalArgumentException("Reservation should not be null");
        }
    }

    @Transactional
    public ReservationDto save(ReservationDto reservationDto) {

        log.info("Invoked save reservation method");

        if (reservationDto != null) {
            var userDto = userService.findById(reservationDto.getUserId());
            var carDto = carService.getCarById(reservationDto.getCarId());
            var reservation = reservationMapper.toEntity(reservationDto);

            if (carDto.getCarStatus().equals(CarStatus.RENTED)) {
                throw new CarAlreadyRentedException(String.format("Car with id %s already rented.", carDto.getId()));
            }

            reservation.setConfirmed(reservation.isConfirmed());
            reservation.setUser(userMapper.toUser(userDto));
            reservation.setCar(carMapper.toEntity(carDto));
            reservation.setFinalPrice(getTotalPrice(carDto, reservationDto));

            if(reservation.isConfirmed()) {
                carDto.setCarStatus(CarStatus.RENTED);
            }
            carService.updateCar(carDto.getId(), carDto);

            log.info("Saving/updating reservation");

            return reservationMapper.toDto(reservationRepository.save(reservation));

        } else {
            throw new IllegalArgumentException("Reservation object is null");
        }
    }

    private Long getDays(ReservationDto reservationDto) {

        return ChronoUnit.DAYS.between(reservationDto.getDateFrom(), reservationDto.getDateTo());
    }

    private Double getTotalPrice(CarDto car, ReservationDto reservationDto) {

        return car.getPricePerDay() * getDays(reservationDto);
    }
}
