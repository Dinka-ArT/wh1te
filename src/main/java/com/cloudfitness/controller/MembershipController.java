package com.cloudfitness.controller;

import com.cloudfitness.common.Result;
import com.cloudfitness.entity.Membership;
import com.cloudfitness.service.MembershipService;
import com.cloudfitness.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/memberships")
public class MembershipController {
    @Autowired
    private MembershipService membershipService;
    
    @Autowired
    private JwtUtil jwtUtil;

    private Integer getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.getUserIdFromToken(token);
    }

    @GetMapping("/current")
    public Result<Membership> getCurrentMembership(HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);
        Membership membership = membershipService.getCurrentMembership(userId);
        return Result.success(membership);
    }
}

