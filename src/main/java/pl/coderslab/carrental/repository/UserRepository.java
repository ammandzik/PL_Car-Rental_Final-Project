package pl.coderslab.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.coderslab.carrental.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select count(u) > 0 from User u where u.email = :email")
    boolean existsByEmail(String email);

    @Query("select count(u) < 1 from User u where u.id = :userId")
    boolean userIdDiffers(Long userId);
}
