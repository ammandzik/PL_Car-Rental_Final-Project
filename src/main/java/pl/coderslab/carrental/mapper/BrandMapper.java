package pl.coderslab.carrental.mapper;

import org.springframework.stereotype.Controller;
import pl.coderslab.carrental.dto.BrandDto;
import pl.coderslab.carrental.model.Brand;

@Controller
public class BrandMapper {

    public BrandDto toDto(Brand brand) {

        return BrandDto.builder()
                .id(brand.getId())
                .name(brand.getBrandName())
                .build();
    }
     public Brand toEntity(BrandDto brandDto) {

        return Brand.builder()
                .id(brandDto.getId())
                .brandName(brandDto.getName())
                .build();
     }
}
