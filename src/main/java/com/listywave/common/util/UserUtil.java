package com.listywave.common.util;

import com.listywave.common.exception.CustomException;
import com.listywave.common.exception.ErrorCode;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUtil {

    private final UserRepository userRepository;

    //TODO: 추후 springSecurity 구현할 시 로그인 한 유저 SecurityContextHolder에서 Principal객체에서 유저 아이디 가져오는 부분 구현
//    public User getCurrentUser(){
//        return userRepository
//                .findById(securityUtil.getCurrentMemberId())
//                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 회원을 찾을 수 없습니다."));
//    }

    public User getUserByUserid(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 회원을 찾을 수 없습니다."));
    }
}
