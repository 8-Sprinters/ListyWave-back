package com.listywave.collaborator.application.domain;

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
import com.listywave.list.application.domain.list.ListDescription;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.application.domain.list.ListTitle;
import com.listywave.user.application.domain.User;
import java.util.List;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;

class CollaboratorsTest {

    @Test
    void 콜라보레이터는_최대_20명까지_등록가능하다() {
        // given
        List<Collaborator> collaborators = LongStream.range(0, 20)
                .mapToObj(i -> {
                    User user = User.init(i, String.valueOf(i), String.valueOf(i));
                    ListEntity list = new ListEntity(
                            user,
                            ETC,
                            new ListTitle(String.valueOf(i)),
                            new ListDescription(String.valueOf(i)),
                            true,
                            String.valueOf(i),
                            false,
                            new Labels(List.of()),
                            new Items(List.of(
                                    Item.init((int) i, new ItemTitle(String.valueOf(i)), new ItemComment(String.valueOf(i)), new ItemLink(String.valueOf(i)), new ItemImageUrl(String.valueOf(i))),
                                    Item.init((int) i + 1, new ItemTitle(String.valueOf(i)), new ItemComment(String.valueOf(i)), new ItemLink(String.valueOf(i)), new ItemImageUrl(String.valueOf(i))),
                                    Item.init((int) i + 2, new ItemTitle(String.valueOf(i)), new ItemComment(String.valueOf(i)), new ItemLink(String.valueOf(i)), new ItemImageUrl(String.valueOf(i)))
                            ))
                    );
                    return Collaborator.init(user, list);
                }).toList();

        // then
        assertDoesNotThrow(() -> new Collaborators(collaborators));
    }

    @Test
    void 등록하는_콜라보레이터가_20명이_넘으면_예외가_발생한다() {
        // given
        List<Collaborator> collaborators = LongStream.range(0, 21)
                .mapToObj(i -> {
                    User user = User.init(i, String.valueOf(i), String.valueOf(i));
                    ListEntity list = new ListEntity(
                            user,
                            ETC,
                            new ListTitle(String.valueOf(i)),
                            new ListDescription(String.valueOf(i)),
                            true,
                            String.valueOf(i),
                            false,
                            new Labels(List.of()),
                            new Items(List.of(
                                    Item.init((int) i, new ItemTitle(String.valueOf(i)), new ItemComment(String.valueOf(i)), new ItemLink(String.valueOf(i)), new ItemImageUrl(String.valueOf(i))),
                                    Item.init((int) i + 1, new ItemTitle(String.valueOf(i)), new ItemComment(String.valueOf(i)), new ItemLink(String.valueOf(i)), new ItemImageUrl(String.valueOf(i))),
                                    Item.init((int) i + 2, new ItemTitle(String.valueOf(i)), new ItemComment(String.valueOf(i)), new ItemLink(String.valueOf(i)), new ItemImageUrl(String.valueOf(i)))
                            ))
                    );
                    return Collaborator.init(user, list);
                }).toList();

        // then
        assertThrows(CustomException.class, () -> new Collaborators(collaborators));
    }

    @Test
    void 리스트를_수정할_수_있는_사용자인지_검증한다() {
        // given
        User userA = User.init(1L, "aaaa@naver.com", "aaaa");
        User userB = User.init(2L, "bbbb@naver.com", "bbbb");
        ListEntity list = new ListEntity(userA, ETC, new ListTitle("title"), new ListDescription("description"), true, "#123345", true, new Labels(List.of()), new Items(List.of(
                Item.init(1, new ItemTitle(String.valueOf(1)), new ItemComment(String.valueOf(1)), new ItemLink(String.valueOf(1)), new ItemImageUrl(String.valueOf(1))),
                Item.init(2, new ItemTitle(String.valueOf(1)), new ItemComment(String.valueOf(1)), new ItemLink(String.valueOf(1)), new ItemImageUrl(String.valueOf(1))),
                Item.init(3, new ItemTitle(String.valueOf(1)), new ItemComment(String.valueOf(1)), new ItemLink(String.valueOf(1)), new ItemImageUrl(String.valueOf(1)))
        )));

        Collaborators collaborators = new Collaborators(List.of(
                Collaborator.init(userA, list),
                Collaborator.init(userB, list)
        ));

        // then
        User userC = User.init(3L, "cccc@naver.com", "cccc");
        assertThrows(CustomException.class, () -> collaborators.validateListUpdateAuthority(userC));

        assertDoesNotThrow(() -> collaborators.validateListUpdateAuthority(userA));
    }

