package pl.coderslab.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.coderslab.carrental.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("select count(r) > 0 from Role r where r.name = :name")
    boolean existsByName(String name);
}
