package com.listywave.collaborator.domain.repository;

import com.listywave.collaborator.domain.Collaborator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {
}
