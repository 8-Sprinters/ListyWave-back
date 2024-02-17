package com.listywave.collaborator.application.domain;

import static com.listywave.common.exception.ErrorCode.INVALID_COUNT;

import com.listywave.common.exception.CustomException;
import java.util.ArrayList;
import java.util.List;

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

    public List<Collaborator> getCollaborators() {
        return new ArrayList<>(collaborators);
    }
}
