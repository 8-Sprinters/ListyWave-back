package com.listywave.collaborator.domain.repository;

import com.listywave.collaborator.domain.Collaborator;
import com.listywave.list.application.domain.Lists;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {

    List<Collaborator> findAllByListId(Long listId);

    void deleteAllByList(Lists lists);
}
