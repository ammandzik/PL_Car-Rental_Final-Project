package pl.coderslab.carrental.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.coderslab.carrental.dto.RoleDto;
import pl.coderslab.carrental.mapper.RoleMapper;
import pl.coderslab.carrental.repository.RoleRepository;

import java.util.List;

@Service
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    public List<RoleDto> getRoles() {

        log.info("Invoked get all roles method");

        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toDto)
                .toList();
    }

    public RoleDto saveRole(RoleDto roleDto) {

        log.info("Invoked save role method");

        if (roleDto != null) {

            var role = roleMapper.toEntity(roleDto);

            log.info("Saved role: {}", role);
            return roleMapper.toDto(roleRepository.save(role));
        } else {
            throw new IllegalArgumentException("Role is null");
        }
    }

    public RoleDto updateRole(Long id, RoleDto roleDto) {

        log.info("Invoked update role method");

        if (roleDto != null && id != null) {
            var role = roleRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Role with id %s not found", id)));

            role.setName(roleDto.getName());

            log.info("Updated role with id: {}", id);
            return roleMapper.toDto(roleRepository.save(role));
        } else {
            throw new IllegalArgumentException(String.format("Role and/or id is null: %s %s", id, roleDto));
        }
    }

    public void deleteRole(Long id) {

        log.info("Invoked delete role method");
        if (id != null) {
            var role = roleRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Role with id %s not found", id)));
            roleRepository.delete(role);
        }
        log.info("Deleted role with id: {}", id);

    }
}
