package pl.coderslab.carrental.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.carrental.dto.CarDto;
import pl.coderslab.carrental.mapper.CarMapper;
import pl.coderslab.carrental.model.Car;
import pl.coderslab.carrental.model.enum_package.CarStatus;
import pl.coderslab.carrental.repository.CarRepository;
import pl.coderslab.carrental.repository.ReservationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final BrandService brandService;
    private final ReservationRepository reservationRepository;

    public List<CarDto> getAllCars() {

        log.info("Invoked get all cars method");

        return carRepository.findAll()
                .stream()
                .map(carMapper::toDto)
                .toList();
    }

    public List<CarDto> getCarsByBrandAndStatus(String brand, CarStatus carStatus) {

        log.info("Invoked get all cars filtered by brand and car status");

        return carRepository.findByBrandAndStatus(brand, carStatus)
                .stream()
                .map(carMapper::toDto)
                .toList();
    }

    public CarDto getCarById(Long id) {

        log.info("Invoked get car by id method");

        if (id != null) {
            var car = getCarOrElseThrow(id);

            log.info("Returned car by id {}", car);
            return carMapper.toDto(car);
        } else {
            throw new IllegalArgumentException("Car id is null");
        }
    }

    @Transactional
    public CarDto createCar(CarDto carDto) {

        log.info("Invoked create car method");

        if (carDto != null && carDto.getBrand().getId() != null) {
            var brand = brandService.findBrandById(carDto.getBrand().getId());

            var car = carMapper.toEntity(carDto);
            car.setBrand(brand);

            log.info("Car created {}", car);

            return carMapper.toDto(carRepository.save(car));
        } else {
            throw new IllegalArgumentException(String.format("Brand id and car should not be null: %s %s", carDto.getId(), carDto));
        }
    }

    @Transactional
    public CarDto updateCar(Long id, CarDto carDto) {

        log.info("Invoked update car method");

        if (id == null || carDto == null) {
            throw new IllegalArgumentException(String.format("Id and CarDto are required and cannot be null: %s %s", id, carDto));
        }

        var brand = brandService.findBrandById(carDto.getBrand().getId());
        var car = getCarOrElseThrow(id);

        car.setBrand(brand);
        car.setModel(carDto.getModel());
        car.setYear(carDto.getYear());
        car.setCarStatus(carDto.getCarStatus());

        log.info("Car updated {}", car);

        return carMapper.toDto(carRepository.save(car));
    }

    public void deleteCar(Long id) {

        log.info("Invoked delete car method");

        if (id != null) {
            var car = getCarOrElseThrow(id);
            carRepository.delete(car);
            log.info("Car deleted with ID {}", id);

        } else {
            throw new IllegalArgumentException("Cannot delete Car with null ID");
        }
    }

    private Car getCarOrElseThrow(Long id) {

        log.info("Invoked ger car or else throw method for  for id: {}", id);

        return carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Car with id %s not found", id)));
    }

    public void updateCarsAvailabilityByClosedReservationDate() {

        log.info("Invoked update cars availability method");

        var today = LocalDate.now();

        Set<Long> activeCarIds = reservationRepository.findActiveCarIds(today);

        List<Car> carsToUpdate = activeCarIds.isEmpty()
                ? carRepository.findByCarStatusNot(CarStatus.AVAILABLE)
                : carRepository.findByCarStatusNotAndIdNotIn(CarStatus.AVAILABLE, activeCarIds);

        if (!carsToUpdate.isEmpty()) {
            carsToUpdate.forEach(c -> c.setCarStatus(CarStatus.AVAILABLE));
            carRepository.saveAll(carsToUpdate);
        }
    }

}

