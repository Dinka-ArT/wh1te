package com.cloudfitness.service;

import java.util.List;
import java.util.Map;

public interface AdminRoleService {
    Map<String, Object> getRoles(String roleName, Integer page, Integer pageSize);
    Map<String, Object> getRoleDetail(Integer roleId);
    Integer createRole(String roleName, String roleDescription);
    void updateRole(Integer roleId, String roleName, String roleDescription);
    void assignPermissions(Integer roleId, List<Integer> permissionIds);
    void deleteRole(Integer roleId);
}


