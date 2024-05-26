package com.listywave.user.repository.user.elastic;

import static com.listywave.common.exception.ErrorCode.ELASTICSEARCH_REQUEST_FAILED;
import static com.listywave.common.util.PaginationUtils.checkEndPage;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.listywave.common.exception.CustomException;
import com.listywave.user.application.domain.UserDocument;
import com.listywave.user.application.dto.search.UserElasticSearchResult;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserElasticRepository {

    private final ElasticsearchClient esClient;

    public Map<String, Object> findAllByElasticSearch(String search, Pageable pageable, Long loginUserId) {
        Long excludeUserId = loginUserId != null ? loginUserId : -1L;

        Query excludeUserIdQuery = buildTermQuery("_id", excludeUserId);
        Query isNotDeletedQuery = buildTermQuery("is_delete", false);
        Query nicknameNgramQuery = buildMatchQuery("nickname.ngram", search, 2.0f);
        Query nicknameJasoQuery = buildMatchQuery("nickname.jaso", search, 1.5f);

        SearchRequest searchRequest =
                buildSearchRequest(
                        pageable,
                        excludeUserIdQuery,
                        isNotDeletedQuery,
                        nicknameNgramQuery,
                        nicknameJasoQuery
                );

        SearchResponse<UserDocument> searchResponse = executeSearchRequest(searchRequest);

        List<UserElasticSearchResult> results = processSearchResults(searchResponse);

        return Map.ofEntries(
                Map.entry("totalCount", searchResponse.hits().total().value()),
                Map.entry("result", checkEndPage(pageable, results))
        );
    }

    private Query buildTermQuery(String field, Object value) {
        if (value instanceof Boolean) {
            return TermQuery.of(term -> term.field(field).value((Boolean) value))._toQuery();
        }
        return TermQuery.of(term -> term.field(field).value((Long) value))._toQuery();
    }

    private Query buildMatchQuery(String field, String search, float boost) {
        return MatchQuery.of(match -> match.field(field).query(search).boost(boost))._toQuery();
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
                .query(q -> q
                        .bool(b -> b
                                .mustNot(excludeUserIdQuery)
                                .should(s -> s
                                        .bool(bb -> bb
                                                .must(nicknameNgramQuery, isNotDeletedQuery)
                                        )
                                )
                                .should(s -> s
                                        .bool(bb -> bb
                                                .must(nicknameJasoQuery, isNotDeletedQuery)
                                        )
                                )
                        )
                )
                .from(offset)
                .size(pageable.getPageSize() + 1)
        );
    }

    private SearchResponse<UserDocument> executeSearchRequest(SearchRequest searchRequest) {
        try {
            return esClient.search(searchRequest, UserDocument.class);
        } catch (Exception e) {
            throw new CustomException(ELASTICSEARCH_REQUEST_FAILED);
        }
    }

    private List<UserElasticSearchResult> processSearchResults(SearchResponse<UserDocument> searchResponse) {
        return searchResponse.hits().hits().stream()
                .map(hit -> {
                    UserDocument user = hit.source();
                    user.setId(Long.valueOf(hit.id()));
                    return UserElasticSearchResult.of(user);
                })
                .collect(Collectors.toList());
    }
}
