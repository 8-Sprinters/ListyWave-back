package com.listywave.list.application.service;

import com.listywave.auth.application.domain.JwtManager;
import com.listywave.collaborator.domain.Collaborator;
import com.listywave.collaborator.domain.repository.CollaboratorRepository;
import com.listywave.common.exception.CustomException;
import com.listywave.common.exception.ErrorCode;
import com.listywave.common.util.UserUtil;
import com.listywave.list.application.domain.Item;
import com.listywave.list.application.domain.Lists;
import com.listywave.list.application.dto.ListCreateCommand;
import com.listywave.list.application.dto.response.ListCreateResponse;
import com.listywave.list.application.dto.response.ListDetailResponse;
import com.listywave.list.application.dto.response.ListRecentResponse;
import com.listywave.list.application.dto.response.ListTrandingResponse;
import com.listywave.list.presentation.dto.request.ItemCreateRequest;
import com.listywave.list.repository.list.ListRepository;
import com.listywave.user.application.domain.Follow;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.follow.FollowRepository;
import com.listywave.user.repository.user.UserRepository;
import java.util.List;
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
    private final ListRepository listRepository;
    private final UserRepository userRepository;
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

        Lists list = Lists.createList(
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


    private List<User> findExistingCollaborators(List<Long> collaboratorIds) {
        List<User> existingCollaborators = userRepository.findAllById(collaboratorIds);

        List<Long> nonExistingIds = collaboratorIds.stream()
                .filter(
                        id -> existingCollaborators.stream()
                                .noneMatch(user -> user.getId().equals(id))
                )
                .toList();

        if (!nonExistingIds.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND, "콜라보레이터로 등록한 회원이 존재하지 않습니다.");
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
        Lists list = listRepository.getById(listId);
        List<Collaborator> collaborators = collaboratorRepository.findAllByListId(listId);

        if (accessToken.isBlank()) {
            return ListDetailResponse.of(list, list.getUser(), false, collaborators);
        }
        return ListDetailResponse.of(list, list.getUser(), true, collaborators);
    }

    @Transactional(readOnly = true)
    public List<ListTrandingResponse> getTrandingList() {
        List<Lists> lists = listRepository.findTrandingLists();
        lists.forEach(Lists::sortItems); // <-- 추가 !
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

    @Transactional(readOnly = true)
    public ListRecentResponse getRecentLists(String accessToken) {

        if (isSignedIn(accessToken)) {
            List<Lists> recentLists = listRepository.getRecentLists();
            return ListRecentResponse.of(recentLists);
        } else {
            Long loginUserId = jwtManager.read(accessToken);
            User user = userRepository.getById(loginUserId);
            List<Follow> follows = followRepository.getAllByFollowerUser(user);

            List<User> followingUsers = follows.stream()
                    .map(Follow::getFollowingUser)
                    .toList();
            List<Lists> recentListsByFollowing = listRepository.getRecentListsByFollowing(followingUsers);
            return ListRecentResponse.of(recentListsByFollowing);
        }
    }

    private boolean isSignedIn(String accessToken) {
        return accessToken.isBlank();
    }
}
