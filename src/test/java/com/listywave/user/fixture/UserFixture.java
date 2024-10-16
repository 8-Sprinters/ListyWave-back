package com.listywave.user.fixture;

import com.listywave.image.application.domain.DefaultBackgroundImages;
import com.listywave.user.application.domain.User;
import com.listywave.user.application.vo.BackgroundImageUrl;
import com.listywave.user.application.vo.Description;
import com.listywave.user.application.vo.Nickname;
import com.listywave.user.application.vo.ProfileImageUrl;

public class UserFixture {

    public static User 동호() {
        return User.builder()
                .oauthId(2L)
                .oauthEmail("kdkdhoho@github.com")
                .nickname(Nickname.of("kdkdhoho"))
                .backgroundImageUrl(new BackgroundImageUrl(DefaultBackgroundImages.getRandomImageUrl()))
                .profileImageUrl(new ProfileImageUrl(DefaultBackgroundImages.getRandomImageUrl()))
                .description(new Description("동호동호"))
                .followerCount(30)
                .followingCount(40)
                .allPrivate(false)
                .kakaoAccessToken("KAKAO_ACCESS_TOKEN")
                .isDelete(false)
                .build();
    }

    public static User 정수() {
        return User.builder()
                .oauthId(3L)
                .oauthEmail("pparkjs@github.com")
                .nickname(Nickname.of("pparkjs"))
                .backgroundImageUrl(new BackgroundImageUrl(DefaultBackgroundImages.getRandomImageUrl()))
                .profileImageUrl(new ProfileImageUrl(DefaultBackgroundImages.getRandomImageUrl()))
                .description(new Description("정수정수"))
                .followerCount(70)
                .followingCount(80)
                .allPrivate(false)
                .kakaoAccessToken("KAKAO_ACCESS_TOKEN")
                .isDelete(false)
                .build();
    }

    public static User 유진() {
        return User.builder()
                .oauthId(4L)
                .oauthEmail("eugene@github.com")
                .nickname(Nickname.of("eugene"))
                .backgroundImageUrl(new BackgroundImageUrl(DefaultBackgroundImages.getRandomImageUrl()))
                .profileImageUrl(new ProfileImageUrl(DefaultBackgroundImages.getRandomImageUrl()))
                .description(new Description("유진유진"))
                .followerCount(10)
                .followingCount(20)
                .allPrivate(false)
                .kakaoAccessToken("KAKAO_ACCESS_TOKEN")
                .isDelete(false)
                .build();
    }

    public static User 서영() {
        return User.builder()
                .oauthId(5L)
                .oauthEmail("seyoung@github.com")
                .nickname(Nickname.of("seyoung"))
                .backgroundImageUrl(new BackgroundImageUrl(DefaultBackgroundImages.getRandomImageUrl()))
                .profileImageUrl(new ProfileImageUrl(DefaultBackgroundImages.getRandomImageUrl()))
                .description(new Description("서영서영"))
                .followerCount(120)
                .followingCount(240)
                .allPrivate(false)
                .kakaoAccessToken("KAKAO_ACCESS_TOKEN")
                .isDelete(false)
                .build();
    }
}
