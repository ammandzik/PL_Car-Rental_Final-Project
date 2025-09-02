package pl.coderslab.carrental.mapper;

import org.springframework.stereotype.Component;
import pl.coderslab.carrental.dto.RoleDto;
import pl.coderslab.carrental.model.Role;

@Component
public class RoleMapper {

    public RoleDto toDto(Role role) {

        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

    public Role toEntity(RoleDto roleDto) {

        return Role.builder()
                .id(roleDto.getId())
                .name(roleDto.getName())
                .build();
    }
}
