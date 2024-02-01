package com.listywave.user.domain;

import com.listywave.common.BaseEntity;
import com.listywave.user.vo.Nickname;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Embedded
    private Nickname nickname;

    @Column(nullable = false, length = 2048)
    private String backgroundImageUrl;

    @Column(nullable = false, length = 2048)
    private String profileImageUrl;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(nullable = false)
    private int followingCount = 0;

    @Column(nullable = false)
    private int followerCount = 0;

    @Enumerated(EnumType.STRING)
    private Role role;

}
