package com.listywave.admin;

import static com.listywave.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import com.listywave.common.auth.Auth;
import com.listywave.common.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final Environment environment;
    private final AdminService adminService;

    @GetMapping("/admin")
    String redirectLoginPage(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");

        if (adminService.isValidIp(clientIp)) {
            String[] activeProfiles = environment.getActiveProfiles();

            for (String activeProfile : activeProfiles) {
                switch (activeProfile) {
                    case "dev", "local" -> {
                        return "redirect:http://localhost:3000/admin/login";
                    }
                    case "prod" -> {
                        return "redirect:https://listywave.com/admin/login";
                    }
                }
            }
        }
        throw new CustomException(RESOURCE_NOT_FOUND);
    }

    @PostMapping("/admin/login")
    ResponseEntity<AdminLoginResponse> login(@RequestBody AdminLoginRequest adminLoginRequest) {
        AdminLoginResponse result = adminService.login(adminLoginRequest.account(), adminLoginRequest.password());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/admin")
    ResponseEntity<Void> updateInfo(@Auth Long adminId, @RequestBody AdminUpdateRequest request) {
        adminService.update(adminId, request.password());
        return ResponseEntity.ok().build();
    }
}
