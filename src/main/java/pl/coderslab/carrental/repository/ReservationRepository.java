package pl.coderslab.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.coderslab.carrental.model.Reservation;

import java.time.LocalDate;
import java.util.Set;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
            select distinct r.car.id
               from Reservation r
               where r.dateFrom <= :today
                 and r.dateTo   >= :today
                  and r.confirmed = true
            """)
    Set<Long> findActiveCarIds(@Param("today") LocalDate today);

    @Query("select count(r) > 0 from Reservation r where r.car.id = :carId and r.dateTo >= :dateNow")
    boolean existsByCarIdWithFutureDate(Long carId, LocalDate dateNow);
}
