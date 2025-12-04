package com.cloudfitness.service.impl;

import com.cloudfitness.entity.Permission;
import com.cloudfitness.mapper.PermissionMapper;
import com.cloudfitness.service.AdminPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminPermissionServiceImpl implements AdminPermissionService {
    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public Map<String, Object> getPermissions(String permissionName, Integer page, Integer pageSize) {
        List<Permission> permissions = permissionMapper.selectList(null);
        List<Map<String, Object>> list = new ArrayList<>();
        
        for (Permission permission : permissions) {
            if (permissionName != null && !permission.getPermissionName().contains(permissionName)) {
                continue;
            }
            Map<String, Object> item = new HashMap<>();
            item.put("permission_id", permission.getPermissionId());
            item.put("permission_name", permission.getPermissionName());
            item.put("permission_description", permission.getPermissionDescription());
            list.add(item);
        }
        
        int total = list.size();
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        if (fromIndex < total) {
            list = list.subList(fromIndex, toIndex);
        } else {
            list = new ArrayList<>();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("page_size", pageSize);
        return result;
    }

    @Override
    public Map<String, Object> getPermissionDetail(Integer permissionId) {
        Permission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            throw new RuntimeException("权限不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("permission_id", permission.getPermissionId());
        result.put("permission_name", permission.getPermissionName());
        result.put("permission_description", permission.getPermissionDescription());
        
        return result;
    }

    @Override
    public Integer createPermission(String permissionName, String permissionDescription) {
        List<Permission> allPermissions = permissionMapper.selectList(null);
        boolean exists = allPermissions.stream().anyMatch(p -> p.getPermissionName().equals(permissionName));
        if (exists) {
            throw new RuntimeException("权限名称已存在");
        }
        
        Permission permission = new Permission();
        permission.setPermissionName(permissionName);
        permission.setPermissionDescription(permissionDescription);
        permission.setCreatedAt(LocalDateTime.now());
        permission.setUpdatedAt(LocalDateTime.now());
        permissionMapper.insert(permission);
        return permission.getPermissionId();
    }

    @Override
    public void updatePermission(Integer permissionId, String permissionName, String permissionDescription) {
        Permission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            throw new RuntimeException("权限不存在");
        }
        if (permissionName != null) {
            permission.setPermissionName(permissionName);
        }
        if (permissionDescription != null) {
            permission.setPermissionDescription(permissionDescription);
        }
        permission.setUpdatedAt(LocalDateTime.now());
        permissionMapper.updateById(permission);
    }

    @Override
    public void deletePermission(Integer permissionId) {
        Permission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            throw new RuntimeException("权限不存在");
        }
        permissionMapper.deleteById(permissionId);
    }
}

