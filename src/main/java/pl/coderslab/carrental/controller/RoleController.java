package pl.coderslab.carrental.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.carrental.dto.RoleDto;
import pl.coderslab.carrental.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<RoleDto>> getRoles() {

        return ResponseEntity.ok(roleService.getRoles());
    }

    @PostMapping
    public ResponseEntity<RoleDto> createRole(@Valid @RequestBody RoleDto roleDto) {

        return new ResponseEntity<>(roleService.saveRole(roleDto), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<RoleDto> updateRole(@RequestParam Long id, @Valid @RequestBody RoleDto roleDto) {

        return new ResponseEntity<>(roleService.updateRole(id, roleDto), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteRole(@RequestParam Long id) {

        roleService.deleteRole(id);
        return new ResponseEntity<>("Role deleted", HttpStatus.NO_CONTENT);
    }
}
