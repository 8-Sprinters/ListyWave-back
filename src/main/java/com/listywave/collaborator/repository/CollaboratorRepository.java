package com.listywave.collaborator.repository;

import com.listywave.collaborator.application.domain.Collaborator;
import com.listywave.list.application.domain.Lists;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {

    List<Collaborator> findAllByListId(Long listId);

    void deleteAllByList(Lists lists);

    @Query("select c from Collaborator c join fetch c.list l where c.user.id =:userId")
    List<Collaborator> findAllByUserId(Long userId);
}
