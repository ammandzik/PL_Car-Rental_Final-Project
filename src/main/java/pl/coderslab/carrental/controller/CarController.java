package pl.coderslab.carrental.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.carrental.dto.CarDto;
import pl.coderslab.carrental.model.enum_package.CarStatus;
import pl.coderslab.carrental.service.CarService;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public ResponseEntity<List<CarDto>> getCars(@RequestParam(required = false) String brand,
                                                @RequestParam(required = false) CarStatus carStatus) {

        if (brand == null && carStatus == null) {
            return new ResponseEntity<>(carService.getAllCars(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(carService.getCarsByBrandAndStatus(brand, carStatus), HttpStatus.OK);
        }
    }

    @GetMapping("/car")
    public ResponseEntity<CarDto> getCar(@RequestParam Long id) {

        return new ResponseEntity<>(carService.getCarById(id), HttpStatus.OK);
    }

    @PostMapping("admin/car")
    public ResponseEntity<CarDto> createCar(@RequestParam String brandName, @RequestBody CarDto carDto) {

        return new ResponseEntity<>(carService.createCar(brandName, carDto), HttpStatus.CREATED);
    }

    @PutMapping("admin/car")
    public ResponseEntity<CarDto> updateCar(@RequestParam Long id, @RequestBody CarDto carDto) {

        return new ResponseEntity<>(carService.updateCar(id, carDto), HttpStatus.OK);
    }

    @DeleteMapping("admin/car")
    public ResponseEntity<String> deleteCar(@RequestParam Long id) {

        carService.deleteCar(id);
        return new ResponseEntity<>("Car deleted", HttpStatus.OK);
    }
}
