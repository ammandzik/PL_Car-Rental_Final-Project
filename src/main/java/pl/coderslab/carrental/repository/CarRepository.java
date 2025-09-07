package pl.coderslab.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.coderslab.carrental.model.Car;
import pl.coderslab.carrental.model.enum_package.CarStatus;

import java.util.Collection;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("""
            SELECT c FROM Car c
            WHERE (:brand IS NULL OR c.brand.brandName = :brand)
            AND (:carStatus IS NULL OR c.carStatus = :carStatus)
            """)
    List<Car> findByBrandAndStatus(String brand, CarStatus carStatus);
    boolean existsByBrandId(Long brandId);
    List<Car> findByCarStatusNot(CarStatus status);
    List<Car> findByCarStatusNotAndIdNotIn(CarStatus status, Collection<Long> idNotIn);

}
