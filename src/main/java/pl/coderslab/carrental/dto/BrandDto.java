package pl.coderslab.carrental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.coderslab.carrental.model.Brand;
import pl.coderslab.carrental.validator.UniqueValue;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandDto {

    private Long id;
    @NotNull
    @NotBlank
    @UniqueValue(entity = Brand.class, fieldName = "brandName", message = "Brand with this name already exists.")
    private String brandName;
}