    @Test
    void 삭제되는_콜라보레이터를_필터링한다() {
        // given
        User userA = User.init(1L, "aaaa@naver.com", "aaaa");
        User userB = User.init(2L, "bbbb@naver.com", "bbbb");
        User userC = User.init(3L, "cccc@naver.com", "cccc");
        User userD = User.init(4L, "dddd@naver.com", "dddd");

        ListEntity list = new ListEntity(userA, ETC, new ListTitle("title"), new ListDescription("description"), true, "#123345", true, new Labels(List.of()), new Items(List.of(
                Item.init(1, new ItemTitle(String.valueOf(1)), new ItemComment(String.valueOf(1)), new ItemLink(String.valueOf(1)), new ItemImageUrl(String.valueOf(1))),
                Item.init(2, new ItemTitle(String.valueOf(1)), new ItemComment(String.valueOf(1)), new ItemLink(String.valueOf(1)), new ItemImageUrl(String.valueOf(1))),
                Item.init(3, new ItemTitle(String.valueOf(1)), new ItemComment(String.valueOf(1)), new ItemLink(String.valueOf(1)), new ItemImageUrl(String.valueOf(1)))
        )));

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
        assertThat(result.getCollaborators()).hasSize(2);
        assertThat(result.getCollaborators().stream().map(Collaborator::getUser)).containsOnly(userB, userC);
    }

    @Test
    void 새로_추가되는_콜라보레이터를_필터링한다() {
        User userA = User.init(1L, "aaaa@naver.com", "aaaa");
        User userC = User.init(3L, "cccc@naver.com", "cccc");

        ListEntity list = new ListEntity(userA, ETC, new ListTitle("title"), new ListDescription("description"), true, "#123345", true, new Labels(List.of()), new Items(List.of(
                Item.init(1, new ItemTitle(String.valueOf(1)), new ItemComment(String.valueOf(1)), new ItemLink(String.valueOf(1)), new ItemImageUrl(String.valueOf(1))),
                Item.init(2, new ItemTitle(String.valueOf(1)), new ItemComment(String.valueOf(1)), new ItemLink(String.valueOf(1)), new ItemImageUrl(String.valueOf(1))),
                Item.init(3, new ItemTitle(String.valueOf(1)), new ItemComment(String.valueOf(1)), new ItemLink(String.valueOf(1)), new ItemImageUrl(String.valueOf(1)))
        )));

        Collaborators beforeCollaborators = new Collaborators(List.of(
        ));

        // when
        Collaborators newCollaborators = new Collaborators(List.of(
                Collaborator.init(userA, list),
                Collaborator.init(userC, list)
        ));
        Collaborators result = beforeCollaborators.filterAddedCollaborators(newCollaborators);

        // then
        assertThat(result.getCollaborators()).hasSize(2);
        assertThat(result.getCollaborators().stream().map(Collaborator::getUser)).containsOnly(userA, userC);
    }

    @Test
    void 콜라보레이터가_변경되기_전_후로_동일할_때_필터링_테스트() {
        // given
        User userA = User.init(1L, "aaaa@naver.com", "aaaa");
        ListEntity list = new ListEntity(userA, ETC, new ListTitle("title"), new ListDescription("description"), true, "#123345", true, new Labels(List.of()), new Items(List.of(
                Item.init(1, new ItemTitle(String.valueOf(1)), new ItemComment(String.valueOf(1)), new ItemLink(String.valueOf(1)), new ItemImageUrl(String.valueOf(1))),
                Item.init(2, new ItemTitle(String.valueOf(1)), new ItemComment(String.valueOf(1)), new ItemLink(String.valueOf(1)), new ItemImageUrl(String.valueOf(1))),
                Item.init(3, new ItemTitle(String.valueOf(1)), new ItemComment(String.valueOf(1)), new ItemLink(String.valueOf(1)), new ItemImageUrl(String.valueOf(1)))
        )));

        Collaborators beforeCollaborators = new Collaborators(List.of(Collaborator.init(userA, list)));
        Collaborators afterCollaborators = new Collaborators(List.of(Collaborator.init(userA, list)));

        // when
        Collaborators removed = beforeCollaborators.filterRemovedCollaborators(afterCollaborators);
        Collaborators added = beforeCollaborators.filterAddedCollaborators(afterCollaborators);

        // then
        assertThat(removed.getCollaborators()).hasSize(0);
        assertThat(added.getCollaborators()).hasSize(0);
    }
}
