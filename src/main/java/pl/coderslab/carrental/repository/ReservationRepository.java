package pl.coderslab.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.coderslab.carrental.model.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
