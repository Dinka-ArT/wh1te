package com.cloudfitness.service.impl;

import com.cloudfitness.entity.Permission;
import com.cloudfitness.entity.Role;
import com.cloudfitness.entity.RolePermission;
import com.cloudfitness.mapper.PermissionMapper;
import com.cloudfitness.mapper.RoleMapper;
import com.cloudfitness.mapper.RolePermissionMapper;
import com.cloudfitness.service.AdminRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminRoleServiceImpl implements AdminRoleService {
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private PermissionMapper permissionMapper;
    
    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public Map<String, Object> getRoles(String roleName, Integer page, Integer pageSize) {
        List<Role> roles = roleMapper.selectList(null);
        List<Map<String, Object>> list = new ArrayList<>();
        
        for (Role role : roles) {
            if (roleName != null && !role.getRoleName().contains(roleName)) {
                continue;
            }
            Map<String, Object> item = new HashMap<>();
            item.put("role_id", role.getRoleId());
            item.put("role_name", role.getRoleName());
            item.put("role_description", role.getRoleDescription());
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
    public Map<String, Object> getRoleDetail(Integer roleId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("role_id", role.getRoleId());
        result.put("role_name", role.getRoleName());
        result.put("role_description", role.getRoleDescription());
        
        List<Integer> permissionIds = rolePermissionMapper.selectPermissionIdsByRoleId(roleId);
        List<Map<String, Object>> permissions = new ArrayList<>();
        for (Integer permissionId : permissionIds) {
            Permission permission = permissionMapper.selectById(permissionId);
            if (permission != null) {
                Map<String, Object> perm = new HashMap<>();
                perm.put("permission_id", permission.getPermissionId());
                perm.put("permission_name", permission.getPermissionName());
                perm.put("permission_description", permission.getPermissionDescription());
                permissions.add(perm);
            }
        }
        result.put("permissions", permissions);
        
        return result;
    }

    @Override
    public Integer createRole(String roleName, String roleDescription) {
        List<Role> allRoles = roleMapper.selectList(null);
        boolean exists = allRoles.stream().anyMatch(r -> r.getRoleName().equals(roleName));
        if (exists) {
            throw new RuntimeException("角色名称已存在");
        }
        
        Role role = new Role();
        role.setRoleName(roleName);
        role.setRoleDescription(roleDescription);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        roleMapper.insert(role);
        return role.getRoleId();
    }

    @Override
    public void updateRole(Integer roleId, String roleName, String roleDescription) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        if (roleName != null) {
            role.setRoleName(roleName);
        }
        if (roleDescription != null) {
            role.setRoleDescription(roleDescription);
        }
        role.setUpdatedAt(LocalDateTime.now());
        roleMapper.updateById(role);
    }

    @Override
    public void assignPermissions(Integer roleId, List<Integer> permissionIds) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        
        rolePermissionMapper.deleteByRoleId(roleId);
        
        for (Integer permissionId : permissionIds) {
            Permission permission = permissionMapper.selectById(permissionId);
            if (permission == null) {
                continue;
            }
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            rolePermission.setCreatedAt(LocalDateTime.now());
            rolePermissionMapper.insert(rolePermission);
        }
    }

    @Override
    public void deleteRole(Integer roleId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        rolePermissionMapper.deleteByRoleId(roleId);
        roleMapper.deleteById(roleId);
    }
}

