package com.listywave.list.application.service;

import static com.listywave.list.application.domain.SortType.COLLECTED;
import static com.listywave.list.application.domain.SortType.OLD;

import com.listywave.auth.application.domain.JwtManager;
import com.listywave.collaborator.application.domain.Collaborator;
import com.listywave.collaborator.repository.CollaboratorRepository;
import com.listywave.common.exception.CustomException;
import com.listywave.common.exception.ErrorCode;
import com.listywave.common.util.UserUtil;
import com.listywave.image.application.service.ImageService;
import com.listywave.list.application.domain.CategoryType;
import com.listywave.list.application.domain.Comment;
import com.listywave.list.application.domain.Item;
import com.listywave.list.application.domain.ListEntity;
import com.listywave.list.application.domain.SortType;
import com.listywave.list.application.dto.ListCreateCommand;
import com.listywave.list.application.dto.response.ListCreateResponse;
import com.listywave.list.application.dto.response.ListDetailResponse;
import com.listywave.list.application.dto.response.ListRecentResponse;
import com.listywave.list.application.dto.response.ListSearchResponse;
import com.listywave.list.application.dto.response.ListTrandingResponse;
import com.listywave.list.presentation.dto.request.ItemCreateRequest;
import com.listywave.list.repository.CommentRepository;
import com.listywave.list.repository.list.ListRepository;
import com.listywave.list.repository.reply.ReplyRepository;
import com.listywave.user.application.domain.Follow;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.follow.FollowRepository;
import com.listywave.user.repository.user.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ListService {

    private final UserUtil userUtil;
    private final JwtManager jwtManager;
    private final ImageService imageService;
    private final ListRepository listRepository;
    private final UserRepository userRepository;
    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final FollowRepository followRepository;

    public ListCreateResponse listCreate(
            ListCreateCommand listCreateCommand,
            List<String> labels,
            List<Long> collaboratorIds,
            List<ItemCreateRequest> items
    ) {
        //TODO: 글쓰는 회원이 실제 존재하는지 검증 (security 이용해서 해야함)
        final User user = userUtil.getUserByUserid(listCreateCommand.ownerId());

        Boolean isLabels = isLabelCountValid(labels);
        validateItemsCount(items);
        Boolean hasCollaboratorId = isExistCollaborator(collaboratorIds);
        validateDuplicateCollaborators(collaboratorIds);

        ListEntity list = ListEntity.createList(
                user,
                listCreateCommand,
                labels,
                items,
                isLabels,
                hasCollaboratorId
        );
        listRepository.save(list);

        if (hasCollaboratorId) {
            List<User> users = findExistingCollaborators(collaboratorIds);

            List<Collaborator> collaborators = users.stream()
                    .map(u -> Collaborator.createCollaborator(u, list))
                    .collect(Collectors.toList());
            collaboratorRepository.saveAll(collaborators);
        }
        return ListCreateResponse.of(list.getId());
    }

    private void validateDuplicateCollaborators(List<Long> collaboratorIds) {
        Set<Long> uniqueIds = new HashSet<>();
        Set<Long> duplicateIds = collaboratorIds.stream()
                .filter(id -> !uniqueIds.add(id)) // 중복된 ID 필터링
                .collect(Collectors.toSet());

        if (!duplicateIds.isEmpty()) {
            throw new CustomException(ErrorCode.DUPLICATE_USER, "중복된 콜라보레이터를 선택할 수 없습니다.");
        }
    }

    private List<User> findExistingCollaborators(List<Long> collaboratorIds) {
        List<User> existingCollaborators = userRepository.findAllById(collaboratorIds);

        List<Long> nonExistingIds = collaboratorIds.stream()
                .filter(
                        id -> existingCollaborators.stream()
                                .noneMatch(user -> user.getId().equals(id))
                )
                .toList();

        if (!nonExistingIds.isEmpty()) {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "콜라보레이터로 등록한 회원이 존재하지 않습니다.");
        }
        return existingCollaborators;
    }

    private Boolean isExistCollaborator(List<Long> collaboratorIds) {
        if (collaboratorIds != null && collaboratorIds.size() > 20) {
            throw new CustomException(ErrorCode.INVALID_COUNT, "콜라보레이터는 최대 20명까지 가능합니다.");
        }
        return collaboratorIds != null && !collaboratorIds.isEmpty();
    }

    private void validateItemsCount(List<ItemCreateRequest> items) {
        if (items.size() < 3 || items.size() > 10) {
            throw new CustomException(ErrorCode.INVALID_COUNT, "아이템의 개수는 3개에서 10개까지 가능합니다.");
        }
    }

    private Boolean isLabelCountValid(List<String> labels) {
        if (labels == null || labels.isEmpty()) {
            return false;
        }
        if (labels.size() > 3) {
            throw new CustomException(ErrorCode.INVALID_COUNT, "라벨의 개수는 최대 3개까지 작성 가능합니다.");
        }
        return true;
    }

    public ListDetailResponse getListDetail(Long listId, String accessToken) {
        ListEntity list = listRepository.getById(listId);
        List<Collaborator> collaborators = collaboratorRepository.findAllByListId(listId);

        if (accessToken.isBlank()) {
            return ListDetailResponse.of(list, list.getUser(), false, collaborators);
        }
        return ListDetailResponse.of(list, list.getUser(), true, collaborators);
    }

    @Transactional(readOnly = true)
    public List<ListTrandingResponse> getTrandingList() {
        List<ListEntity> lists = listRepository.findTrandingLists();
        lists.forEach(ListEntity::sortItems);
        return lists.stream()
                .map(list -> ListTrandingResponse.of(list, getImageUrlTopRankItem(list.getItems())))
                .toList();
    }

    private String getImageUrlTopRankItem(List<Item> items) {
        return items.stream()
                .map(Item::getImageUrl)
                .filter(url -> !url.isEmpty())
                .findFirst()
                .orElse("");
    }

    public void deleteList(Long listId) {
        imageService.deleteAllOfListImages(listId);

        ListEntity list = listRepository.getById(listId);

        collaboratorRepository.deleteAllByList(list);
        List<Comment> comments = commentRepository.findAllByList(list);
        replyRepository.deleteAllByCommentIn(comments);
        commentRepository.deleteAllInBatch(comments);

        listRepository.deleteById(listId);
    }

    @Transactional(readOnly = true)
    public ListRecentResponse getRecentLists(String accessToken) {
        if (isSignedIn(accessToken)) {
            Long loginUserId = jwtManager.read(accessToken);
            User user = userRepository.getById(loginUserId);
            List<Follow> follows = followRepository.getAllByFollowerUser(user);

            List<User> followingUsers = follows.stream()
                    .map(Follow::getFollowingUser)
                    .toList();
            List<ListEntity> recentListByFollowing = listRepository.getRecentListsByFollowing(followingUsers);
            return ListRecentResponse.of(recentListByFollowing);
        }
        List<ListEntity> recentLists = listRepository.getRecentLists();
        return ListRecentResponse.of(recentLists);

    }

    private boolean isSignedIn(String accessToken) {
        return !accessToken.isBlank();
    }

    // TODO: 관련도 순 추가 (List 일급 컬렉션 만들어서 Scoring 하는 방식)
    // TODO: 리팩터링
    public ListSearchResponse search(String keyword, SortType sortType, CategoryType category, int size, Long cursorId) {
        List<ListEntity> all = listRepository.findAll();

        List<ListEntity> filtered = all.stream()
                .filter(list -> list.isIncluded(category))
                .filter(list -> list.isRelatedWith(keyword))
                .sorted((list, other) -> {
                    if (sortType.equals(OLD)) {
                        return list.getUpdatedDate().compareTo(other.getUpdatedDate());
                    }
                    if (sortType.equals(COLLECTED)) {
                        return -(list.getCollectCount() - other.getCollectCount());
                    }
                    return -(list.getUpdatedDate().compareTo(other.getUpdatedDate()));
                })
                .toList();

        List<ListEntity> result;
        if (cursorId == 0L) {
            if (filtered.size() >= size) {
                return ListSearchResponse.of(filtered.subList(0, size), (long) filtered.size(), filtered.get(size - 1).getId(), true);
            }
            return ListSearchResponse.of(filtered, (long) filtered.size(), filtered.get(filtered.size() - 1).getId(), false);
        } else {
            ListEntity cursorList = listRepository.getById(cursorId);

            int cursorIndex = filtered.indexOf(cursorList);
            int startIndex = cursorIndex + 1;
            int endIndex = cursorIndex + 1 + size;

            if (endIndex >= filtered.size()) {
                endIndex = filtered.size() - 1;
            }

            result = filtered.subList(startIndex, endIndex + 1);
        }

        int totalCount = filtered.size();
        if (result.size() < size) {
            return ListSearchResponse.of(result, (long) totalCount, result.get(result.size() - 1).getId(), false);
        }
        boolean hasNext = result.size() > size;
        result = result.subList(0, size);
        return ListSearchResponse.of(result, (long) totalCount, result.get(result.size() - 1).getId(), hasNext);
    }
}
