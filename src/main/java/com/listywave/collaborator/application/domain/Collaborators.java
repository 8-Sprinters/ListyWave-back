package com.listywave.collaborator.application.domain;

import static com.listywave.common.exception.ErrorCode.INVALID_ACCESS;
import static com.listywave.common.exception.ErrorCode.INVALID_COUNT;

import com.listywave.common.exception.CustomException;
import com.listywave.user.application.domain.User;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Collaborators {

    private static final int MAX_SIZE = 20;

    private final List<Collaborator> collaborators;

    public Collaborators(List<Collaborator> collaborators) {
        validateSize(collaborators);
        this.collaborators = new ArrayList<>(collaborators);
    }

    public static void validateSize(List<Collaborator> collaborators) {
        if (collaborators.size() > MAX_SIZE) {
            throw new CustomException(INVALID_COUNT);
        }
    }

    public void validateListUpdateAuthority(User user) {
        if (!collaborators.isEmpty() && !contains(user)) {
            throw new CustomException(INVALID_ACCESS, "리스트 수정은 작성자 혹은 콜라보레이터만 가능합니다.");
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
                .anyMatch(collaborator -> collaborator.hasUser(user));
    }

    public void add(Collaborator collaborator) {
        collaborators.add(collaborator);
    }

    public List<Collaborator> getCollaborators() {
        return new ArrayList<>(collaborators);
    }
}
