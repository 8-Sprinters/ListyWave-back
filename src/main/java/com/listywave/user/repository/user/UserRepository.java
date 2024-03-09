package com.listywave.user.repository.user;

import static com.listywave.common.exception.ErrorCode.DELETED_USER_EXCEPTION;
import static com.listywave.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import com.listywave.common.exception.CustomException;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.custom.CustomUserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {

    Optional<User> findByOauthId(Long oauthId);

    default User getById(Long id) {
        Optional<User> result = findById(id);
        if (result.isPresent()) {
            User user = result.get();
            if (user.isDelete()) {
                throw new CustomException(DELETED_USER_EXCEPTION);
            }
            return user;
        }
        throw new CustomException(RESOURCE_NOT_FOUND);
    }

    Boolean existsByNicknameValue(String nickname);

    @Override
    @Query("""
            select u
            from User u
            where u.isDelete = false
            """)
    List<User> findAll();
}
