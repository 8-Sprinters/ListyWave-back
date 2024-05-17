package com.listywave.collaborator.application.service;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;

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

    @Transactional(readOnly = true, propagation = REQUIRED)
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
    public List<Collaborator> saveAll(Collaborators collaborators) {
        return collaboratorRepository.saveAll(collaborators.collaborators());
    }

    @Transactional(propagation = REQUIRED)
    public void updateCollaborators(Collaborators beforeCollaborators, Collaborators newCollaborators) {
        Collaborators removedCollaborators = beforeCollaborators.filterRemovedCollaborators(newCollaborators);
        collaboratorRepository.deleteAllInBatch(removedCollaborators.collaborators());

        Collaborators addedCollaborators = beforeCollaborators.filterAddedCollaborators(newCollaborators);
        collaboratorRepository.saveAll(addedCollaborators.collaborators());
    }

    @Transactional(propagation = REQUIRED)
    public void deleteAllByList(ListEntity list) {
        collaboratorRepository.deleteAllByList(list);
    }

    @Transactional(propagation = REQUIRED)
    public void deleteAllByListIn(List<ListEntity> lists) {
        collaboratorRepository.deleteAllByListIn(lists);
    }
}
