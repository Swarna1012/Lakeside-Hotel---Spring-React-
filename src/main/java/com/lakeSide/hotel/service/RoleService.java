package com.lakeSide.hotel.service;

import com.lakeSide.hotel.model.Role;
import com.lakeSide.hotel.model.User;

import java.util.List;

public interface RoleService {

    List<Role> getRoles();

    Role createRole(Role theRole);

    void deleteRole(Long id);

    Role findByName(String name);

    User removeUserFromRole(Long userId, Long roleId);

    User assignRoleToUser(Long userId, Long roleId);

    Role removeAllUserFromRole(Long roleId);
}
