package com.listywave.collaborator.application.domain;

import static com.listywave.common.exception.ErrorCode.DUPLICATE_COLLABORATOR_EXCEPTION;
import static com.listywave.common.exception.ErrorCode.INVALID_COUNT;

import com.listywave.common.exception.CustomException;
import com.listywave.user.application.domain.User;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record Collaborators(
        List<Collaborator> collaborators
) {

    private static final int MAX_SIZE = 20;

    public Collaborators(List<Collaborator> collaborators) {
        validateSize(collaborators);
        this.collaborators = new ArrayList<>(collaborators);
    }

    public static void validateSize(List<Collaborator> collaborators) {
        if (collaborators.size() > MAX_SIZE) {
            throw new CustomException(INVALID_COUNT);
        }
    }

    public Collaborators filterRemovedCollaborators(Collaborators newCollaborators) {
        Set<Collaborator> before = new HashSet<>(this.collaborators);
        newCollaborators.collaborators.forEach(before::remove);
        return new Collaborators(new ArrayList<>(before));
    }

    public Collaborators filterAddedCollaborators(Collaborators newCollaborators) {
        Set<Collaborator> after = new HashSet<>(newCollaborators.collaborators);
        this.collaborators.forEach(after::remove);
        return new Collaborators(new ArrayList<>(after));
    }

    public boolean contains(User user) {
        return collaborators.stream()
                .anyMatch(collaborator -> collaborator.getUser().equals(user));
    }

    public boolean isEmpty() {
        return this.collaborators.isEmpty();
    }

    public void add(Collaborator collaborator) {
        if (collaborators.contains(collaborator)) {
            throw new CustomException(DUPLICATE_COLLABORATOR_EXCEPTION);
        }
        collaborators.add(collaborator);
    }

    public List<Collaborator> collaborators() {
        return new ArrayList<>(collaborators);
    }
}
