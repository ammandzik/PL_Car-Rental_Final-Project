package pl.coderslab.carrental.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.carrental.dto.BrandDto;
import pl.coderslab.carrental.mapper.BrandMapper;
import pl.coderslab.carrental.model.Brand;
import pl.coderslab.carrental.repository.BrandRepository;

import java.util.List;

@Service
@Slf4j
public class BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final BrandDeletionPolicy brandDeletionPolicy;

    public BrandService(BrandRepository brandRepository, BrandMapper brandMapper, BrandDeletionPolicy brandDeletionPolicy) {
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
        this.brandDeletionPolicy = brandDeletionPolicy;
    }

    public Brand findOrCreateBrand(String name) {

        log.info("Invoked find or create brand method by brand name: {}", name);

        var byNameIgnoreCase = brandRepository.findByBrandNameIgnoreCase(name);

        if (byNameIgnoreCase.isPresent()) {

            log.info("Found brand with name {}", byNameIgnoreCase.get());

            return byNameIgnoreCase.get();

        } else {

            var brand = new Brand();
            brand.setBrandName(name);
            return brandRepository.save(brand);
        }
    }

    public boolean existsByName(String name) {

        log.info("Invoked exists by brand name method: {}", name);

        return brandRepository.existsByBrandNameIgnoreCase(name);
    }

    public List<BrandDto> getAllBrands() {

        log.info("Invoked find all brands method");

        return brandRepository.findAll()
                .stream()
                .map(brandMapper::toDto)
                .toList();
    }

    public BrandDto getBrandById(Long id) {

        log.info("Invoked find by brand id method: {}", id);

        return brandRepository.findById(id)
                .map(brandMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Brand not found with id %s", id)));
    }

    public BrandDto createBrand(BrandDto brandDto) {

        log.info("Invoked create brand method");

        if (brandDto != null && brandDto.getName() != null) {

            log.info("Saving brand: {}", brandDto);

            var brand = brandMapper.toEntity(brandDto);
            return brandMapper.toDto(brandRepository.save(brand));

        } else {
            throw new IllegalArgumentException("Given brand cannot be null");
        }

    }

    @Transactional
    public void deleteBrandById(Long id) {

        log.info("Invoked delete brand method");

        if (!brandDeletionPolicy.canDelete(id)) {
            throw new IllegalStateException(String.format("Cannot delete brand: cars are existing for brand with id %s", id));
        }
        brandRepository.deleteById(id);
    }
}
