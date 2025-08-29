package pl.coderslab.carrental.dto;

import lombok.Builder;
import lombok.Data;
import pl.coderslab.carrental.model.Brand;

@Data
@Builder
public class CarDto {

    private Long id;
    private String brandName;
    private Brand brand;
    private String carStatus;
    private String model;
    private int year;
}
