package pl.coderslab.carrental.service;

import org.springframework.stereotype.Service;
import pl.coderslab.carrental.model.Brand;
import pl.coderslab.carrental.repository.BrandRepository;

@Service
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public Brand findOrCreateBrand(String name) {

        var byNameIgnoreCase = brandRepository.findByBrandNameIgnoreCase(name);

        if (byNameIgnoreCase.isPresent()) {
            return byNameIgnoreCase.get();

        } else {
            var brand = new Brand();
            brand.setBrandName(name);
            return brandRepository.save(brand);
        }
    }

    public boolean existsByName(String name) {
        return brandRepository.existsByBrandNameIgnoreCase(name);
    }
}
