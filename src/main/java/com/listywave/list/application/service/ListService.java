package com.listywave.list.application.service;

import static com.listywave.common.exception.ErrorCode.INVALID_ACCESS;

import com.listywave.auth.application.domain.JwtManager;
import com.listywave.collaborator.application.domain.Collaborator;
import com.listywave.collaborator.application.domain.Collaborators;
import com.listywave.collaborator.repository.CollaboratorRepository;
import com.listywave.common.exception.CustomException;
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
import com.listywave.list.presentation.dto.request.ListCreateRequest;
import com.listywave.list.repository.CommentRepository;
import com.listywave.list.repository.list.ListRepository;
import com.listywave.list.repository.reply.ReplyRepository;
import com.listywave.user.application.domain.Follow;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.follow.FollowRepository;
import com.listywave.user.repository.user.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ListService {

    private final JwtManager jwtManager;
    private final ImageService imageService;
    private final ListRepository listRepository;
    private final UserRepository userRepository;
    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final FollowRepository followRepository;

    public ListCreateResponse listCreate(ListCreateRequest request, String accessToken) {
        Long userId = jwtManager.read(accessToken);
        User user = userRepository.getById(userId);

        List<Long> collaboratorIds = request.collaboratorIds();
        Collaborators.validateDuplicateCollaboratorIds(collaboratorIds);
        boolean hasCollaboration = !collaboratorIds.isEmpty();

        Labels labels = createLabels(request);
        Items items = createItems(request);

        ListEntity list = new ListEntity(user, request.category(), new ListTitle(request.title()),
                new ListDescription(request.description()), request.isPublic(),
                request.backgroundColor(), hasCollaboration, labels, items);
        ListEntity savedList = listRepository.save(list);

        if (hasCollaboration) {
            saveCollaborators(collaboratorIds, savedList);
        }

        return ListCreateResponse.of(savedList.getId());
    }

    private Labels createLabels(ListCreateRequest request) {
        return new Labels(request.labels().stream()
                .map(LabelName::new)
                .map(Label::init)
                .toList());
    }

    private Items createItems(ListCreateRequest request) {
        return new Items(request.items().stream()
                .map(it -> Item.init(
                        it.rank(),
                        new ItemTitle(it.title()), new ItemComment(it.comment()),
                        new ItemLink(it.comment()), new ItemImageUrl(it.imageUrl()))
                ).toList());
    }

    private void saveCollaborators(List<Long> collaboratorIds, ListEntity list) {
        List<Collaborator> collaborators = collaboratorIds.stream()
                .map(userRepository::getById)
                .map(user -> Collaborator.init(user, list))
                .toList();
        collaboratorRepository.saveAll(collaborators);
    }

    public ListDetailResponse getListDetail(Long listId, String accessToken) {
        ListEntity list = listRepository.getById(listId);
        List<Collaborator> collaborators = collaboratorRepository.findAllByList(list);

        if (accessToken.isBlank()) {
            return ListDetailResponse.of(list, list.getUser(), false, collaborators);
        }
        return ListDetailResponse.of(list, list.getUser(), true, collaborators);
    }

    @Transactional(readOnly = true)
    public List<ListTrandingResponse> getTrandingList() {
        List<ListEntity> lists = listRepository.findTrandingLists();
        lists.forEach(ListEntity::sortItemsByRank);
        return lists.stream()
                .map(list -> ListTrandingResponse.of(list, list.getFirstItemImageUrl()))
                .toList();
    }

    public void deleteList(Long listId, String accessToken) {
        ListEntity list = listRepository.getById(listId);
        Long loginUserId = jwtManager.read(accessToken);
        User loginUser = userRepository.getById(loginUserId);

        if (!list.canDeleteBy(loginUser)) {
            throw new CustomException(INVALID_ACCESS, "리스트는 작성자만 삭제 가능합니다.");
        }

        imageService.deleteAllOfListImages(listId);
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
            return ListRecentResponse.of(listRepository.getRecentListsByFollowing(followingUsers));
        }

        return ListRecentResponse.of(listRepository.getRecentLists());
    }

    private boolean isSignedIn(String accessToken) {
        return !accessToken.isBlank();
    }

    public ListSearchResponse search(String keyword, SortType sortType, CategoryType category, int size, Long cursorId) {
        ListEntities allList = new ListEntities(listRepository.findAll());
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
}
