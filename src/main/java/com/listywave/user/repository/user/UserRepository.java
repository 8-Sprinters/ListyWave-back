package com.listywave.user.repository.user;

import static com.listywave.common.exception.ErrorCode.NOT_FOUND;

import com.listywave.common.exception.CustomException;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.custom.CustomUserRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {

    Optional<User> findByOauthId(Long oauthId);

    default User getById(Long id) {
        return findById(id).orElseThrow(() -> new CustomException(NOT_FOUND));
    }
}
