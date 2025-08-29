package pl.coderslab.carrental.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.carrental.dto.CarDto;
import pl.coderslab.carrental.mapper.CarMapper;
import pl.coderslab.carrental.model.Car;
import pl.coderslab.carrental.model.enum_package.CarStatus;
import pl.coderslab.carrental.repository.CarRepository;

import java.util.List;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final BrandService brandService;

    public CarService(CarRepository carRepository, CarMapper carMapper, BrandService brandService) {
        this.carRepository = carRepository;
        this.carMapper = carMapper;
        this.brandService = brandService;
    }

    public List<CarDto> getAllCars() {
        return carRepository.findAll()
                .stream()
                .map(carMapper::toDto)
                .toList();
    }

    public List<CarDto> getCarsByBrandAndStatus(String brand, CarStatus carStatus) {

        return carRepository.findByBrandAndStatus(brand, carStatus)
                .stream()
                .map(carMapper::toDto)
                .toList();
    }

    public CarDto getCarById(Long id) {

        var car = getCarOrElseThrow(id);

        return carMapper.toDto(car);
    }

    @Transactional
    public CarDto createCar(String brandName, CarDto carDto) {

        var brand = brandService.findOrCreateBrand(brandName);

        var car = carMapper.toEntity(carDto);
        car.setBrand(brand);

        return carMapper.toDto(carRepository.save(car));
    }

    @Transactional
    public CarDto updateCar(Long id, CarDto carDto) {

        if (brandService.existsByName(carDto.getBrand().getBrandName())) {

            var car = getCarOrElseThrow(id);

            car.setBrand(carDto.getBrand());
            car.setModel(carDto.getModel());
            car.setYear(carDto.getYear());
            car.setCarStatus(carDto.getCarStatus());
            return carMapper.toDto(carRepository.save(car));

        } else
            throw new EntityNotFoundException(String.format("Could not update car due to non existing brand %s", carDto.getBrand().getBrandName()));
    }

    public void deleteCar(Long id) {

        var car = getCarOrElseThrow(id);
        carRepository.delete(car);
    }

    private Car getCarOrElseThrow(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Car with id %s not found", id)));
    }

}

