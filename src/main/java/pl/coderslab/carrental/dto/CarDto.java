package pl.coderslab.carrental.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.coderslab.carrental.model.Brand;
import pl.coderslab.carrental.model.enum_package.CarStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarDto {

    private Long id;
    @NotNull
    private Brand brand;
    @NotNull
    private CarStatus carStatus;
    @NotNull
    @NotEmpty
    @Size(min = 1, max = 20)
    private String model;
    @NotNull
    private int year;
    @NotNull
    private double pricePerDay;
}
