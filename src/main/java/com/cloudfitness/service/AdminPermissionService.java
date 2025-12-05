package com.cloudfitness.service;

import java.util.Map;

public interface AdminPermissionService {
    Map<String, Object> getPermissions(String permissionName, Integer page, Integer pageSize);
    Map<String, Object> getPermissionDetail(Integer permissionId);
    Integer createPermission(String permissionName, String permissionDescription);
    void updatePermission(Integer permissionId, String permissionName, String permissionDescription);
    void deletePermission(Integer permissionId);
}


