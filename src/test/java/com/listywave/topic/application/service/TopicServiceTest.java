package com.listywave.topic.application.service;

import static com.listywave.list.application.domain.category.CategoryType.DAILYLIFE_THOUGHTS;
import static com.listywave.list.application.domain.category.CategoryType.MOVIE_DRAMA;
import static com.listywave.list.application.domain.category.CategoryType.MUSIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.listywave.admin.Admin;
import com.listywave.common.IntegrationTest;
import com.listywave.list.application.domain.list.ListDescription;
import com.listywave.list.application.domain.list.ListTitle;
import com.listywave.topic.application.domain.Topic;
import com.listywave.topic.application.service.dto.ExposedTopicFindResponse;
import com.listywave.topic.application.service.dto.ExposedTopicFindResponse.TopicDto;
import com.listywave.topic.application.service.dto.RecommendTopicFindResponse;
import com.listywave.topic.application.service.dto.TopicCreateRequest;
import com.listywave.topic.application.service.dto.TopicFindResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Repeat;

class TopicServiceTest extends IntegrationTest {

    @Nested
    class 토픽_생성 {

        @Test
        void 토픽을_생성한다() {
            // given
            TopicCreateRequest request = new TopicCreateRequest(DAILYLIFE_THOUGHTS.getViewName(), "제일 좋아하는 여자 아이돌 TOP3", "여러분들은 어떤 여돌을 가장 좋아하나요?", false);
            Long userId = ej.getId();

            // when
            topicService.create(request, userId);

            // then
            List<Topic> result = topicRepository.findAll();
            assertAll(
                    () -> assertThat(result).hasSize(1),
                    () -> {
                        Topic topic = result.get(0);
                        assertThat(topic.getCategory()).isEqualTo(DAILYLIFE_THOUGHTS);
                        assertThat(topic.getTitle().getValue()).isEqualTo("제일 좋아하는 여자 아이돌 TOP3");
                        assertThat(topic.getDescription().getValue()).isEqualTo("여러분들은 어떤 여돌을 가장 좋아하나요?");
                        assertThat(topic.isAnonymous()).isFalse();
                        assertThat(topic.isExposed()).isTrue(); // TODO: 어드민 로그인 붙이기 전까지는 항상 True로 생성
                    }
            );
        }
    }

    @Nested
    class 사용자용_토픽_조회 {

