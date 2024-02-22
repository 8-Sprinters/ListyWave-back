package com.listywave.collaborator.application.service;

import com.listywave.collaborator.application.dto.CollaboratorResponse;
import com.listywave.collaborator.application.dto.CollaboratorSearchResponse;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CollaboratorService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public CollaboratorSearchResponse getCollaborators(Long loginUserId, String search, Pageable pageable) {
        User user = userRepository.getById(loginUserId);

        Long count = userRepository.getCollaboratorCount(search, user);
        Slice<CollaboratorResponse> collaborators = userRepository.getCollaborators(search, pageable, user);
        return CollaboratorSearchResponse.of(collaborators.getContent(), count, collaborators.hasNext());
    }
}
