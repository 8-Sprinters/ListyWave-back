package com.listywave.user.repository;

import com.listywave.user.application.domain.User;
import com.listywave.user.repository.custom.UserRepositoryCustom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    Optional<User> findByOauthId(Long oauthId);
}
