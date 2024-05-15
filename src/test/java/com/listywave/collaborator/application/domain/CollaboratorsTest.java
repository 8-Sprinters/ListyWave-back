package com.listywave.collaborator.application.domain;

import static com.listywave.common.exception.ErrorCode.DUPLICATE_COLLABORATOR_EXCEPTION;
import static com.listywave.list.application.domain.category.CategoryType.ANIMAL_PLANT;
import static com.listywave.list.application.domain.category.CategoryType.ETC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.listywave.common.exception.CustomException;
import com.listywave.list.application.domain.item.Item;
import com.listywave.list.application.domain.item.ItemComment;
import com.listywave.list.application.domain.item.ItemImageUrl;
import com.listywave.list.application.domain.item.ItemLink;
import com.listywave.list.application.domain.item.ItemTitle;
import com.listywave.list.application.domain.item.Items;
import com.listywave.list.application.domain.label.Labels;
import com.listywave.list.application.domain.list.BackgroundColor;
import com.listywave.list.application.domain.list.BackgroundPalette;
import com.listywave.list.application.domain.list.ListDescription;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.application.domain.list.ListTitle;
import com.listywave.user.application.domain.User;
import java.util.List;
import java.util.stream.LongStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Collaboratros는 ")
class CollaboratorsTest {

    private User user;
    private ListEntity list;

    @BeforeEach
    void setUp() {
        user = User.init(1L, "hkim4410@naver.com", "kakaoAccessToken");
        list = new ListEntity(
                user,
                ANIMAL_PLANT,
                new ListTitle("리스트 제목"),
                new ListDescription("리스트 설명"),
                true,
                BackgroundPalette.PASTEL,
                BackgroundColor.PASTEL_GREEN,
                false,
                new Labels(List.of()),
                new Items(List.of(
                        Item.init(1, new ItemTitle("아이템 1등"), new ItemComment("1등 입니다."), new ItemLink(""), new ItemImageUrl("")),
                        Item.init(2, new ItemTitle("아이템 2등"), new ItemComment("1등 입니다."), new ItemLink(""), new ItemImageUrl("")),
                        Item.init(3, new ItemTitle("아이템 3등"), new ItemComment("1등 입니다."), new ItemLink(""), new ItemImageUrl(""))
                ))
        );
    }

    @Nested
    class 콜라보레이터_수_검증 {

        @Test
        void 콜라보레이터는_최대_20명까지_등록가능하다() {
            int size = 20;
            List<Collaborator> collaborators = createCollaborators(size);

            assertDoesNotThrow(() -> new Collaborators(collaborators));
        }

        @Test
        void 등록하는_콜라보레이터가_20명이_넘으면_예외가_발생한다() {
            int size = 21;
            List<Collaborator> collaborators = createCollaborators(size);

            assertThrows(CustomException.class, () -> new Collaborators(collaborators));
        }
    }

    @Nested
    class 콜라보레이터_필터링 {

        @Test
        void 삭제되는_콜라보레이터를_필터링한다() {
            // given
            User userA = User.init(1L, "aaaa@naver.com", "aaaa");
            User userB = User.init(2L, "bbbb@naver.com", "bbbb");
            User userC = User.init(3L, "cccc@naver.com", "cccc");
            User userD = User.init(4L, "dddd@naver.com", "dddd");

            ListEntity list = new ListEntity(
                    userA,
                    ETC,
                    new ListTitle("title"),
                    new ListDescription("description"),
                    true,
                    BackgroundPalette.PASTEL,
                    BackgroundColor.PASTEL_GREEN,
                    true,
                    new Labels(List.of()),
                    new Items(
                            List.of(
                                    Item.init(1, new ItemTitle(String.valueOf(1)), new ItemComment(String.valueOf(1)), new ItemLink(String.valueOf(1)), new ItemImageUrl(String.valueOf(1))),
                                    Item.init(2, new ItemTitle(String.valueOf(1)), new ItemComment(String.valueOf(1)), new ItemLink(String.valueOf(1)), new ItemImageUrl(String.valueOf(1))),
                                    Item.init(3, new ItemTitle(String.valueOf(1)), new ItemComment(String.valueOf(1)), new ItemLink(String.valueOf(1)), new ItemImageUrl(String.valueOf(1)))
                            )
                    )
            );

            Collaborators beforeCollaborators = new Collaborators(List.of(
                    Collaborator.init(userA, list),
                    Collaborator.init(userB, list),
                    Collaborator.init(userC, list),
                    Collaborator.init(userD, list)
            ));

            // when
            Collaborators newCollaborators = new Collaborators(List.of(
                    Collaborator.init(userA, list),
                    Collaborator.init(userD, list)
            ));
            Collaborators result = beforeCollaborators.filterRemovedCollaborators(newCollaborators);

            // then
            assertThat(result.collaborators().stream().map(Collaborator::getUser)).containsOnly(userB, userC);
        }

