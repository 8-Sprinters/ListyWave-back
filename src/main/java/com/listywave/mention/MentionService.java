package com.listywave.mention;

import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MentionService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Mention> toMentions(List<Long> mentionIds) {
        List<User> mentionedUsers = userRepository.findAllById(mentionIds);
        return mentionedUsers.stream()
                .map(Mention::new)
                .toList();
    }
}
