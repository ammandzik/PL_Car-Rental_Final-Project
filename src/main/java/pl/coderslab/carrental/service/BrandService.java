package pl.coderslab.carrental.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.carrental.dto.BrandDto;
import pl.coderslab.carrental.mapper.BrandMapper;
import pl.coderslab.carrental.repository.BrandRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BrandService {

    private static final String BRAND_NOT_FOUND_WITH_ID_S = "Brand with id %s was not found";
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final BrandDeletionPolicy brandDeletionPolicy;


    public List<BrandDto> getAllBrands() {

        log.info("Invoked find all brands method");

        return brandRepository.findAll()
                .stream()
                .map(brandMapper::toDto)
                .toList();
    }

    @Cacheable(value = "brand", key = "#id")
    public BrandDto getBrandById(Long id) {

        log.info("Invoked find by brand id method: {}", id);

        if (id != null) {
            return brandRepository.findById(id)
                    .map(brandMapper::toDto)
                    .orElseThrow(() -> new EntityNotFoundException(String.format(BRAND_NOT_FOUND_WITH_ID_S, id)));
        } else {
            throw new IllegalArgumentException("Brand id is null");
        }
    }

    public BrandDto createBrand(BrandDto brandDto) {

        log.info("Invoked create brand method");

        if (brandDto != null && brandDto.getName() != null) {

            checkIfBrandAlreadyExistsOrElseThrow(brandDto);

            log.info("Saving brand: {}", brandDto);

            var brand = brandMapper.toEntity(brandDto);
            return brandMapper.toDto(brandRepository.save(brand));

        } else {
            throw new IllegalArgumentException("Given brand cannot be null");
        }

    }

    @CachePut(value = "brand", key = "#id")
    public BrandDto updateBrand(Long id, BrandDto brandDto) {
        log.info("Invoked update brand method");

        if (brandDto != null && id != null) {

            var brand = brandRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(String.format(BRAND_NOT_FOUND_WITH_ID_S, id)));

            brand.setBrandName(brandDto.getName());
            return brandMapper.toDto(brandRepository.save(brand));
        } else {
            throw new IllegalArgumentException("Cannot update brand. Given brand and/or id cannot be null");
        }
    }

    @Transactional
    @CachePut(value = "brand", key = "#id")
    public void deleteBrandById(Long id) {

        log.info("Invoked delete brand method");

        if (id != null) {

            if (!brandDeletionPolicy.canDelete(id)) {
                throw new IllegalStateException(String.format("Cannot delete brand: cars are existing for brand with id %s", id));
            }

            brandRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format(BRAND_NOT_FOUND_WITH_ID_S, id)));
            brandRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Cannot delete brand. Given brand and/or id cannot be null");
        }
    }

    private void checkIfBrandAlreadyExistsOrElseThrow(BrandDto brandDto) {

        if (brandRepository.existsByName(brandDto.getName())) {
            throw new EntityExistsException(String.format("Brand with name %s already exists", brandDto.getName()));
        }
    }
}
