package com.listywave.reaction.repository;

import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.reaction.application.domain.Reaction;
import com.listywave.reaction.application.domain.UserReaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserReactionRepository extends JpaRepository<UserReaction, Long> {

    boolean existsByUserIdAndListAndReaction(Long userId, ListEntity list, Reaction reaction);

    void deleteByUserIdAndListAndReaction(Long userId, ListEntity list, Reaction reaction);

    List<UserReaction> findByUserIdAndList(Long loginUserId, ListEntity list);
}
