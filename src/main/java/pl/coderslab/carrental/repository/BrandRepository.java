package pl.coderslab.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.coderslab.carrental.model.Brand;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findById(Long id);
    Optional<Brand> findByBrandNameIgnoreCase(String name);
    boolean existsByBrandNameIgnoreCase(String name);
}
