package com.listywave.collaborator.application.domain;

import static com.listywave.common.exception.ErrorCode.DUPLICATE_USER;

import com.listywave.common.exception.CustomException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Collaborators {

    private final List<Collaborator> collaborators;

    public Collaborators(List<Collaborator> collaborators) {
        this.collaborators = new ArrayList<>(collaborators);
    }

    public static void validateDuplicateCollaboratorIds(List<Long> collaboratorIds) {
        Set<Long> uniqueIds = new HashSet<>(collaboratorIds);
        if (collaboratorIds.size() != uniqueIds.size()) {
            throw new CustomException(DUPLICATE_USER, "중복된 콜라보레이터를 선택할 수 없습니다.");
        }
    }

    public List<Collaborator> getCollaborators() {
        return new ArrayList<>(collaborators);
    }
}
