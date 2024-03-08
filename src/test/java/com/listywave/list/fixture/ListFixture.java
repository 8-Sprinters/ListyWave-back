package com.listywave.list.fixture;

import static com.listywave.acceptance.list.ListAcceptanceTestHelper.아이템_순위가_바뀐_좋아하는_견종_TOP3_요청_데이터;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.좋아하는_견종_TOP3_생성_요청_데이터;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.좋아하는_라면_TOP3_생성_요청_데이터;
import static com.listywave.list.application.domain.category.CategoryFixture.무작위_카테고리_추출;

import com.listywave.list.application.domain.item.Item;
import com.listywave.list.application.domain.item.ItemComment;
import com.listywave.list.application.domain.item.ItemImageUrl;
import com.listywave.list.application.domain.item.ItemLink;
import com.listywave.list.application.domain.item.ItemTitle;
import com.listywave.list.application.domain.item.Items;
import com.listywave.list.application.domain.label.Label;
import com.listywave.list.application.domain.label.LabelName;
import com.listywave.list.application.domain.label.Labels;
import com.listywave.list.application.domain.list.ListDescription;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.application.domain.list.ListTitle;
import com.listywave.list.presentation.dto.request.ListCreateRequest;
import com.listywave.user.application.domain.User;
import java.util.List;
import java.util.stream.IntStream;

public abstract class ListFixture {

    public static ListEntity 가장_좋아하는_견종_TOP3(User owner, List<Long> collaboratorIds) {
        ListCreateRequest request = 좋아하는_견종_TOP3_생성_요청_데이터(collaboratorIds);
        boolean hasCollaborators = !request.collaboratorIds().isEmpty();

        Labels labels = new Labels(request.labels().stream()
                .map(LabelName::new)
                .map(Label::init)
                .toList());

        Items items = new Items(request.items().stream()
                .map(it -> Item.init(
                        it.rank(),
                        new ItemTitle(it.title()),
                        new ItemComment(it.comment()),
                        new ItemLink(it.link()),
                        new ItemImageUrl(it.imageUrl())
                )).toList());

        return new ListEntity(
                owner, request.category(), new ListTitle(request.title()),
                new ListDescription(request.description()), request.isPublic(),
                request.backgroundColor(), hasCollaborators, labels, items
        );
    }

    public static ListEntity 가장_좋아하는_견종_TOP3_순위_변경(User owner, List<Long> collaboratorIds) {
        ListCreateRequest request = 아이템_순위가_바뀐_좋아하는_견종_TOP3_요청_데이터(collaboratorIds);
        boolean hasCollaborators = !request.collaboratorIds().isEmpty();

        Labels labels = new Labels(request.labels().stream()
                .map(LabelName::new)
                .map(Label::init)
                .toList());

        Items items = new Items(request.items().stream()
                .map(it -> Item.init(
                        it.rank(),
                        new ItemTitle(it.title()),
                        new ItemComment(it.comment()),
                        new ItemLink(it.link()),
                        new ItemImageUrl(it.imageUrl())
                )).toList());

        return new ListEntity(
                owner, request.category(), new ListTitle(request.title()),
                new ListDescription(request.description()), request.isPublic(),
                request.backgroundColor(), hasCollaborators, labels, items
        );
    }

    public static ListEntity 좋아하는_라면_TOP3(User owner, List<Long> collaboratorIds) {
        ListCreateRequest request = 좋아하는_라면_TOP3_생성_요청_데이터(collaboratorIds);
        boolean hasCollaborators = !request.collaboratorIds().isEmpty();

        Labels labels = new Labels(request.labels().stream()
                .map(LabelName::new)
                .map(Label::init)
                .toList());

        Items items = new Items(request.items().stream()
                .map(it -> Item.init(
                        it.rank(),
                        new ItemTitle(it.title()),
                        new ItemComment(it.comment()),
                        new ItemLink(it.link()),
                        new ItemImageUrl(it.imageUrl())
                )).toList());

        return new ListEntity(
                owner, request.category(), new ListTitle(request.title()),
                new ListDescription(request.description()), request.isPublic(),
                request.backgroundColor(), hasCollaborators, labels, items
        );
    }

    public static List<ListEntity> 지정된_개수만큼_리스트를_생성한다(User owner, int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> new ListEntity(
                        owner,
                        무작위_카테고리_추출(),
                        new ListTitle(i + "번째 리스트"),
                        new ListDescription(i + "번째 리스트"),
                        true,
                        "#123456",
                        false,
                        new Labels(List.of()),
                        new Items(List.of(
                                Item.init(
                                        i,
                                        new ItemTitle(String.valueOf(i)),
                                        new ItemComment(String.valueOf(i)),
                                        new ItemLink(String.valueOf(i)),
                                        new ItemImageUrl(String.valueOf(i))
                                ),
                                Item.init(
                                        i + 1,
                                        new ItemTitle(String.valueOf(i + 1)),
                                        new ItemComment(String.valueOf(i + 1)),
                                        new ItemLink(String.valueOf(i + 1)),
                                        new ItemImageUrl(String.valueOf(i + 1))
                                ),
                                Item.init(
                                        i + 2,
                                        new ItemTitle(String.valueOf(i + 2)),
                                        new ItemComment(String.valueOf(i + 2)),
                                        new ItemLink(String.valueOf(i + 2)),
                                        new ItemImageUrl(String.valueOf(i + 2))
                                )
                        ))
                )).toList();
    }
}
