package com.listywave.admin;

import static com.listywave.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import com.listywave.common.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/admin")
    String redirectLoginPage(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");

        if (adminService.isValidIp(clientIp)) {
            return "redirect:/admin/login";
        }
        throw new CustomException(RESOURCE_NOT_FOUND);
    }
}
