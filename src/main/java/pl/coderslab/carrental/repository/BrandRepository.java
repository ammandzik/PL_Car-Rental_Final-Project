package pl.coderslab.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.coderslab.carrental.model.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    @Query("""
            SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END
            FROM Brand b WHERE b.brandName = :brandName
            """)
    boolean existsByName(String brandName);

}