        @Test
        void 새로_추가되는_콜라보레이터를_필터링한다() {
            User userA = User.init(1L, "aaaa@naver.com", "aaaa");
            User userC = User.init(3L, "cccc@naver.com", "cccc");

            ListEntity list = new ListEntity(
                    userA,
                    ETC,
                    new ListTitle("title"),
                    new ListDescription("description"),
                    true,
                    BackgroundPalette.GRAY,
                    BackgroundColor.GRAY_LIGHT,
                    true,
                    new Labels(List.of()),
                    new Items(
                            List.of(
                                    Item.init(1, new ItemTitle(String.valueOf(1)), new ItemComment(String.valueOf(1)), new ItemLink(String.valueOf(1)), new ItemImageUrl(String.valueOf(1))),
                                    Item.init(2, new ItemTitle(String.valueOf(1)), new ItemComment(String.valueOf(1)), new ItemLink(String.valueOf(1)), new ItemImageUrl(String.valueOf(1))),
                                    Item.init(3, new ItemTitle(String.valueOf(1)), new ItemComment(String.valueOf(1)), new ItemLink(String.valueOf(1)), new ItemImageUrl(String.valueOf(1)))
                            )
                    )
            );

            Collaborators beforeCollaborators = new Collaborators(List.of(
            ));

            // when
            Collaborators newCollaborators = new Collaborators(List.of(
                    Collaborator.init(userA, list),
                    Collaborator.init(userC, list)
            ));
            Collaborators result = beforeCollaborators.filterAddedCollaborators(newCollaborators);

            // then
            assertThat(result.collaborators().stream().map(Collaborator::getUser)).containsOnly(userA, userC);
        }

        @Test
        void 콜라보레이터가_변경되기_전_후로_동일할_때_필터링_테스트() {
            // given
            User userA = User.init(1L, "aaaa@naver.com", "aaaa");
            ListEntity list = new ListEntity(
                    userA,
                    ETC,
                    new ListTitle("title"),
                    new ListDescription("description"),
                    true,
                    BackgroundPalette.GRAY,
                    BackgroundColor.GRAY_LIGHT,
                    true,
                    new Labels(List.of()),
                    new Items(
                            List.of(
                                    Item.init(1, new ItemTitle(String.valueOf(1)), new ItemComment(String.valueOf(1)), new ItemLink(String.valueOf(1)), new ItemImageUrl(String.valueOf(1))),
                                    Item.init(2, new ItemTitle(String.valueOf(1)), new ItemComment(String.valueOf(1)), new ItemLink(String.valueOf(1)), new ItemImageUrl(String.valueOf(1))),
                                    Item.init(3, new ItemTitle(String.valueOf(1)), new ItemComment(String.valueOf(1)), new ItemLink(String.valueOf(1)), new ItemImageUrl(String.valueOf(1)))
                            )
                    )
            );

            Collaborators beforeCollaborators = new Collaborators(List.of(Collaborator.init(userA, list)));
            Collaborators afterCollaborators = new Collaborators(List.of(Collaborator.init(userA, list)));

            // when
            Collaborators removed = beforeCollaborators.filterRemovedCollaborators(afterCollaborators);
            Collaborators added = beforeCollaborators.filterAddedCollaborators(afterCollaborators);

            // then
            assertThat(removed.collaborators()).hasSize(0);
            assertThat(added.collaborators()).hasSize(0);
        }
    }

    @Nested
    class 콜라보레이터_추가 {

        @Test
        void 콜라보레이터를_추가한다() {
            // given
            Collaborators collaborators = new Collaborators(createCollaborators(5));
            Collaborator newCollaborator = Collaborator.init(user, list);

            // when
            collaborators.add(newCollaborator);

            // then
            List<Collaborator> values = collaborators.collaborators();
            assertThat(values.get(5)).isEqualTo(newCollaborator);
        }

        @Test
        void 이미_동일한_콜라보레이터가_있다면_예외를_발생한다() {
            // given
            Collaborator collaborator = Collaborator.init(user, list);
            Collaborators collaborators = new Collaborators(List.of(collaborator));

            // when
            CustomException exception = assertThrows(CustomException.class, () -> collaborators.add(collaborator));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(DUPLICATE_COLLABORATOR_EXCEPTION);
        }
    }

    @Test
    void 특정_유저가_Collaborators에_포함되는지_여부를_반환한다() {
        // given
        Collaborator collaborator = Collaborator.init(user, list);
        Collaborators collaborators = new Collaborators(List.of(collaborator));

        User newUser = User.init(12L, "new@naver.com", "newKakaoAccessToken");

        // when
        // then
        assertThat(collaborators.contains(user)).isTrue();
        assertThat(collaborators.contains(newUser)).isFalse();
    }

    private List<Collaborator> createCollaborators(int size) {
        return LongStream.range(0, size)
                .mapToObj(i -> {
                    User user = User.init(i, String.valueOf(i), String.valueOf(i));
                    ListEntity list = new ListEntity(
                            user,
                            ETC,
                            new ListTitle(String.valueOf(i)),
                            new ListDescription(String.valueOf(i)),
                            true,
                            BackgroundPalette.LISTY,
                            BackgroundColor.LISTY_BLUE,
                            false,
                            new Labels(List.of()),
                            new Items(
                                    List.of(
                                            Item.init((int) i, new ItemTitle(String.valueOf(i)), new ItemComment(String.valueOf(i)), new ItemLink(String.valueOf(i)), new ItemImageUrl(String.valueOf(i))),
                                            Item.init((int) i + 1, new ItemTitle(String.valueOf(i)), new ItemComment(String.valueOf(i)), new ItemLink(String.valueOf(i)), new ItemImageUrl(String.valueOf(i))),
                                            Item.init((int) i + 2, new ItemTitle(String.valueOf(i)), new ItemComment(String.valueOf(i)), new ItemLink(String.valueOf(i)), new ItemImageUrl(String.valueOf(i)))
                                    )
                            )
                    );
                    return Collaborator.init(user, list);
                }).toList();
    }
}
