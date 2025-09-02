package pl.coderslab.carrental.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import pl.coderslab.carrental.model.Brand;
import pl.coderslab.carrental.model.enum_package.CarStatus;

@Data
@Builder
public class CarDto {

    private Long id;
    @NotNull
    private Brand brand;
    @NotNull
    private CarStatus carStatus;
    @NotNull
    @NotEmpty
    private String model;
    @NotEmpty
    private int year;
    @NotEmpty
    private double pricePerDay;
}
