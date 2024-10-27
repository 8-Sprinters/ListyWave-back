package com.listywave.reaction.repository;

import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.reaction.application.domain.Reaction;
import com.listywave.reaction.application.domain.ReactionStats;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReactionStatsRepository extends JpaRepository<ReactionStats, Long> {

    Optional<ReactionStats> findByListAndReaction(ListEntity list, Reaction reaction);

    List<ReactionStats> findByList(ListEntity list);
}
