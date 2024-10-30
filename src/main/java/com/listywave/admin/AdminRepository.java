package com.listywave.admin;

import static com.listywave.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import com.listywave.common.exception.CustomException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    default Admin getById(Long id) {
        return findById(id).orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));
    }
}
