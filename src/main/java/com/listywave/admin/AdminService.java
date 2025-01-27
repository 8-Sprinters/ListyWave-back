package com.listywave.admin;

import static com.listywave.common.exception.ErrorCode.INVALID_ACCESS;

import com.listywave.auth.application.domain.JwtManager;
import com.listywave.common.encrypt.Sha256Cipher;
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
    private final Sha256Cipher sha256Cipher;
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

            // 암호화 적용으로 인해, 임시로 작성해둔 코드입니다.
            // 모든 어드민이 암호를 변경하면 if 조건식만 제거합니다.
            if (!password.equals("12345")) {
                admin.validatePassword(sha256Cipher.encrypt(password)); // 해당 라인은 제거하지 않습니다.
            }

            String accessToken = jwtManager.createAdminAccessToken(admin.getId());
            String refreshToken = jwtManager.createAdminRefreshToken(admin.getId());
            return new AdminLoginResponse(accessToken, refreshToken);
        }
        throw new CustomException(INVALID_ACCESS);
    }

    public void update(Long adminId, String password) {
        Admin admin = adminRepository.getById(adminId);
        String encryptedNewPassword = sha256Cipher.encrypt(password);
        admin.update(encryptedNewPassword);
    }
}
