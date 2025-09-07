package pl.coderslab.carrental.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pl.coderslab.carrental.dto.RoleDto;
import pl.coderslab.carrental.mapper.RoleMapper;
import pl.coderslab.carrental.repository.RoleRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Cacheable(value = "role", key = "#id")
    public RoleDto getRoleById(Long id) {
        var role = roleRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return roleMapper.toDto(role);
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

            System.out.println(roleRepository.existsByName(roleDto.getName()));

            if (roleRepository.existsByName(roleDto.getName())) {
                throw new EntityExistsException(String.format("Role with name %s already exists", roleDto.getName()));
            }

            var role = roleMapper.toEntity(roleDto);
            roleRepository.save(role);

            log.info("Saved role: {}", role);
            return roleMapper.toDto(role);
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
