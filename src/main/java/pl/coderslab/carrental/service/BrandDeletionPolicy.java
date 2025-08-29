package pl.coderslab.carrental.service;

import org.springframework.stereotype.Component;
import pl.coderslab.carrental.repository.CarRepository;

@Component
public class BrandDeletionPolicy {

    private final CarRepository carRepository;

    public BrandDeletionPolicy(CarRepository carRepo) {
        this.carRepository = carRepo;
    }

    boolean canDelete(Long brandId) {
        return !carRepository.existsByBrandId(brandId);
    }
}
