package pl.coderslab.carrental.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.coderslab.carrental.repository.CarRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class BrandDeletionPolicy {

    private final CarRepository carRepository;

    boolean canDelete(Long brandId) {

        log.info("Invoked BrandDeletionPolicy canDelete method");

        return !carRepository.existsByBrandId(brandId);
    }
}
