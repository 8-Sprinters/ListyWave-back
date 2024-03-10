package com.listywave.user.application.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.listywave.image.application.domain.DefaultBackgroundImages;
import com.listywave.image.application.domain.DefaultProfileImages;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("회원은 ")
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = User.init(333443434L, "user@gmail.com", "sflkhadsfsad.asdjhfahsjdf.asdkjhfasdhjf");
    }

    @Nested
    class 객체_생성 {

        @Test
        void 처음_객체를_생성하면_oauthId를_닉네임_정책의_최대_길이만큼_잘라_설정한다() {
            assertThat(user.getNickname().length()).isLessThanOrEqualTo(10);
        }

        @Test
        void 처음_객체를_생성하면_기본_프로필사진_중_하나가_랜덤하게_할당된다() {
            List<String> defaultProfileImageUrls = Arrays.stream(DefaultProfileImages.values())
                    .map(DefaultProfileImages::getValue)
                    .toList();

            assertThat(user.getProfileImageUrl()).isIn(defaultProfileImageUrls);
        }

        @Test
        void 처음_객체를_생성하면_기본_배경사진_중_하나가_랜덤하게_할당된다() {
            List<String> defaultBackgroundImageUrls = Arrays.stream(DefaultBackgroundImages.values())
                    .map(DefaultBackgroundImages::getValue)
                    .toList();

            assertThat(user.getBackgroundImageUrl()).isIn(defaultBackgroundImageUrls);
        }

        @Test
        void 처음_객체를_생성하면_followingCount와_followerCount는_0이다() {
            assertThat(user.getFollowerCount()).isZero();
            assertThat(user.getFollowingCount()).isZero();
        }

        @Test
        void 처음_객체를_생성하면_allPrivate은_false이다() {
            assertThat(user.isAllPrivate()).isFalse();
        }

        @Test
        void 처음_객체를_생성하면_isDelete는_false이다() {
            assertThat(user.isDelete()).isFalse();
        }
    }

    @Nested
    class 정보_수정 {

        @Test
        void 정보를_수정할_수_있다() {
            // when
            user.updateUserProfile("바꾼닉네임", "바꾼설명", "바꾼프로필사진", "바꾼배경사진");

            // then
            assertAll(
                    () -> assertThat(user.getNickname()).isEqualTo("바꾼닉네임"),
                    () -> assertThat(user.getDescription()).isEqualTo("바꾼설명"),
                    () -> assertThat(user.getProfileImageUrl()).isEqualTo("바꾼프로필사진"),
                    () -> assertThat(user.getBackgroundImageUrl()).isEqualTo("바꾼배경사진")
            );
        }

        @Test
        void 닉네임만_수정할_수_있다() {
            // given
            String beforeDescription = user.getDescription();
            String beforeProfileImageUrl = user.getProfileImageUrl();
            String beforeBackgroundImageUrl = user.getBackgroundImageUrl();

            // when
            user.updateUserProfile("바꾼닉네임", null, null, null);

            // then
            assertAll(
                    () -> assertThat(user.getNickname()).isEqualTo("바꾼닉네임"),
                    () -> assertThat(user.getDescription()).isEqualTo(beforeDescription),
                    () -> assertThat(user.getProfileImageUrl()).isIn(beforeProfileImageUrl),
                    () -> assertThat(user.getBackgroundImageUrl()).isIn(beforeBackgroundImageUrl)
            );
        }

        @Test
        void 설명만_수정할_수_있다() {
            // given
            String beforeNickname = user.getNickname();
            String beforeProfileImageUrl = user.getProfileImageUrl();
            String beforeBackgroundImageUrl = user.getBackgroundImageUrl();

            // when
            user.updateUserProfile(null, "바꾼설명", null, null);

            // then
            assertAll(
                    () -> assertThat(user.getNickname()).isEqualTo(beforeNickname),
                    () -> assertThat(user.getDescription()).isEqualTo("바꾼설명"),
                    () -> assertThat(user.getProfileImageUrl()).isIn(beforeProfileImageUrl),
                    () -> assertThat(user.getBackgroundImageUrl()).isIn(beforeBackgroundImageUrl)
            );
        }

        @Test
        void 프로필_이미지_URL만_수정할_수_있다() {
            // given
            String beforeNickname = user.getNickname();
            String beforeDescription = user.getDescription();
            String beforeBackgroundImageUrl = user.getBackgroundImageUrl();

            // when
            user.updateUserProfile(null, null, "바꾼프로필이미지", null);

            // then
            assertAll(
                    () -> assertThat(user.getNickname()).isEqualTo(beforeNickname),
                    () -> assertThat(user.getDescription()).isEqualTo(beforeDescription),
                    () -> assertThat(user.getProfileImageUrl()).isIn("바꾼프로필이미지"),
                    () -> assertThat(user.getBackgroundImageUrl()).isIn(beforeBackgroundImageUrl)
            );
        }

        @Test
        void 배경_이미지_URL만_수정할_수_있다() {
            // given
            String beforeNickname = user.getNickname();
            String beforeDescription = user.getDescription();
            String beforeProfileImageUrl = user.getProfileImageUrl();

            // when
            user.updateUserProfile(null, null, null, "바꾼배경이미지");

            // then
            assertAll(
                    () -> assertThat(user.getNickname()).isEqualTo(beforeNickname),
                    () -> assertThat(user.getDescription()).isEqualTo(beforeDescription),
                    () -> assertThat(user.getProfileImageUrl()).isIn(beforeProfileImageUrl),
                    () -> assertThat(user.getBackgroundImageUrl()).isIn("바꾼배경이미지")
            );
        }

        @Test
        void 프로필_이미지_URL과_배경_이미지_URL만_수정할_수_있다() {
            // given
            String beforeNickname = user.getNickname();
            String beforeDescription = user.getDescription();

            // when
            user.updateUserProfile(null, null, "바꾼프로필이미지", "바꾼배경이미지");

            // then
            assertAll(
                    () -> assertThat(user.getNickname()).isEqualTo(beforeNickname),
                    () -> assertThat(user.getDescription()).isEqualTo(beforeDescription),
                    () -> assertThat(user.getProfileImageUrl()).isIn("바꾼프로필이미지"),
                    () -> assertThat(user.getBackgroundImageUrl()).isIn("바꾼배경이미지")
            );
        }

        @Test
        void 아무_값도_수정하지_않을_수_있다() {
            // given
            String beforeNickname = user.getNickname();
            String beforeDescription = user.getDescription();
            String beforeProfileImageUrl = user.getProfileImageUrl();
            String beforeBackgroundImageUrl = user.getBackgroundImageUrl();

            // when
            user.updateUserProfile(null, null, null, null);

            // then
            assertAll(
                    () -> assertThat(user.getNickname()).isEqualTo(beforeNickname),
                    () -> assertThat(user.getDescription()).isEqualTo(beforeDescription),
                    () -> assertThat(user.getProfileImageUrl()).isIn(beforeProfileImageUrl),
                    () -> assertThat(user.getBackgroundImageUrl()).isIn(beforeBackgroundImageUrl)
            );
        }
    }

    @Nested
    class 팔로우_팔로워_수 {

        @Test
        void 다른_회원을_팔로우하면_나의_followingCount와_팔로우_당하는_회원의_followerCount가_1씩_증가한다() {
            // given
            User other = User.init(444334343L, "user2@naver.com", "sfasdf.asdfasdf.sadf");

            // when
            user.follow(other);

            // then
            assertThat(user.getFollowingCount()).isOne();
            assertThat(other.getFollowerCount()).isOne();
        }

        @Test
        void 팔로우를_취소하면_나의_followingCount와_팔로우_당하는_회원의_followerCount가_1씩_감소한다() {
            // given
            User other = User.init(444334343L, "user2@naver.com", "sfasdf.asdfasdf.sadf");
            user.follow(other);

            // when
            user.unfollow(other);

            // then
            assertThat(user.getFollowingCount()).isZero();
            assertThat(other.getFollowerCount()).isZero();
        }

        @Test
        void 팔로워를_삭제하면_나의_followerCount와_팔로워의_followingCount를_1씩_감소한다() {
            // given
            User other = User.init(444334343L, "user2@naver.com", "sfasdf.asdfasdf.sadf");
            user.follow(other);

            // when
            other.remove(user);

            // then
            assertThat(user.getFollowingCount()).isZero();
            assertThat(other.getFollowerCount()).isZero();
        }

        @Test
        void 팔로우_수가_최대_가능_수보다_많아질_경우_팔로우는_불가능하다() {
            // given
            IntStream.range(0, 1000)
                    .mapToObj(i -> User.init(444334343L, "user2@naver.com", "sfasdf.asdfasdf.sadf"))
                    .forEach(other -> user.follow(other));

            User other = User.init(444334343L, "user2@naver.com", "sfasdf.asdfasdf.sadf");

            // when
            // then
            assertThatThrownBy(() -> user.follow(other));
        }

        @Test
        void 팔로잉_수를_감소한다() {
            // given
            User other = User.init(444334343L, "user2@naver.com", "sfasdf.asdfasdf.sadf");
            user.follow(other);

            // when
            user.decreaseFollowingCount();

            // then
            assertThat(user.getFollowingCount()).isZero();
        }

        @Test
        void 팔로잉_수는_음수가_될_수_없다() {
            user.decreaseFollowingCount();

            assertThat(user.getFollowingCount()).isZero();
        }

        @Test
        void 팔로워_수를_감소한다() {
            // given
            User other = User.init(444334343L, "user2@naver.com", "sfasdf.asdfasdf.sadf");
            user.follow(other);

            // when
            other.decreaseFollowerCount();

            // then
            assertThat(other.getFollowerCount()).isZero();
        }

        @Test
        void 팔로워_수는_음수가_될_수_없다() {
            user.decreaseFollowerCount();

            assertThat(user.getFollowerCount()).isZero();
        }
    }

    @Test
    void 삭제_처리를_할_수_있다() {
        user.softDelete();

        assertThat(user.isDelete()).isTrue();
    }

    @Test
    void 카카오_액세스_토큰을_갱신한다() {
        // given
        String kakaoAccessToken = "new.KakaoAccess.Token";

        // when
        user.updateKakaoAccessToken(kakaoAccessToken);

        // then
        assertThat(user.getKakaoAccessToken()).isEqualTo(kakaoAccessToken);
    }

    @Test
    void 카카오_액세스_토큰이_존재하는지_검증한다() {
        assertThatNoException().isThrownBy(() -> user.validateHasKakaoAccessToken());
    }
}
