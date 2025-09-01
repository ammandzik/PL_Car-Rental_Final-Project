package pl.coderslab.carrental.dto;

import lombok.Builder;
import lombok.Data;
import pl.coderslab.carrental.model.Role;

import java.util.Set;

@Data
@Builder
public class UserDto {

    private Long id;
    private String username;
    private String name;
    private String surname;
    private String phone;
    private String email;
    private String password;
    private Set<Role> roles;
}
