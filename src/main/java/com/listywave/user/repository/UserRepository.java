package com.listywave.user.repository;

import com.listywave.user.application.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByOauthId(Long oauthId);
}
