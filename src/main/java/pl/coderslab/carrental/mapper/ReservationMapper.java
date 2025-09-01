package pl.coderslab.carrental.mapper;

import org.springframework.stereotype.Component;
import pl.coderslab.carrental.dto.ReservationDto;
import pl.coderslab.carrental.model.Reservation;

@Component
public class ReservationMapper {

    public ReservationDto toDto(Reservation reservation) {

        return ReservationDto.builder()
                .id(reservation.getId())
                .carId(reservation.getId())
                .userId(reservation.getUser().getId())
                .confirmed(reservation.isConfirmed())
                .dateFrom(reservation.getDateFrom())
                .dateTo(reservation.getDateTo())
                .build();
    }

    public Reservation toEntity(ReservationDto reservationDto) {

        return Reservation.builder()
                .id(reservationDto.getId())
                .confirmed(reservationDto.isConfirmed())
                .dateFrom(reservationDto.getDateFrom())
                .dateTo(reservationDto.getDateTo())
                .build();
    }
}
