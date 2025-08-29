package pl.coderslab.carrental.controller;

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
    public List<CarDto> getCars(@RequestParam(required = false) String brand,
                                @RequestParam(required = false) CarStatus carStatus) {

        if (brand == null && carStatus == null) {
            return carService.getAllCars();
        } else {
            return carService.getCarsByBrandAndStatus(brand, carStatus);
        }
    }

    @GetMapping("/car")
    public CarDto getCar(@RequestParam Long id) {

        return carService.getCarById(id);
    }

    @PostMapping("admin/car")
    public CarDto createCar(@RequestParam String brandName, @RequestBody CarDto carDto) {
        return carService.createCar(brandName, carDto);
    }

    @PutMapping("admin/car")
    public CarDto updateCar(@RequestParam Long id, @RequestBody CarDto carDto) {

        return carService.updateCar(id, carDto);
    }

    @DeleteMapping("admin/car")
    public void deleteCar(@RequestParam Long id) {

        carService.deleteCar(id);
    }
}
