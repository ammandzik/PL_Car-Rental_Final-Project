package pl.coderslab.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.coderslab.carrental.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
            SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
            FROM User u
            WHERE u.email = :email
            """)
    boolean existsByEmail(String email);

    @Query("""
            SELECT CASE WHEN COUNT(u) < 1 THEN true ELSE false END
            FROM User u
            WHERE u.id = :userId
            """)
    boolean userIdDiffers(Long userId);

}
