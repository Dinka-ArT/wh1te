package com.cloudfitness.controller.admin;

import com.cloudfitness.common.Result;
import com.cloudfitness.service.AdminRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/roles")
public class AdminRoleController {
    @Autowired
    private AdminRoleService adminRoleService;

    @GetMapping
    public Result<Map<String, Object>> getRoles(
            @RequestParam(required = false) String role_name,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer page_size) {
        Map<String, Object> data = adminRoleService.getRoles(role_name, page, page_size);
        return Result.success(data);
    }

    @GetMapping("/{role_id}")
    public Result<Map<String, Object>> getRoleDetail(@PathVariable("role_id") Integer roleId) {
        Map<String, Object> data = adminRoleService.getRoleDetail(roleId);
        return Result.success(data);
    }

    @PostMapping
    public Result<Map<String, Integer>> createRole(@RequestBody Map<String, String> request) {
        String roleName = request.get("role_name");
        String roleDescription = request.get("role_description");
        Integer roleId = adminRoleService.createRole(roleName, roleDescription);
        Map<String, Integer> data = Map.of("role_id", roleId);
        return Result.success("创建成功", data);
    }

    @PutMapping("/{role_id}")
    public Result<Object> updateRole(@PathVariable("role_id") Integer roleId, @RequestBody Map<String, String> request) {
        String roleName = request.get("role_name");
        String roleDescription = request.get("role_description");
        adminRoleService.updateRole(roleId, roleName, roleDescription);
        return Result.success("更新成功", null);
    }

    @PutMapping("/{role_id}/permissions")
    public Result<Object> assignPermissions(@PathVariable("role_id") Integer roleId, @RequestBody Map<String, List<Integer>> request) {
        List<Integer> permissionIds = request.get("permission_ids");
        adminRoleService.assignPermissions(roleId, permissionIds);
        return Result.success("分配成功", null);
    }

    @DeleteMapping("/{role_id}")
    public Result<Object> deleteRole(@PathVariable("role_id") Integer roleId) {
        adminRoleService.deleteRole(roleId);
        return Result.success("删除成功", null);
    }
}

