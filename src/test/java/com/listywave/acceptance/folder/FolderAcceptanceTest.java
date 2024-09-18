package com.listywave.acceptance.folder;

import com.listywave.acceptance.common.AcceptanceTest;
import com.listywave.collection.application.dto.FolderCreateResponse;
import com.listywave.collection.application.dto.FolderListResponse;
import com.listywave.list.application.dto.response.ListCreateResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.listywave.acceptance.collection.CollectionAcceptanceTestHelper.콜렉트_또는_콜렉트취소_API_호출;
import static com.listywave.acceptance.common.CommonAcceptanceHelper.HTTP_상태_코드를_검증한다;
import static com.listywave.acceptance.folder.FolderAcceptanceTestHelper.*;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.*;
import static com.listywave.list.fixture.ListFixture.지정된_개수만큼_리스트를_생성한다;
import static com.listywave.user.fixture.UserFixture.동호;
import static com.listywave.user.fixture.UserFixture.정수;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpStatus.*;

@DisplayName("폴더 관련 인수테스트")
public class FolderAcceptanceTest extends AcceptanceTest {


    @Nested
    class 폴더_생성 {

        @Test
        void 폴더를_성공적으로_생성한다(){
            // given
            var 정수 = 회원을_저장한다(정수());
            var 정수_엑세스_토큰 = 액세스_토큰을_발급한다(정수);
            var 폴더_생성_요청_데이터 = 폴더_생성_요청_데이터("맛집");

            // when
            var 응답 = 폴더_생성_API_호출(정수_엑세스_토큰, 폴더_생성_요청_데이터);
            var 결과 = 응답.as(FolderCreateResponse.class);

            // then
            HTTP_상태_코드를_검증한다(응답, CREATED);
            assertThat(결과.folderId()).isEqualTo(1L);
        }

        @Test
        void 폴더_생성_시_기존에_생성한_폴더명과_중복될_수_없다(){
            // given
            var 정수 = 회원을_저장한다(정수());
            var 정수_엑세스_토큰 = 액세스_토큰을_발급한다(정수);
            var 폴더_생성_요청_데이터1 = 폴더_생성_요청_데이터("맛집");
            var 폴더_생성_요청_데이터2 = 폴더_생성_요청_데이터("맛집");

            // when
            폴더_생성_API_호출(정수_엑세스_토큰, 폴더_생성_요청_데이터1);
            var 응답 = 폴더_생성_API_호출(정수_엑세스_토큰, 폴더_생성_요청_데이터2);

            // then
            HTTP_상태_코드를_검증한다(응답, BAD_REQUEST);
        }
    }

    @Nested
    class 폴더_수정 {

        @Test
        void 폴더를_성공적으로_수정한다(){
            // given
            var 정수 = 회원을_저장한다(정수());
            var 정수_엑세스_토큰 = 액세스_토큰을_발급한다(정수);
            var 폴더_생성_요청_데이터 = 폴더_생성_요청_데이터("맛집");
            var 생성된_폴더_ID = 폴더_생성_API_호출(정수_엑세스_토큰, 폴더_생성_요청_데이터)
                    .as(FolderCreateResponse.class)
                    .folderId();
            var 폴더_수정_요청_데이터 = 폴더_수정_요청_데이터("예카");

            // when
            var 응답 = 폴더_수정_API_호출(정수_엑세스_토큰, 폴더_수정_요청_데이터, 생성된_폴더_ID);

            // then
            HTTP_상태_코드를_검증한다(응답, NO_CONTENT);
        }

        @Test
        void 폴더_수정_시_기존에_생성한_폴더명과_중복될_수_없다(){
            // given
            var 정수 = 회원을_저장한다(정수());
            var 정수_엑세스_토큰 = 액세스_토큰을_발급한다(정수);
            var 폴더_생성_요청_데이터 = 폴더_생성_요청_데이터("맛집");
            var 생성된_폴더_ID = 폴더_생성_API_호출(정수_엑세스_토큰, 폴더_생성_요청_데이터)
                    .as(FolderCreateResponse.class)
                    .folderId();
            var 폴더_수정_요청_데이터 = 폴더_수정_요청_데이터("맛집");

            // when
            var 응답 = 폴더_수정_API_호출(정수_엑세스_토큰, 폴더_수정_요청_데이터, 생성된_폴더_ID);

            // then
            HTTP_상태_코드를_검증한다(응답, BAD_REQUEST);
        }

        @Test
        void 폴더_생성자만_수정할_수_있다(){
            // given
            var 정수 = 회원을_저장한다(정수());
            var 동호 = 회원을_저장한다(동호());
            var 정수_엑세스_토큰 = 액세스_토큰을_발급한다(정수);
            var 동호_엑세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 폴더_생성_요청_데이터 = 폴더_생성_요청_데이터("맛집");
            var 생성된_폴더_ID = 폴더_생성_API_호출(정수_엑세스_토큰, 폴더_생성_요청_데이터)
                    .as(FolderCreateResponse.class)
                    .folderId();
            var 폴더_수정_요청_데이터 = 폴더_수정_요청_데이터("맛집");

            // when
            var 응답 = 폴더_수정_API_호출(동호_엑세스_토큰, 폴더_수정_요청_데이터, 생성된_폴더_ID);

            // then
            HTTP_상태_코드를_검증한다(응답, FORBIDDEN);
        }
    }

    @Nested
    class 폴더_삭제 {

