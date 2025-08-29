package pl.coderslab.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.coderslab.carrental.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
