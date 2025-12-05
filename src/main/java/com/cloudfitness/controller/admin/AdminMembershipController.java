package com.cloudfitness.controller.admin;

import com.cloudfitness.common.Result;
import com.cloudfitness.service.AdminMembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/admin/memberships")
public class AdminMembershipController {
    @Autowired
    private AdminMembershipService adminMembershipService;

    @GetMapping
    public Result<Map<String, Object>> getMemberships(
            @RequestParam(required = false) Integer user_id,
            @RequestParam(required = false) String membership_type,
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String end_date,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer page_size) {
        Map<String, Object> data = adminMembershipService.getMemberships(
            user_id, membership_type, start_date, end_date, page, page_size);
        return Result.success(data);
    }

    @GetMapping("/{membership_id}")
    public Result<Map<String, Object>> getMembershipDetail(@PathVariable("membership_id") Integer membershipId) {
        Map<String, Object> data = adminMembershipService.getMembershipDetail(membershipId);
        return Result.success(data);
    }

    @PostMapping
    public Result<Map<String, Integer>> createMembership(@RequestBody Map<String, Object> request) {
        Integer userId = (Integer) request.get("user_id");
        String membershipType = (String) request.get("membership_type");
        String startDate = (String) request.get("start_date");
        String expiryDate = (String) request.get("expiry_date");
        Integer membershipId = adminMembershipService.createMembership(userId, membershipType, startDate, expiryDate);
        Map<String, Integer> data = Map.of("membership_id", membershipId);
        return Result.success("创建成功", data);
    }

    @PutMapping("/{membership_id}")
    public Result<Object> updateMembership(@PathVariable("membership_id") Integer membershipId, @RequestBody Map<String, Object> request) {
        String membershipType = (String) request.get("membership_type");
        String startDate = (String) request.get("start_date");
        String expiryDate = (String) request.get("expiry_date");
        adminMembershipService.updateMembership(membershipId, membershipType, startDate, expiryDate);
        return Result.success("更新成功", null);
    }

    @PutMapping("/{membership_id}/renew")
    public Result<Map<String, String>> renewMembership(@PathVariable("membership_id") Integer membershipId, @RequestBody Map<String, Object> request) {
        String expiryDate = (String) request.get("expiry_date");
        adminMembershipService.renewMembership(membershipId, expiryDate);
        Map<String, String> data = Map.of("new_expiry_date", expiryDate);
        return Result.success("续费成功", data);
    }

    @DeleteMapping("/{membership_id}")
    public Result<Object> deleteMembership(@PathVariable("membership_id") Integer membershipId) {
        adminMembershipService.deleteMembership(membershipId);
        return Result.success("删除成功", null);
    }
}


