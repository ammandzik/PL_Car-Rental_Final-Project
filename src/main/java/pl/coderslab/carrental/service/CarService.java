package pl.coderslab.carrental.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.carrental.dto.CarDto;
import pl.coderslab.carrental.mapper.BrandMapper;
import pl.coderslab.carrental.mapper.CarMapper;
import pl.coderslab.carrental.model.Car;
import pl.coderslab.carrental.model.Reservation;
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
    private final BrandMapper brandMapper;
    private final DeletionPolicy deletionPolicy;

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

    @Cacheable(value = "car", key = "#id")
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
    public CarDto saveCar(CarDto carDto) {

        log.info("Invoked create car method");

        if (carDto != null && carDto.getBrand().getId() != null) {

            var brand = brandService.getBrandById(carDto.getBrand().getId());
            var car = carMapper.toEntity(carDto);
            car.setBrand(brandMapper.toEntity(brand));
            car.setCarStatus(CarStatus.AVAILABLE);

            log.info("Car created {}", car);

            return carMapper.toDto(carRepository.save(car));
        } else {
            throw new IllegalArgumentException("Brand id and car should not be null");
        }
    }

    @Transactional
    @CacheEvict(value = "car", key = "#id")
    public CarDto updateCar(Long id, CarDto carDto) {

        log.info("Invoked update car method");

        if (id != null || carDto != null) {

            var brand = brandService.getBrandById(carDto.getBrand().getId());
            var car = getCarOrElseThrow(id);

            car.setBrand(brandMapper.toEntity(brand));
            car.setModel(carDto.getModel());
            car.setYear(carDto.getYear());
            car.setCarStatus(carDto.getCarStatus());
            car.setPricePerDay(carDto.getPricePerDay());

            log.info("Car updated {}", car);

            return carMapper.toDto(carRepository.save(car));
        } else {
            throw new IllegalArgumentException("Id and CarDto are required and cannot be null: %s %s");
        }
    }

    @CacheEvict(value = "car", key = "#id")
    public void deleteCar(Long id) {

        log.info("Invoked delete car method");

        if (!deletionPolicy.canDeleteCar(id)) {
            throw new IllegalStateException(String.format("Cannot delete car: reservations are existing for car with id %s", id));
        }

        if (id != null) {

            var car = getCarOrElseThrow(id);
            carRepository.delete(car);

            log.info("Car deleted with ID {}", id);

        } else {
            throw new IllegalArgumentException("Cannot delete Car with null ID");
        }
    }

    @Transactional
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

            log.info("Cars updated {}", carsToUpdate);
        }
    }

    @Transactional
    public void updateCarsToRentedForActiveReservations() {

        log.info("Invoked update cars to rented for active reservations");

        var today = LocalDate.now();

        List<Reservation> activeReservations = reservationRepository.findActiveReservations(today);

        activeReservations.stream()
                .map(Reservation::getCar)
                .forEach(c -> {
                    c.setCarStatus(CarStatus.RENTED);
                    carRepository.save(c);
                });

        log.info("Active reservations car status updated {}", activeReservations);
    }

    private Car getCarOrElseThrow(Long id) {

        log.info("Invoked ger car or else throw method for  for id: {}", id);

        return carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Car with id %s not found", id)));
    }
}

