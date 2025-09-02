package pl.coderslab.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.coderslab.carrental.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("select count(r) > 0 from Review r where r.reservation.id = :reservationId")
    boolean existsByReservationId(Long reservationId);
}
