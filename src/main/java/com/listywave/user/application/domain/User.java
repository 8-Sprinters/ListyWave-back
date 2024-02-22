package com.listywave.user.application.domain;

import static com.listywave.common.exception.ErrorCode.EXCEED_FOLLOW_COUNT_EXCEPTION;
import static com.listywave.common.exception.ErrorCode.INVALID_ACCESS;
import static lombok.AccessLevel.PROTECTED;

import com.listywave.common.BaseEntity;
import com.listywave.common.exception.CustomException;
import com.listywave.user.application.vo.BackgroundImageUrl;
import com.listywave.user.application.vo.Description;
import com.listywave.user.application.vo.Nickname;
import com.listywave.user.application.vo.ProfileImageUrl;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "users")
@NoArgsConstructor(access = PROTECTED)
public class User extends BaseEntity {

    private static final int MAX_FOLLOW_COUNT = 1000;

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

    @Column(nullable = false)
    private String kakaoAccessToken;

    public static User init(Long oauthId, String oauthEmail, String kakaoAccessToken) {
        return new User(
                oauthId,
                oauthEmail,
                Nickname.initialCreate(String.valueOf(oauthId)),
                new BackgroundImageUrl(""),
                new ProfileImageUrl(""),
                new Description(""),
                0,
                0,
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
        return this.getId().equals(id);
    }

    public void validateUpdate(Long id) {
        if (!this.getId().equals(id)) {
            throw new CustomException(INVALID_ACCESS);
        }
    }

    public void follow(User followingUser) {
        if (this.followingCount == MAX_FOLLOW_COUNT) {
            throw new CustomException(EXCEED_FOLLOW_COUNT_EXCEPTION, "팔로우는 최대 " + MAX_FOLLOW_COUNT + "명까지 가능합니다.");
        }
        this.followingCount++;
        followingUser.followerCount++;
    }

    public void unfollow(User followingUser) {
        if (this.followingCount > 0) {
            this.followingCount--;
        }
        if (followingUser.followerCount > 0) {
            followingUser.followerCount--;
        }
    }

    public void updateKakaoAccessToken(String kakaoAccessToken) {
        this.kakaoAccessToken = kakaoAccessToken;
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
