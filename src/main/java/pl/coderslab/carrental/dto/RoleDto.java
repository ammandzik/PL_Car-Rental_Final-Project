package pl.coderslab.carrental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.coderslab.carrental.model.Role;
import pl.coderslab.carrental.validator.UniqueValue;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {

    private Long id;
    @NotNull
    @NotBlank
    @Size(min = 5, max = 30)
    @UniqueValue(entity = Role.class, fieldName = "name", message = "Role with this name already exists")
    private String name;
}
