package com.listywave.auth.dev.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.listywave.user.application.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DevAccount {

    @Id
    @Column(name = "user_id", nullable = false, unique = true)
    private Long id;

    @MapsId
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, unique = true)
    private String account;

    @Column(nullable = false)
    private String password;

    public void validatePassword(String password) {
        if (this.password.equals(password)) {
            return;
        }
        throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
    }

    public Long getUserId() {
        return user.getId();
    }
}
