package pl.coderslab.carrental.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.carrental.dto.CarDto;
import pl.coderslab.carrental.dto.ReservationDto;
import pl.coderslab.carrental.exception.ReservationDateException;
import pl.coderslab.carrental.exception.ReservationEditNotAllowed;
import pl.coderslab.carrental.mapper.CarMapper;
import pl.coderslab.carrental.mapper.ReservationMapper;
import pl.coderslab.carrental.mapper.UserMapper;
import pl.coderslab.carrental.model.Reservation;
import pl.coderslab.carrental.model.enum_package.PaymentStatus;
import pl.coderslab.carrental.repository.ReservationRepository;

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
    private final DeletionPolicy deletionPolicy;

    public List<ReservationDto> findAll() {

        log.info("Find all reservations method invoked");

        return reservationRepository.findAll()
                .stream()
                .map(reservationMapper::toDto)
                .toList();
    }

    @Cacheable(value = "reservation", key = "#id")
    public ReservationDto findById(Long id) {

        log.info("Find reservation by id method invoked");

        return reservationRepository.findById(id)
                .map(reservationMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format(RESERVATION_NOT_FOUND_WITH_ID_S, id)));
    }

    @Transactional
    @CachePut(value = "reservation", key = "#id")
    public ReservationDto updateStatus(Long id, Boolean status) {
        log.info("Update reservation status invoked");

        if (status != null && id != null) {

            var reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(String.format(RESERVATION_NOT_FOUND_WITH_ID_S, id)));

            reservation.setConfirmed(status);

            log.info("Updating reservation status with id {}", id);
            return reservationMapper.toDto(reservationRepository.save(reservation));
        } else {
            throw new IllegalArgumentException("Reservation and/or id should not be null");
        }
    }

    @Transactional
    @CachePut(value = "reservation", key = "#id")
    public ReservationDto update(Long id, ReservationDto reservationDto) {
        log.info("Update reservation method invoked");

        if (reservationDto != null && id != null) {

            if (reservationDto.getDateFrom() == reservationDto.getDateTo()) {
                throw new ReservationDateException("Date from and Date should not be the same");
            }

            var reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(String.format(RESERVATION_NOT_FOUND_WITH_ID_S, id)));

            if (!reservationRepository.reservationAllowedForUpdate(reservationDto.getDateFrom(), reservationDto.getDateTo(), id)) {
                throw new ReservationDateException("Car is not available for rent during that period");
            }

            checkIfPaidAndConfirmed(id, reservation);
            checkIfFundsBeingReturned(id);
            checkWhichComponentsShouldBeUpdated(reservationDto, reservation);
            reservationRepository.updatePaymentTotalPriceForReservation(id, reservation.getFinalPrice());

            log.info("Updating reservation with id {}", id);
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

            if (reservationDto.getDateFrom().equals(reservationDto.getDateTo())) {
                throw new ReservationDateException("Date from and Date should not be the same");
            }

            var userDto = userService.findById(reservationDto.getUserId());
            var carDto = carService.getCarById(reservationDto.getCarId());
            var reservation = reservationMapper.toEntity(reservationDto);

            if (!reservationRepository.reservationAllowedForNew(reservationDto.getDateFrom(), reservationDto.getDateTo())) {
                throw new ReservationDateException("Car is not available for rent during that period");
            }

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

    @CacheEvict(value = "reservation", key = "#id")
    public void deleteById(Long id) {
        log.info("Delete reservation by id method invoked");

        if (id != null) {

            if (!deletionPolicy.canDeleteReservation(id)) {
                throw new IllegalArgumentException(String.format("Reservation cannot be removed due to existing entities for reservation with id %s", id));
            }

            var reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(String.format(RESERVATION_NOT_FOUND_WITH_ID_S, id)));

            reservationRepository.delete(reservation);
            log.info("Deleting reservation with id {}", id);
        } else {
            throw new IllegalArgumentException("Reservation id is null");
        }
    }

    private Long getDays(ReservationDto reservationDto) {

        log.info("Invoked getDays reservation method");

        return ChronoUnit.DAYS.between(reservationDto.getDateFrom(), reservationDto.getDateTo());
    }

    private Double getTotalPrice(CarDto car, ReservationDto reservationDto) {

        log.info("Invoked getTotalPrice method");

        return car.getPricePerDay() * getDays(reservationDto);
    }

    private void checkWhichComponentsShouldBeUpdated(ReservationDto reservationDto, Reservation reservation) {
        log.info("Invoked check which components should be updated");

        if (reservationDto.getDateFrom() != null) {
            reservation.setDateFrom(reservationDto.getDateFrom());
        }
        if (reservationDto.getDateTo() != null) {
            reservation.setDateTo(reservationDto.getDateTo());
        }
        if (reservationDto.getDateFrom() != null && reservationDto.getDateTo() != null) {
            reservation.setFinalPrice(getTotalPrice(carMapper.toDto(reservation.getCar()), reservationDto));
        }
    }

    private void checkIfPaidAndConfirmed(Long id, Reservation reservation) {
        log.info("Invoked checkIfPaidAndConfirmed method");

        if (reservation.isConfirmed()) {
            throw new ReservationEditNotAllowed(String.format("Cannot update. Reservation with id %s is already confirmed and paid", id));
        }
    }

    private void checkIfFundsBeingReturned(Long reservationId) {
        log.info("Invoked checkIfFundsBeingReturned method");

        if (reservationRepository.paymentExistsForReservationAndStatus(reservationId, PaymentStatus.FUNDS_BEING_REFUNDED) || reservationRepository.paymentExistsForReservationAndStatus(reservationId, PaymentStatus.FUNDS_PAID_BACK)) {
            throw new ReservationEditNotAllowed("Cannot update. Funds being returned and paid back");
        }
    }
}
