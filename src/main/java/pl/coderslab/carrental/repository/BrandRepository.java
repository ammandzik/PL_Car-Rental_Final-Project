package pl.coderslab.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.coderslab.carrental.model.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
}
