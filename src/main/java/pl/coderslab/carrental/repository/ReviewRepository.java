package pl.coderslab.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.coderslab.carrental.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
}
