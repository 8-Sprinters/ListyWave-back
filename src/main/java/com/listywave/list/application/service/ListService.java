package com.listywave.list.application.service;

import static com.listywave.common.exception.ErrorCode.DUPLICATE_USER;
import static com.listywave.common.exception.ErrorCode.RESOURCES_EMPTY;

import com.listywave.alarm.application.domain.AlarmEvent;
import com.listywave.alarm.repository.AlarmRepository;
import com.listywave.collaborator.application.domain.Collaborator;
import com.listywave.collaborator.application.domain.Collaborators;
import com.listywave.collaborator.application.service.CollaboratorService;
import com.listywave.collaborator.repository.CollaboratorRepository;
import com.listywave.collection.repository.CollectionRepository;
import com.listywave.common.exception.CustomException;
import com.listywave.history.application.service.HistoryService;
import com.listywave.history.repository.HistoryRepository;
import com.listywave.image.application.service.ImageService;
import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.list.application.domain.comment.Comment;
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
import com.listywave.list.application.domain.list.ListEntities;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.application.domain.list.ListTitle;
import com.listywave.list.application.domain.list.SortType;
import com.listywave.list.application.dto.response.ListCreateResponse;
import com.listywave.list.application.dto.response.ListDetailResponse;
import com.listywave.list.application.dto.response.ListRecentResponse;
import com.listywave.list.application.dto.response.ListSearchResponse;
import com.listywave.list.application.dto.response.ListTrandingResponse;
import com.listywave.list.presentation.dto.request.ItemCreateRequest;
import com.listywave.list.presentation.dto.request.ListCreateRequest;
import com.listywave.list.presentation.dto.request.ListUpdateRequest;
import com.listywave.list.repository.CommentRepository;
import com.listywave.list.repository.ItemRepository;
import com.listywave.list.repository.label.LabelRepository;
import com.listywave.list.repository.list.ListRepository;
import com.listywave.list.repository.reply.ReplyRepository;
import com.listywave.user.application.domain.Follow;
import com.listywave.user.application.domain.User;
import com.listywave.user.application.dto.FindFeedListResponse;
import com.listywave.user.repository.follow.FollowRepository;
import com.listywave.user.repository.user.UserRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ListService {

    private final ImageService imageService;
    private final ListRepository listRepository;
    private final UserRepository userRepository;
    private final ReplyRepository replyRepository;
    private final FollowRepository followRepository;
    private final CommentRepository commentRepository;
    private final HistoryRepository historyRepository;
    private final ItemRepository itemRepository;
    private final LabelRepository labelRepository;
    private final CollectionRepository collectionRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final AlarmRepository alarmRepository;
    private final CollaboratorService collaboratorService;
    private final HistoryService historyService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public ListCreateResponse listCreate(ListCreateRequest request, Long loginUserId) {
        User user = userRepository.getById(loginUserId);

        List<Long> collaboratorIds = request.collaboratorIds();
        validateDuplicateCollaboratorIds(collaboratorIds);
        boolean hasCollaboration = !collaboratorIds.isEmpty();

        Labels labels = createLabels(request.labels());
        Items items = createItems(request.items());

        ListEntity list = new ListEntity(
                user,
                request.category(),
                new ListTitle(request.title()),
                new ListDescription(request.description()),
                request.isPublic(),
                request.backgroundPalette(),
                request.backgroundColor(),
                hasCollaboration,
                labels,
                items
        );
        ListEntity savedList = listRepository.save(list);

        historyService.saveHistory(savedList, LocalDateTime.now(), true);

        if (hasCollaboration) {
            Collaborators collaborators = collaboratorService.createCollaborators(collaboratorIds, savedList);
            List<Collaborator> saved = collaboratorService.saveAll(collaborators);
            saved.forEach(it -> applicationEventPublisher.publishEvent(AlarmEvent.collaborator(it, user)));
        }
        return ListCreateResponse.of(savedList.getId());
    }

    private void validateDuplicateCollaboratorIds(List<Long> collaboratorIds) {
        Set<Long> uniqueIds = new HashSet<>(collaboratorIds);
        if (collaboratorIds.size() != uniqueIds.size()) {
            throw new CustomException(DUPLICATE_USER, "중복된 콜라보레이터를 선택할 수 없습니다.");
        }
    }

    private Labels createLabels(List<String> labels) {
        return new Labels(labels.stream()
                .map(LabelName::new)
                .map(Label::init)
                .toList());
    }

    private Items createItems(List<ItemCreateRequest> itemCreateRequests) {
        return new Items(itemCreateRequests.stream()
                .map(it -> Item.init(
                        it.rank(),
                        new ItemTitle(it.title()), new ItemComment(it.comment()),
                        new ItemLink(it.link()), new ItemImageUrl(it.imageUrl()))
                ).toList());
    }

    public ListDetailResponse getListDetail(Long listId, Long loginUserId) {
        ListEntity list = listRepository.getById(listId);
        list.validateOwnerIsNotDelete();
        List<Collaborator> collaborators = collaboratorRepository.findAllByList(list);

        boolean isCollected = false;
        if (loginUserId != null) {
            User user = userRepository.getById(loginUserId);
            isCollected = collectionRepository.existsByListAndUserId(list, user.getId());
        }
        return ListDetailResponse.of(list, list.getUser(), isCollected, collaborators);
    }

    @Transactional(readOnly = true)
    public List<ListTrandingResponse> getTrandingList() {
        List<ListEntity> lists = listRepository.findTrandingLists();
        return lists.stream()
                .map(ListTrandingResponse::of)
                .toList();
    }

    public void deleteList(Long listId, Long loginUserId) {
        ListEntity list = listRepository.getById(listId);
        User loginUser = userRepository.getById(loginUserId);
        list.validateOwner(loginUser);

        collectionRepository.deleteAllByList(list);
        alarmRepository.deleteAllByListId(list.getId());
        imageService.deleteAllOfListImages(listId);
        collaboratorRepository.deleteAllByList(list);
        List<Comment> comments = commentRepository.findAllByList(list);
        replyRepository.deleteAllByCommentIn(comments);
        commentRepository.deleteAllInBatch(comments);
        historyRepository.deleteAllByList(list);
        listRepository.deleteById(listId);
    }

    @Transactional(readOnly = true)
    public ListRecentResponse getRecentLists(Long loginUserId, LocalDateTime cursorUpdatedDate, Pageable pageable) {
        if (loginUserId != null) {
            User user = userRepository.getById(loginUserId);
            List<Follow> follows = followRepository.getAllByFollowerUser(user);

            List<User> myFollowingUsers = follows.stream()
                    .map(Follow::getFollowingUser)
                    .filter(followingUser -> !followingUser.isDelete())
                    .toList();

            Slice<ListEntity> result =
                    listRepository.getRecentListsByFollowing(myFollowingUsers, cursorUpdatedDate, pageable);
            return getListRecentResponse(result);
        }
        Slice<ListEntity> result = listRepository.getRecentLists(cursorUpdatedDate, pageable);
        return getListRecentResponse(result);
    }

    private ListRecentResponse getListRecentResponse(Slice<ListEntity> result) {
        List<ListEntity> recentList = result.getContent();

        LocalDateTime cursorUpdatedDate = null;
        if (!recentList.isEmpty()) {
            cursorUpdatedDate = recentList.get(recentList.size() - 1).getUpdatedDate();
        }
        return ListRecentResponse.of(recentList, cursorUpdatedDate, result.hasNext());
    }

    @Transactional(readOnly = true)
    public ListSearchResponse search(String keyword, SortType sortType, CategoryType category, int size, Long cursorId) {
        List<ListEntity> lists = listRepository.findAll().stream()
                .filter(list -> !list.isDeletedUser() && list.isPublic())
                .toList();
        ListEntities allList = new ListEntities(lists);
        ListEntities filtered = allList.filterBy(category)
                .filterBy(keyword)
                .sortBy(sortType, keyword);

        long totalCount = filtered.size();

        ListEntity cursorList = (cursorId == 0L) ? null : listRepository.getById(cursorId);
        List<ListEntity> paged = filtered.paging(cursorList, size + 1).listEntities();

        if (paged.size() > size) {
            return ListSearchResponse.of(paged.subList(0, size), totalCount, paged.get(size - 1).getId(), true);
        }
        if (paged.isEmpty()) {
            return ListSearchResponse.of(paged, totalCount, null, false);
        }
        return ListSearchResponse.of(paged, totalCount, paged.get(paged.size() - 1).getId(), false);
    }

    public void update(Long listId, Long loginUserId, ListUpdateRequest request) {
        validateDuplicateCollaboratorIds(request.collaboratorIds());

        User loginUser = userRepository.getById(loginUserId);
        ListEntity list = listRepository.getById(listId);

        Collaborators beforeCollaborators = collaboratorService.findAllByList(list);
        list.validateUpdateAuthority(loginUser, beforeCollaborators);
        Collaborators newCollaborators = collaboratorService.createCollaborators(request.collaboratorIds(), list);
        updateCollaborators(beforeCollaborators, newCollaborators);

        Labels newLabels = createLabels(request.labels());
        Items newItems = createItems(request.items());

        boolean doesChangedAnyItemRank = list.canCreateHistory(newItems);

        LocalDateTime updatedDate = LocalDateTime.now();
        boolean hasCollaborator = !request.collaboratorIds().isEmpty();
        list.update(
                request.category(),
                new ListTitle(request.title()),
                new ListDescription(request.description()),
                request.isPublic(),
                request.backgroundPalette(),
                request.backgroundColor(),
                hasCollaborator,
                updatedDate,
                newLabels,
                newItems
        );

        if (doesChangedAnyItemRank) {
            historyService.saveHistory(list, updatedDate, true);
        }
    }

    private void updateCollaborators(Collaborators beforeCollaborators, Collaborators newCollaborators) {
        Collaborators removedCollaborators = beforeCollaborators.filterRemovedCollaborators(newCollaborators);
        collaboratorRepository.deleteAllInBatch(removedCollaborators.collaborators());

        Collaborators addedCollaborators = beforeCollaborators.filterAddedCollaborators(newCollaborators);
        collaboratorRepository.saveAll(addedCollaborators.collaborators());
    }

    public void deleteLists(Long loginUserId, List<Long> listIds) {
        List<ListEntity> lists = listRepository.findAllById(listIds);
        if (lists.isEmpty()) {
            throw new CustomException(RESOURCES_EMPTY, "해당 리스트들이 존재하지 않습니다.");
        }
        User loginUser = userRepository.getById(loginUserId);
        lists.forEach(list -> list.validateOwner(loginUser));

        deleteListImages(lists);
        alarmRepository.deleteAllByListIdIn(listIds);
        collectionRepository.deleteAllByListIn(lists);
        collaboratorRepository.deleteAllByListIn(lists);
        List<Comment> comments = commentRepository.findAllByListIn(lists);
        replyRepository.deleteAllByCommentIn(comments);
        commentRepository.deleteAllInBatch(comments);
        itemRepository.deleteAllListIn(lists);
        labelRepository.deleteAllListIn(lists);
        historyRepository.deleteAllByListIn(lists);
        listRepository.deleteAllInBatch(lists);
    }

    private void deleteListImages(List<ListEntity> lists) {
        lists.forEach(list -> imageService.deleteAllOfListImages(list.getId()));
    }

    @Transactional(readOnly = true)
    public FindFeedListResponse findFeedList(
            Long targetUserId,
            String type,
            CategoryType category,
            LocalDateTime cursorUpdatedDate,
            Pageable pageable
    ) {
        userRepository.getById(targetUserId);

        Slice<ListEntity> result =
                listRepository.findFeedLists(targetUserId, type, category, cursorUpdatedDate, pageable);
        List<ListEntity> lists = result.getContent();

        cursorUpdatedDate = null;
        if (!lists.isEmpty()) {
            cursorUpdatedDate = lists.get(lists.size() - 1).getUpdatedDate();
        }
        return FindFeedListResponse.of(result.hasNext(), cursorUpdatedDate, lists);
    }
}
