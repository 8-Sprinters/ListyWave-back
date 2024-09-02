package com.listywave.auth.dev.repository;

import com.listywave.auth.dev.domain.DevAccount;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

@Profile("!prod")
public interface DevAccountRepository extends JpaRepository<DevAccount, Long> {

    Optional<DevAccount> findByAccount(String account);
}