        @Test
        void 노출이_승인된_토픽만_조회한다() {
            // given
            List<Topic> topics = List.of(
                    new Topic(dh, MUSIC, new ListTitle("1"), new ListDescription("1"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("2"), new ListDescription("2"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("3"), new ListDescription("3"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("4"), new ListDescription("4"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("5"), new ListDescription("5"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("6"), new ListDescription("6"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("7"), new ListDescription("7"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("8"), new ListDescription("8"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("9"), new ListDescription("9"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("10"), new ListDescription("10"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("11"), new ListDescription("11"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("12"), new ListDescription("12"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("13"), new ListDescription("13"), false, true) // O
            );
            topicRepository.saveAll(topics);

            // when
            int size = 5;
            ExposedTopicFindResponse result = topicService.findAllExposed(null, size);

            // then
            assertAll(
                    () -> assertThat(result.hasNext()).isTrue(),
                    () -> {
                        List<TopicDto> topicDtos = result.topics();

                        assertThat(result.cursorId()).isEqualTo(topicDtos.get(topicDtos.size() - 1).id());
                        assertThat(topicDtos).extracting("title")
                                .isEqualTo(List.of("13", "11", "9", "7", "5"));
                    }
            );
        }

        @Test
        void cursorId가_null이고_size가_10일_때_노출이_승인된_토픽을_조회한다() {
            // given
            save10TopicsAllExposed();

            // when
            int size = 10;
            ExposedTopicFindResponse result = topicService.findAllExposed(null, size);

            // then
            assertAll(
                    () -> assertThat(result.hasNext()).isTrue(),
                    () -> {
                        List<TopicDto> topicDtos = result.topics();

                        assertThat(result.cursorId()).isEqualTo(topicDtos.get(topicDtos.size() - 1).id());
                        assertThat(topicDtos).extracting("title")
                                .isEqualTo(List.of("13", "12", "11", "10", "9", "8", "7", "6", "5", "4"));
                    }
            );
        }

        private List<Topic> save10TopicsAllExposed() {
            List<Topic> topics = List.of(
                    new Topic(dh, MUSIC, new ListTitle("1"), new ListDescription("1"), false, true),
                    new Topic(dh, MUSIC, new ListTitle("2"), new ListDescription("2"), false, true),
                    new Topic(dh, MUSIC, new ListTitle("3"), new ListDescription("3"), false, true),
                    new Topic(dh, MUSIC, new ListTitle("4"), new ListDescription("4"), false, true),
                    new Topic(dh, MUSIC, new ListTitle("5"), new ListDescription("5"), false, true),
                    new Topic(dh, MUSIC, new ListTitle("6"), new ListDescription("6"), false, true),
                    new Topic(dh, MUSIC, new ListTitle("7"), new ListDescription("7"), false, true),
                    new Topic(dh, MUSIC, new ListTitle("8"), new ListDescription("8"), false, true),
                    new Topic(dh, MUSIC, new ListTitle("9"), new ListDescription("9"), false, true),
                    new Topic(dh, MUSIC, new ListTitle("10"), new ListDescription("10"), false, true),
                    new Topic(dh, MUSIC, new ListTitle("11"), new ListDescription("11"), false, true),
                    new Topic(dh, MUSIC, new ListTitle("12"), new ListDescription("12"), false, true),
                    new Topic(dh, MUSIC, new ListTitle("13"), new ListDescription("13"), false, true)
            );
            topicRepository.saveAll(topics);

            return topics;
        }

        @Test
        void cursorId가_뒤에서_다섯_번째고_size가_5일_때_노출이_승인된_토픽을_조회한다() {
            // given
            List<Topic> topics = save10TopicsAllExposed();

            // when
            long cursorId = topics.get(8).getId();
            ExposedTopicFindResponse result = topicService.findAllExposed(cursorId, 5);

            // then
            assertAll(
                    () -> assertThat(result.hasNext()).isTrue(),
                    () -> {
                        List<TopicDto> topicDtos = result.topics();

                        assertThat(result.cursorId()).isEqualTo(topics.get(3).getId());
                        assertThat(topicDtos).extracting("title")
                                .isEqualTo(List.of("8", "7", "6", "5", "4"));
                    }
            );

        }

        @Test
        @Repeat(10)
        void 홈_화면에서_추천_토픽을_조회한다() {
            // given
            List<Topic> topics = List.of(
                    new Topic(dh, MUSIC, new ListTitle("1"), new ListDescription("1"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("2"), new ListDescription("2"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("3"), new ListDescription("3"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("4"), new ListDescription("4"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("5"), new ListDescription("5"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("6"), new ListDescription("6"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("7"), new ListDescription("7"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("8"), new ListDescription("8"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("9"), new ListDescription("9"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("10"), new ListDescription("10"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("11"), new ListDescription("11"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("12"), new ListDescription("12"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("13"), new ListDescription("13"), false, true) // O
            );
            topicRepository.saveAll(topics);

            // when
            List<RecommendTopicFindResponse> result = topicService.getRecommendTopics(5);

            // then
            assertAll(
                    () -> {
                        List<String> titles = result.stream()
                                .map(RecommendTopicFindResponse::title)
                                .toList();

                        assertThat(titles).hasSizeLessThanOrEqualTo(5);
                        assertThat(titles).containsAnyOf("1", "3", "5", "7", "9", "11", "13");
                        assertThat(titles).doesNotContainAnyElementsOf(List.of("2", "4", "6", "8", "10", "12"));
                    }
            );
        }
    }

    @Nested
    class 관리자용_토픽_조회 {

        private Admin admin;

        @BeforeEach
        void setUp() {
            this.admin = adminRepository.save(new Admin(null, "1.2.3.4", "account", "1234"));
        }

        @Test
        void cursorId가_null이고_size가_10일_때_모든_토픽을_조회한다() {
            // given
            List<Topic> topics = List.of(
                    new Topic(dh, MUSIC, new ListTitle("1"), new ListDescription("1"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("2"), new ListDescription("2"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("3"), new ListDescription("3"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("4"), new ListDescription("4"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("5"), new ListDescription("5"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("6"), new ListDescription("6"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("7"), new ListDescription("7"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("8"), new ListDescription("8"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("9"), new ListDescription("9"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("10"), new ListDescription("10"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("11"), new ListDescription("11"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("12"), new ListDescription("12"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("13"), new ListDescription("13"), false, true) // O
            );
            topicRepository.saveAll(topics);

            // when
            TopicFindResponse result = topicService.findAll(admin.getId(), null, 10);

            // then
            assertAll(
                    () -> assertThat(result.hasNext()).isTrue(),
                    () -> assertThat(result.totalCount()).isEqualTo(2),
                    () -> assertThat(result.topics()).extracting("title")
                            .isEqualTo(List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"))
            );
        }

        @Test
        void cursorId가_열_번째_ID이고_size가_10일_때_모든_토픽을_조회한다() {
            // given
            List<Topic> topics = List.of(
                    new Topic(dh, MUSIC, new ListTitle("1"), new ListDescription("1"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("2"), new ListDescription("2"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("3"), new ListDescription("3"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("4"), new ListDescription("4"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("5"), new ListDescription("5"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("6"), new ListDescription("6"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("7"), new ListDescription("7"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("8"), new ListDescription("8"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("9"), new ListDescription("9"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("10"), new ListDescription("10"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("11"), new ListDescription("11"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("12"), new ListDescription("12"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("13"), new ListDescription("13"), false, true) // O
            );
            topicRepository.saveAll(topics);

            // when
            TopicFindResponse result = topicService.findAll(admin.getId(), topics.get(9).getId(), 10);

            // then
            assertAll(
                    () -> assertThat(result.hasNext()).isFalse(),
                    () -> assertThat(result.totalCount()).isEqualTo(2),
                    () -> assertThat(result.topics()).extracting("title")
                            .isEqualTo(List.of("11", "12", "13"))
            );
        }

        @Test
        void cursorId가_다섯_번째이고_size가_5일_때_모든_토픽을_조회한다() {
            // given
            List<Topic> topics = List.of(
                    new Topic(dh, MUSIC, new ListTitle("1"), new ListDescription("1"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("2"), new ListDescription("2"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("3"), new ListDescription("3"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("4"), new ListDescription("4"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("5"), new ListDescription("5"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("6"), new ListDescription("6"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("7"), new ListDescription("7"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("8"), new ListDescription("8"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("9"), new ListDescription("9"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("10"), new ListDescription("10"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("11"), new ListDescription("11"), false, true), // O
                    new Topic(dh, MUSIC, new ListTitle("12"), new ListDescription("12"), false, false),
                    new Topic(dh, MUSIC, new ListTitle("13"), new ListDescription("13"), false, true) // O
            );
            topicRepository.saveAll(topics);

            // when
            TopicFindResponse result = topicService.findAll(admin.getId(), topics.get(4).getId(), 5);

            // then
            assertAll(
                    () -> assertThat(result.hasNext()).isTrue(),
                    () -> assertThat(result.totalCount()).isEqualTo(3),
                    () -> assertThat(result.topics()).extracting("title")
                            .isEqualTo(List.of("6", "7", "8", "9", "10"))
            );
        }
    }

    @Test
    void 노출_여부와_타이틀과_카테고리_수정() {
        // given
        Topic topic = new Topic(dh, MUSIC, new ListTitle("origin"), new ListDescription("origin"), true, false);
        topicRepository.save(topic);

        // when
        topicService.update(topic.getId(), true, MOVIE_DRAMA.getCode(), "new");

        // then
        Topic result = topicRepository.getById(topic.getId());
        assertAll(
                () -> assertThat(result.isExposed()).isTrue(),
                () -> assertThat(result.getCategory()).isEqualTo(MOVIE_DRAMA),
                () -> assertThat(result.getTitle()).isEqualTo(new ListTitle("new"))
        );
    }
}
