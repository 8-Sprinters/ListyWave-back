package com.listywave.user.application.service;

import static com.listywave.common.exception.ErrorCode.NOT_FOUND;

import com.listywave.auth.application.domain.JwtManager;
import com.listywave.common.exception.CustomException;
import com.listywave.user.application.domain.User;
import com.listywave.user.application.dto.AllUserResponse;
import com.listywave.user.application.dto.UserInfoResponse;
import com.listywave.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtManager jwtManager;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(Long userId, String accessToken) {
        Optional<User> found = userRepository.findById(userId);
        User foundUser = found.orElseThrow(() -> new CustomException(NOT_FOUND));

        if (isSignedIn(accessToken)) {
            return UserInfoResponse.of(foundUser, false, false);
        }

        Long loginUserId = jwtManager.read(accessToken);
        if (foundUser.isSame(loginUserId)) {
            return UserInfoResponse.of(foundUser, false, true);
        }
        return UserInfoResponse.of(foundUser, false, false);
    }

    private static boolean isSignedIn(String accessToken) {
        return accessToken.isBlank();
    }

    @Transactional(readOnly = true)
    public AllUserResponse getAllUser() {
        List<User> allUser = userRepository.findAll();
        return AllUserResponse.of(allUser);
    }
}
