package pl.coderslab.carrental.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import pl.coderslab.carrental.dto.ReservationDto;
import pl.coderslab.carrental.mapper.CarMapper;
import pl.coderslab.carrental.mapper.ReservationMapper;
import pl.coderslab.carrental.mapper.UserMapper;
import pl.coderslab.carrental.repository.ReservationRepository;

import java.util.List;

@Service
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
        return reservationRepository.findAll()
                .stream()
                .map(reservationMapper::toDto)
                .toList();
    }

    public ReservationDto findById(Long id) {

        return reservationRepository.findById(id)
                .map(reservationMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Reservation not found with id %s", id)));
    }

    public ReservationDto save(ReservationDto reservationDto) {

        var user = userService.findById(reservationDto.getUserId());
        var car = carService.getCarById(reservationDto.getCarId());
        var reservation = reservationMapper.toEntity(reservationDto);

        reservation.setUser(userMapper.toUser(user));
        reservation.setCar(carMapper.toEntity(car));

        return reservationMapper.toDto(reservationRepository.save(reservation));
    }
}
