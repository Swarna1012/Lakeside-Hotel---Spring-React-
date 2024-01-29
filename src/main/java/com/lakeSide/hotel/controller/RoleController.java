package com.lakeSide.hotel.controller;

import com.lakeSide.hotel.exception.RoleAlreadyExistsException;
import com.lakeSide.hotel.model.Role;
import com.lakeSide.hotel.model.User;
import com.lakeSide.hotel.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Role>> getRoles(){
        return new ResponseEntity<>(roleService.getRoles(), HttpStatus.FOUND);
    }

    @PostMapping("/create-role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> createRole(@RequestBody Role role){
        try {
            roleService.createRole(role);
            return ResponseEntity.ok("Role created successfully");
        }catch (RoleAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{roleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteRole(@PathVariable("roleId") Long roleId){
        roleService.deleteRole(roleId);
    }

    @PostMapping("/remove/{roleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Role removeAllUserFromRole(@PathVariable("roleId") Long roleId){
        return roleService.removeAllUserFromRole(roleId);
    }

    @PostMapping("/remove-user-from-role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public User removeUserFromRole(@RequestParam("userId") Long userId,
                                   @RequestParam("roleId") Long roleId){
        return roleService.removeUserFromRole(userId, roleId);
    }

    @PostMapping("/assign-role-to-user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public User assignRoleToUser(@RequestParam("userId") Long userId,
                                   @RequestParam("roleId") Long roleId){
        return roleService.assignRoleToUser(userId, roleId);
    }


}
