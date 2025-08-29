package pl.coderslab.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.coderslab.carrental.model.Car;
import pl.coderslab.carrental.model.enum_package.CarStatus;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("select c from Car c where (:brand IS NULL or c.brand.brandName = :brand) and (:carStatus IS NULL OR c.carStatus = :carStatus)")
    List<Car> findByBrandAndStatus(@Param("brand") String brand, @Param("carStatus") CarStatus carStatus);

    boolean existsByBrandId(Long brandId);

}
