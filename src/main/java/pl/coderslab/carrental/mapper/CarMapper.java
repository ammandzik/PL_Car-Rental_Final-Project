package pl.coderslab.carrental.mapper;

import org.springframework.stereotype.Component;
import pl.coderslab.carrental.dto.CarDto;
import pl.coderslab.carrental.model.Car;

@Component
public class CarMapper {

    public CarDto toDto(Car car) {

        return CarDto.builder()
                .id(car.getId())
                .model(car.getModel())
                .year(car.getYear())
                .brand(car.getBrand())
                .carStatus(car.getCarStatus())
                .pricePerDay(car.getPricePerDay())
                .build();
    }

    public Car toEntity(CarDto carDto) {

        return Car.builder()
                .id(carDto.getId())
                .model(carDto.getModel())
                .year(carDto.getYear())
                .brand(carDto.getBrand())
                .carStatus(carDto.getCarStatus())
                .pricePerDay(carDto.getPricePerDay())
                .build();
    }
}
