package com.cloudfitness.controller.admin;

import com.cloudfitness.common.Result;
import com.cloudfitness.service.AdminPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/admin/permissions")
public class AdminPermissionController {
    @Autowired
    private AdminPermissionService adminPermissionService;

    @GetMapping
    public Result<Map<String, Object>> getPermissions(
            @RequestParam(required = false) String permission_name,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer page_size) {
        Map<String, Object> data = adminPermissionService.getPermissions(permission_name, page, page_size);
        return Result.success(data);
    }

    @GetMapping("/{permission_id}")
    public Result<Map<String, Object>> getPermissionDetail(@PathVariable("permission_id") Integer permissionId) {
        Map<String, Object> data = adminPermissionService.getPermissionDetail(permissionId);
        return Result.success(data);
    }

    @PostMapping
    public Result<Map<String, Integer>> createPermission(@RequestBody Map<String, String> request) {
        String permissionName = request.get("permission_name");
        String permissionDescription = request.get("permission_description");
        Integer permissionId = adminPermissionService.createPermission(permissionName, permissionDescription);
        Map<String, Integer> data = Map.of("permission_id", permissionId);
        return Result.success("创建成功", data);
    }

    @PutMapping("/{permission_id}")
    public Result<Object> updatePermission(@PathVariable("permission_id") Integer permissionId, @RequestBody Map<String, String> request) {
        String permissionName = request.get("permission_name");
        String permissionDescription = request.get("permission_description");
        adminPermissionService.updatePermission(permissionId, permissionName, permissionDescription);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{permission_id}")
    public Result<Object> deletePermission(@PathVariable("permission_id") Integer permissionId) {
        adminPermissionService.deletePermission(permissionId);
        return Result.success("删除成功", null);
    }
}


