package com.listywave.user.fixture;

import com.listywave.user.application.domain.User;
import com.listywave.user.application.vo.BackgroundImageUrl;
import com.listywave.user.application.vo.Description;
import com.listywave.user.application.vo.Nickname;
import com.listywave.user.application.vo.ProfileImageUrl;

public class UserFixture {

    public static User 카카오_서버_회원() {
        return User.builder()
                .oauthId(1L)
                .oauthEmail("kakaoUser@naver.com")
                .nickname(new Nickname("kakaoUser"))
                .backgroundImageUrl(new BackgroundImageUrl(""))
                .profileImageUrl(new ProfileImageUrl(""))
                .description(new Description("카카오 서버 회원 Fixture 입니다."))
                .followerCount(15)
                .followingCount(50)
                .allPrivate(false)
                .kakaoAccessToken("KAKAO_ACCESS_TOKEN")
                .isDelete(false)
                .build();
    }

    public static User 동호() {
        return User.builder()
                .oauthId(2L)
                .oauthEmail("kdkdhoho@github.com")
                .nickname(new Nickname("kdkdhoho"))
                .backgroundImageUrl(new BackgroundImageUrl(""))
                .profileImageUrl(new ProfileImageUrl(""))
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
                .nickname(new Nickname("pparkjs"))
                .backgroundImageUrl(new BackgroundImageUrl(""))
                .profileImageUrl(new ProfileImageUrl(""))
                .description(new Description("정수정수"))
                .followerCount(70)
                .followingCount(80)
                .allPrivate(false)
                .kakaoAccessToken("KAKAO_ACCESS_TOKEN")
                .isDelete(false)
                .build();
    }
}
