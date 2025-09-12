package pl.coderslab.carrental.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.coderslab.carrental.model.Role;
import pl.coderslab.carrental.model.User;
import pl.coderslab.carrental.validator.UniqueValue;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    @NotNull
    @Size(min = 2, max = 35)
    private String name;
    @NotNull
    @Size(min = 2, max = 35)
    private String surname;
    @NotNull
    @Size(min = 7, max = 20)
    private String phone;
    @NotNull
    @Email
    @UniqueValue(entity = User.class, fieldName = "email", message = "User with this email already exists.")
    private String email;
    @NotNull
    private Set<Role> roles;
}