        @Test
        void 폴더를_성공적으로_삭제한다(){
            // given
            var 정수 = 회원을_저장한다(정수());
            var 정수_엑세스_토큰 = 액세스_토큰을_발급한다(정수);
            var 폴더_생성_요청_데이터 = 폴더_생성_요청_데이터("맛집");
            var 생성된_폴더_ID = 폴더_생성_API_호출(정수_엑세스_토큰, 폴더_생성_요청_데이터)
                    .as(FolderCreateResponse.class)
                    .folderId();

            // when
            var 응답 = 폴더_삭제_API_호출(정수_엑세스_토큰, 생성된_폴더_ID);

            // then
            HTTP_상태_코드를_검증한다(응답, NO_CONTENT);
        }

        @Test
        void 폴더_삭제시_콜렉트한_리스트들도_삭제된다(){
            // given
            var 정수 = 회원을_저장한다(정수());
            var 동호 = 회원을_저장한다(동호());
            var 정수_엑세스_토큰 = 액세스_토큰을_발급한다(정수);
            var 동호_엑세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 리스트_생성_요청_데이터 = 가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of());
            var 동호_리스트_ID = 리스트_저장_API_호출(리스트_생성_요청_데이터, 동호_엑세스_토큰)
                    .as(ListCreateResponse.class)
                    .listId();

            var 폴더_생성_요청_데이터 = 폴더_생성_요청_데이터("맛집");
            var 정수_폴더_ID = 폴더_생성_API_호출(정수_엑세스_토큰, 폴더_생성_요청_데이터)
                    .as(FolderCreateResponse.class).folderId();
            콜렉트_또는_콜렉트취소_API_호출(정수_엑세스_토큰, 동호_리스트_ID, 정수_폴더_ID);

            // when
            var 응답 = 폴더_삭제_API_호출(정수_엑세스_토큰, 정수_폴더_ID);
            var 동호_리스트_콜렉트수 = 회원용_리스트_상세_조회_API_호출(동호_엑세스_토큰, 동호_리스트_ID).collectCount();

            // then
            HTTP_상태_코드를_검증한다(응답, NO_CONTENT);
            assertThat(동호_리스트_콜렉트수).isEqualTo(0);
        }

        @Test
        void 폴더_생성자만_삭제할_수_있다(){
            // given
            var 정수 = 회원을_저장한다(정수());
            var 동호 = 회원을_저장한다(동호());
            var 정수_엑세스_토큰 = 액세스_토큰을_발급한다(정수);
            var 동호_엑세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 폴더_생성_요청_데이터 = 폴더_생성_요청_데이터("맛집");
            var 생성된_폴더_ID = 폴더_생성_API_호출(정수_엑세스_토큰, 폴더_생성_요청_데이터)
                    .as(FolderCreateResponse.class)
                    .folderId();

            // when
            var 응답 = 폴더_삭제_API_호출(동호_엑세스_토큰, 생성된_폴더_ID);

            // then
            HTTP_상태_코드를_검증한다(응답, FORBIDDEN);
        }
    }

    @Nested
    class 폴더_조회 {

        @Test
        void 폴더_목록을_조회한다(){
            // given
            var 정수 = 회원을_저장한다(정수());
            var 동호 = 회원을_저장한다(동호());
            var 정수_엑세스_토큰 = 액세스_토큰을_발급한다(정수);
            var 동호_엑세스_토큰 = 액세스_토큰을_발급한다(동호);
//            var 리스트_생성_요청_데이터 = 가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of());
//            var 동호_리스트_ID = 리스트_저장_API_호출(리스트_생성_요청_데이터, 동호_엑세스_토큰)
//                    .as(ListCreateResponse.class)
//                    .listId();

            리스트를_모두_저장한다(지정된_개수만큼_리스트를_생성한다(동호, 3));

            var 폴더_생성_요청_데이터1 = 폴더_생성_요청_데이터("맛집");
            var 폴더_생성_요청_데이터2 = 폴더_생성_요청_데이터("예카");
            var 폴더_생성_요청_데이터3 = 폴더_생성_요청_데이터("비밀폴더");
            폴더_생성_API_호출(정수_엑세스_토큰, 폴더_생성_요청_데이터1);
            폴더_생성_API_호출(정수_엑세스_토큰, 폴더_생성_요청_데이터2);
            폴더_생성_API_호출(정수_엑세스_토큰, 폴더_생성_요청_데이터3);
            콜렉트_또는_콜렉트취소_API_호출(정수_엑세스_토큰, 1L, 1L);
            콜렉트_또는_콜렉트취소_API_호출(정수_엑세스_토큰, 2L, 2L);
            콜렉트_또는_콜렉트취소_API_호출(정수_엑세스_토큰, 3L, 2L);

            // when
            var 응답 = 폴더_조회_API_호출(정수_엑세스_토큰);
//            var 결과 = 응답.as(FolderListResponse.class);
            var 결과 = 응답.as(FolderListResponse.class);

            // then
            assertAll(
                    () -> assertThat(결과.folders().get(0).listCount()).isEqualTo(0L),
                    () -> assertThat(결과.folders().get(0).folderName()).isEqualTo("비밀폴더"),
                    () -> assertThat(결과.folders().get(1).listCount()).isEqualTo(2L),
                    () -> assertThat(결과.folders().get(1).folderName()).isEqualTo("예카"),
                    () -> assertThat(결과.folders().get(2).listCount()).isEqualTo(1L),
                    () -> assertThat(결과.folders().get(2).folderName()).isEqualTo("맛집")
            );
        }

        @Test
        void 폴더_생성_시_기존에_생성한_폴더명과_중복될_수_없다(){
            // given
            // when
            // then
        }
    }
}
