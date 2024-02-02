package com.listywave.user.application.domain;

import com.listywave.common.BaseEntity;
import com.listywave.user.application.vo.BackgroundImageUrl;
import com.listywave.user.application.vo.Description;
import com.listywave.user.application.vo.Nickname;
import com.listywave.user.application.vo.ProfileImageUrl;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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
    private Long oauthId;

    @Column(nullable = false)
    private String oauthEmail;

    @Embedded
    private Nickname nickname;

    @Embedded
    private BackgroundImageUrl backgroundImageUrl;

    @Embedded
    private ProfileImageUrl profileImageUrl;

    @Embedded
    private Description description;

    @Column(nullable = false)
    private int followingCount;

    @Column(nullable = false)
    private int followerCount;
    
    @Column(nullable = false, length = 5)
    private Boolean allPrivate;

    public static User initialCreate(Long oauthId, String oauthEmail) {
        return new User(
                oauthId,
                oauthEmail,
                Nickname.initialCreate(String.valueOf(oauthId)),
                new BackgroundImageUrl(""),
                new ProfileImageUrl(""),
                new Description(""),
                0,
                0,
                false
        );
    }
}
