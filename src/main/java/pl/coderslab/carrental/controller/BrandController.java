package pl.coderslab.carrental.controller;

import org.springframework.web.bind.annotation.*;
import pl.coderslab.carrental.dto.BrandDto;
import pl.coderslab.carrental.service.BrandService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/brands")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public List<BrandDto> getBrands() {

        return brandService.getAllBrands();
    }

    @PostMapping
    public BrandDto createBrand(@RequestBody BrandDto brandDto) {

        return brandService.createBrand(brandDto);
    }

    @DeleteMapping
    public void deleteBrand(@RequestParam Long id) {

        brandService.deleteBrandById(id);
    }
}
