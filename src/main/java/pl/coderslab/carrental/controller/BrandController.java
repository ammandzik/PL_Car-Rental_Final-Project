package pl.coderslab.carrental.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<BrandDto>> getBrands() {

        return new ResponseEntity<>(brandService.getAllBrands(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<BrandDto> createBrand(@RequestBody BrandDto brandDto) {

        return new ResponseEntity<>(brandService.createBrand(brandDto), HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteBrand(@RequestParam Long id) {

        brandService.deleteBrandById(id);

        return new ResponseEntity<>("Brand removed", HttpStatus.OK);
    }

    @GetMapping("/brand")
    public ResponseEntity<BrandDto> getBrandById(@RequestParam Long id) {

        return new ResponseEntity<>(brandService.getBrandById(id), HttpStatus.OK);
    }

    @PutMapping("/brand")
    public ResponseEntity<BrandDto> updateBrand(@RequestParam Long id, @RequestBody BrandDto brandDto) {

        return new ResponseEntity<>(brandService.updateBrand(id, brandDto), HttpStatus.OK);
    }
}
