package com.listywave.collaborator.application.service;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

import com.listywave.collaborator.application.domain.Collaborator;
import com.listywave.collaborator.application.domain.Collaborators;
import com.listywave.collaborator.repository.CollaboratorRepository;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.user.repository.user.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CollaboratorService {

    private final UserRepository userRepository;
    private final CollaboratorRepository collaboratorRepository;

    @Transactional(readOnly = true, propagation = REQUIRES_NEW)
    public Collaborators createCollaborators(List<Long> userIds, ListEntity list) {
        return new Collaborators(userIds.stream()
                .map(userRepository::getById)
                .map(user -> Collaborator.init(user, list))
                .toList());
    }

    @Transactional(readOnly = true, propagation = REQUIRED)
    public Collaborators findAllByList(ListEntity list) {
        return new Collaborators(collaboratorRepository.findAllByList(list));
    }

    @Transactional(propagation = REQUIRED)
    public void saveAll(Collaborators collaborators) {
        collaboratorRepository.saveAll(collaborators.collaborators());
    }
}
