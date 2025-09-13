package pl.coderslab.carrental.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.coderslab.carrental.repository.CarRepository;
import pl.coderslab.carrental.repository.UserRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeletionPolicy {

    private final CarRepository carRepository;
    private final UserRepository userRepository;

    boolean canDeleteBrand(Long brandId) {

        log.info("Invoked BrandDeletionPolicy canDeleteBrand method");

        return !carRepository.existsByBrandId(brandId);
    }
//    boolean canDeleteRole(Long roleId) {
//        log.info("Invoked canDeleteRole method");
//
//        return !userRepository.existsByRole(roleId);
//    }
}
