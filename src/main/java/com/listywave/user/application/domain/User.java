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

    @Column(nullable = false, length = 5)
    private Boolean allPrivate;

    @Column(nullable = false, length = 200)
    private String kakaoAccessToken;

    public static User initialCreate(Long oauthId, String oauthEmail, String kakaoAccessToken) {
        return new User(
                oauthId,
                oauthEmail,
                Nickname.initialCreate(String.valueOf(oauthId)),
                new BackgroundImageUrl(""),
                new ProfileImageUrl(""),
                new Description(""),
                false,
                kakaoAccessToken
        );
    }

    public void updateUserProfile(
            String nickname,
            String description,
            String profileImageUrl,
            String backgroundImageUrl
    ) {
        this.nickname = new Nickname(nickname);
        this.description = new Description(description);
        this.profileImageUrl = new ProfileImageUrl(profileImageUrl);
        this.backgroundImageUrl = new BackgroundImageUrl(backgroundImageUrl);
    }

    public void updateUserImageUrl(String profileImage, String backgroundImage) {
        if (!profileImage.isEmpty() && backgroundImage.isEmpty()) {
            this.profileImageUrl = new ProfileImageUrl(profileImage);
        }
        if (!backgroundImage.isEmpty() && profileImage.isEmpty()) {
            this.backgroundImageUrl = new BackgroundImageUrl(backgroundImage);
        }
        if (!backgroundImage.isEmpty() && !profileImage.isEmpty()) {
            this.profileImageUrl = new ProfileImageUrl(profileImage);
            this.backgroundImageUrl = new BackgroundImageUrl(backgroundImage);
        }
    }

    public boolean isSame(Long id) {
        return this.id.equals(id);
    }

    public String getNickname() {
        return nickname.getValue();
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl.getValue();
    }

    public String getProfileImageUrl() {
        return profileImageUrl.getValue();
    }

    public String getDescription() {
        return description.getValue();
    }
}
