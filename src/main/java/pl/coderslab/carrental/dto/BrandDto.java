package pl.coderslab.carrental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BrandDto {

    private Long id;
    @NotNull
    @NotBlank
    private String name;
}
