package com.listywave.admin;

import static com.listywave.common.exception.ErrorCode.INVALID_ACCESS;

import com.listywave.auth.application.domain.JwtManager;
import com.listywave.common.exception.CustomException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final JwtManager jwtManager;
    private final AdminRepository adminRepository;

    @Transactional(readOnly = true)
    public boolean isValidIp(String ip) {
        return adminRepository.existsByIp(ip);
    }

    @Transactional(readOnly = true)
    public AdminLoginResponse login(String account, String password) {
        Optional<Admin> optionalAdmin = adminRepository.findByAccount(account);
        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            admin.validatePassword(password);

            String accessToken = jwtManager.createAdminAccessToken(admin.getId());
            String refreshToken = jwtManager.createAdminRefreshToken(admin.getId());
            return new AdminLoginResponse(accessToken, refreshToken);
        }
        throw new CustomException(INVALID_ACCESS);
    }
}
