package com.listywave.list.repository.reaction;

import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.application.domain.reaction.Reaction;
import com.listywave.list.application.domain.reaction.ReactionStats;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReactionStatsRepository extends JpaRepository<ReactionStats, Long> {

    Optional<ReactionStats> findByListAndReaction(ListEntity list, Reaction reaction);

    List<ReactionStats> findByList(ListEntity list);
}
