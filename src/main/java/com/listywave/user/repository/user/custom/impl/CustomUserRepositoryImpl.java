package com.listywave.user.repository.user.custom.impl;

import static com.listywave.list.application.domain.QItem.item;
import static com.listywave.list.application.domain.QListEntity.listEntity;
import static com.listywave.user.application.domain.QUser.user;

import com.listywave.collaborator.application.domain.Collaborator;
import com.listywave.collaborator.application.dto.CollaboratorResponse;
import com.listywave.list.application.domain.CategoryType;
import com.listywave.list.application.domain.ListEntity;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.custom.CustomUserRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ListEntity> findFeedLists(List<Collaborator> collaborators, Long userId, String type, CategoryType category, Long cursorId, int size) {
        return queryFactory
                .select(listEntity)
                .from(listEntity)
                .leftJoin(listEntity.items, item)
                .where(
                        collaboEq(collaborators, type),
                        userIdEq(userId, type),
                        typeEq(type),
                        categoryEq(category),
                        listIdLt(cursorId)
                )
                .distinct()
                .limit(size + 1)
                .orderBy(listEntity.updatedDate.desc())
                .fetch();
    }

    private BooleanExpression collaboEq(List<Collaborator> collaborators, String type) {
        if (type.equals("collabo") && collaborators != null) {
            return lists.id.in(
                    collaborators.stream()
                            .map(collaborator -> collaborator.getList().getId())
                            .toList()
            );
        }
        return null;
    }

    private BooleanExpression listIdLt(Long cursorId) {
        return cursorId == null ? null : listEntity.id.lt(cursorId);
    }

    private BooleanExpression categoryEq(CategoryType categoryCode) {
        if ("0".equals(categoryCode.getCodeValue())) {
            return null;
        }
        return listEntity.category.eq(categoryCode);
    }

    private BooleanExpression typeEq(String type) {
        if (type.equals("my")) {
            return listEntity.hasCollaboration.eq(false);
        }
        return listEntity.hasCollaboration.eq(true);
    }

    private BooleanExpression userIdEq(Long userId, String type) {
        if (type.equals("my")) {
            return userId == null ? null : listEntity.user.id.eq(userId);
        }
        return null;
    }

    @Override
    public List<User> getRecommendUsers() {
        return queryFactory
                .select(user)
                .from(listEntity)
                .rightJoin(listEntity.user, user)
                .groupBy(user)
                .orderBy(listEntity.updatedDate.max().desc())
                .limit(10)
                .fetch();
    }

    @Override
    public Long getCollaboratorCount(String search, User me) {
        if (search.isEmpty()) {
            return 0L;
        }
        return queryFactory
                .select(user.count())
                .from(user)
                .where(
                        user.id.ne(me.getId()),
                        user.nickname.value.contains(search)
                )
                .fetchOne();
    }

    @Override
    public Slice<CollaboratorResponse> getCollaborators(String search, Pageable pageable, User me) {
        if (search.isEmpty()) {
            return new SliceImpl<>(List.of(), pageable, false);
        }
        List<CollaboratorResponse> collaborators = queryFactory
                .select(Projections.fields(CollaboratorResponse.class,
                        user.id,
                        user.nickname.value.as("nickname"),
                        user.profileImageUrl.value.as("profileImageUrl")
                ))
                .from(user)
                .where(
                        user.id.ne(me.getId()),
                        user.nickname.value.contains(search)
                )
                .orderBy(
                        Expressions.stringTemplate("LOCATE({0}, {1})", search, user.nickname.value).asc(),
                        user.nickname.value.asc()
                )
                .limit(pageable.getPageSize() + 1)
                .offset(pageable.getOffset())
                .fetch();
        return checkEndPage(pageable, collaborators);
    }

    private Slice<CollaboratorResponse> checkEndPage(Pageable pageable, List<CollaboratorResponse> results) {
        boolean hasNext = false;
        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }
        return new SliceImpl<>(results, pageable, hasNext);
    }
}
