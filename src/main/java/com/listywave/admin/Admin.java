package com.listywave.admin;

import static com.listywave.common.exception.ErrorCode.INVALID_ACCESS;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.listywave.common.exception.CustomException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class Admin {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40, unique = true)
    private String ip;

    @Column(nullable = false, length = 50, unique = true)
    private String account;

    @Column(nullable = false, length = 50)
    private String password;

    public void validatePassword(String password) {
        if (this.password.equals(password)) {
            return;
        }
        throw new CustomException(INVALID_ACCESS);
    }

    public void update(String password) {
        this.password = password;
    }
}
