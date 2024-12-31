package com.listywave.reaction.application.service;

import com.listywave.alarm.application.domain.AlarmCreateEvent;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.repository.list.ListRepository;
import com.listywave.reaction.application.domain.Reaction;
import com.listywave.reaction.application.domain.ReactionStats;
import com.listywave.reaction.application.domain.UserReaction;
import com.listywave.reaction.application.dto.response.ReactionResponse;
import com.listywave.reaction.repository.ReactionStatsRepository;
import com.listywave.reaction.repository.UserReactionRepository;
import com.listywave.user.application.domain.User;
import com.listywave.user.application.service.UserService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReactionService {

    private final UserService userService;
    private final ListRepository listRepository;
    private final UserReactionRepository userReactionRepository;
    private final ReactionStatsRepository reactionStatsRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void react(Long userId, Long listId, Reaction reaction) {
        User user = userService.getById(userId);
        ListEntity list = listRepository.getById(listId);

        if (userReactionRepository.existsByUserIdAndListAndReaction(user.getId(), list, reaction)) {
            userReactionRepository.deleteByUserIdAndListAndReaction(user.getId(), list, reaction);
            updateReactionStats(list, reaction, -1);
            return;
        }
        UserReaction newReaction = UserReaction.create(user.getId(), list, reaction);
        userReactionRepository.save(newReaction);
        updateReactionStats(list, reaction, 1);

        applicationEventPublisher.publishEvent(AlarmCreateEvent.reaction(user, list));
    }

    public void updateReactionStats(ListEntity list, Reaction reaction, int changeCount) {
        ReactionStats stats = reactionStatsRepository.findByListAndReaction(list, reaction)
                .orElseGet(() -> {
                    ReactionStats newStats = new ReactionStats(list, reaction, 0);
                    reactionStatsRepository.save(newStats);
                    return newStats;
                });
        stats.updateCount(changeCount);
    }

    @Transactional(readOnly = true)
    public List<ReactionResponse> createReactionResponses(ListEntity list, User user, boolean isOwner) {
        Map<Reaction, Integer> reactionStatsMap = toReactionStatsMap(list);
        Set<Reaction> userReactionSet = toUserReactionSet(list, user);

        return Arrays.stream(Reaction.values())
                .map(reaction -> toReactionResponse(reaction, reactionStatsMap, userReactionSet, isOwner))
                .collect(Collectors.toList());
    }

    private Map<Reaction, Integer> toReactionStatsMap(ListEntity list) {
        return reactionStatsRepository.findByList(list).stream()
                .collect(Collectors.toMap(ReactionStats::getReaction, ReactionStats::getCount));
    }

    private Set<Reaction> toUserReactionSet(ListEntity list, User user) {
        if (user == null) {
            return Collections.emptySet();
        }
        return userReactionRepository.findByUserIdAndList(user.getId(), list).stream()
                .map(UserReaction::getReaction)
                .collect(Collectors.toSet());
    }

    private ReactionResponse toReactionResponse(
            Reaction reaction,
            Map<Reaction, Integer> reactionStatsMap,
            Set<Reaction> userReactionSet,
            boolean isOwner
    ) {
        int count = reactionStatsMap.getOrDefault(reaction, 0);
        boolean isReacted = userReactionSet.contains(reaction);
        return ReactionResponse.of(reaction.name(), isOwner ? count : null, isReacted);
    }
}
