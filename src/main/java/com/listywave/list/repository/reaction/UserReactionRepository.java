package com.listywave.list.repository.reaction;

import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.application.domain.reaction.Reaction;
import com.listywave.list.application.domain.reaction.UserReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserReactionRepository extends JpaRepository<UserReaction, Long> {

    boolean existsByUserIdAndListAndReaction(Long userId, ListEntity list, Reaction reaction);

    void deleteByUserIdAndListAndReaction(Long userId, ListEntity list, Reaction reaction);

    List<UserReaction> findByUserIdAndList(Long loginUserId, ListEntity list);
}
