package com.listywave.collection.repository.custom.impl;

import static com.listywave.collection.application.domain.QCollect.collect;
import static com.listywave.collection.application.domain.QFolder.folder;
import static com.querydsl.jpa.JPAExpressions.select;

import com.listywave.collection.application.dto.FolderResponse;
import com.listywave.collection.repository.custom.CustomFolderRepository;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomFolderRepositoryImpl implements CustomFolderRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<FolderResponse> findByFolders(Long loginUserId) {
        NumberPath<Long> listCountAlias = Expressions.numberPath(Long.class, "listCount");
        return queryFactory
                .select(Projections.constructor(FolderResponse.class,
                                folder.id.as("folderId"),
                                folder.name.value.as("folderName"),
                                ExpressionUtils.as(
                                        select(collect.count())
                                                .from(collect)
                                                .where(collect.folder.id.eq(folder.id)), listCountAlias)
                        )
                )
                .from(folder)
                .where(folder.userId.eq(loginUserId))
                .orderBy(folder.updatedDate.desc())
                .fetch();
    }
}
