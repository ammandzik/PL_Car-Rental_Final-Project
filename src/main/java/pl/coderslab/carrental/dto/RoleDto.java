package pl.coderslab.carrental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleDto {

    private Long id;
    @NotNull
    @NotBlank
    @Size(min = 5, max = 30)
    private String name;
}
