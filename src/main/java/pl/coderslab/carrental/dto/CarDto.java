package pl.coderslab.carrental.dto;

import lombok.Builder;
import lombok.Data;
import pl.coderslab.carrental.model.Brand;
import pl.coderslab.carrental.model.enum_package.CarStatus;

@Data
@Builder
public class CarDto {

    private Long id;
    private Brand brand;
    private CarStatus carStatus;
    private String model;
    private int year;
}
