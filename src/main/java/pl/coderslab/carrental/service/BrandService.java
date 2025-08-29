package pl.coderslab.carrental.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.carrental.dto.BrandDto;
import pl.coderslab.carrental.mapper.BrandMapper;
import pl.coderslab.carrental.model.Brand;
import pl.coderslab.carrental.repository.BrandRepository;

import java.util.List;

@Service
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

    public List<BrandDto> getAllBrands() {
        return brandRepository.findAll()
                .stream()
                .map(brandMapper::toDto)
                .toList();
    }

    public BrandDto getBrandById(Long id) {

        return brandRepository.findById(id)
                .map(brandMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Entity not found with id %s", id)));
    }

    public BrandDto createBrand(BrandDto brandDto) {

        var brand = brandMapper.toEntity(brandDto);
        return brandMapper.toDto(brandRepository.save(brand));

    }

    @Transactional
    public void deleteBrandById(Long id) {

        if (!brandDeletionPolicy.canDelete(id)) {
            throw new IllegalStateException(String.format("Cannot delete brand: cars are existing for brand with id %s", id));
        }
        brandRepository.deleteById(id);
    }
}
