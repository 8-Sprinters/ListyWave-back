package com.listywave.user.repository.user.elastic;

import static com.listywave.common.exception.ErrorCode.ELASTICSEARCH_REQUEST_FAILED;
import static com.listywave.common.util.PaginationUtils.checkEndPage;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.listywave.common.exception.CustomException;
import com.listywave.user.application.domain.UserDocument;
import com.listywave.user.application.dto.search.UserElasticSearchResponse;
import com.listywave.user.application.dto.search.UserElasticSearchResult;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserElasticRepository {

    private static final float NGRAM_BOOST = 2.0f;
    private static final float JASO_BOOST = 1.5f;

    private final ElasticsearchClient esClient;

    public UserElasticSearchResponse findAll(Long loginUserId, String keyword, Pageable pageable) {
        Query excludeUserIdQuery = buildTermQuery("_id", loginUserId);
        Query isNotDeletedQuery = buildTermQuery("is_delete", false);
        Query nicknameNgramQuery = buildMatchQuery("nickname.ngram", keyword, NGRAM_BOOST);
        Query nicknameJasoQuery = buildMatchQuery("nickname.jaso", keyword, JASO_BOOST);

        SearchRequest request =
                buildSearchRequest(pageable, excludeUserIdQuery, isNotDeletedQuery, nicknameNgramQuery, nicknameJasoQuery);
        SearchResponse<UserDocument> searchResponse = requestSearch(request);
        List<UserElasticSearchResult> users = convertSearchResults(searchResponse);

        Long totalCount = searchResponse.hits().total().value();
        Slice<UserElasticSearchResult> result = checkEndPage(pageable, users);

        return UserElasticSearchResponse.of(result.getContent(), totalCount, result.hasNext());
    }

    private Query buildTermQuery(String field, Object value) {
        if (value instanceof Boolean) {
            return TermQuery.of(term -> term.field(field).value((Boolean) value))._toQuery();
        }
        return TermQuery.of(term -> term.field(field).value((Long) value))._toQuery();
    }

    private Query buildMatchQuery(String field, String keyword, float boost) {
        return MatchQuery.of(match -> match.field(field).query(keyword).boost(boost))._toQuery();
    }

    private SearchRequest buildSearchRequest(
            Pageable pageable,
            Query excludeUserIdQuery,
            Query isNotDeletedQuery,
            Query nicknameNgramQuery,
            Query nicknameJasoQuery
    ) {
        int offset = pageable.getPageNumber() * pageable.getPageSize();

        return SearchRequest.of(sr -> sr
                .index("users_sync_idx")
                .query(q -> q.bool(
                                b -> b.mustNot(excludeUserIdQuery)
                                        .should(s -> s.bool(bb -> bb.must(nicknameNgramQuery, isNotDeletedQuery)))
                                        .should(s -> s.bool(bb -> bb.must(nicknameJasoQuery, isNotDeletedQuery)))
                        )
                )
                .from(offset)
                .size(pageable.getPageSize() + 1)
        );
    }

    private SearchResponse<UserDocument> requestSearch(SearchRequest request) {
        try {
            return esClient.search(request, UserDocument.class);
        } catch (ElasticsearchException | IOException e) {
            throw new CustomException(ELASTICSEARCH_REQUEST_FAILED);
        }
    }

    private List<UserElasticSearchResult> convertSearchResults(SearchResponse<UserDocument> searchResponse) {
        return searchResponse.hits().hits().stream()
                .map(hit -> {
                    UserDocument user = hit.source();
                    user.setId(Long.valueOf(hit.id()));
                    return UserElasticSearchResult.of(user);
                })
                .collect(Collectors.toList());
    }
}
