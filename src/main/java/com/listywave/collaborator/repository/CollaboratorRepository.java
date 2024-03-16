package com.listywave.collaborator.repository;

import com.listywave.collaborator.application.domain.Collaborator;
import com.listywave.list.application.domain.list.ListEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {

    @Query("""
            select c from Collaborator  c
            join fetch c.list l
            join fetch c.user u
            where c.list = :list and c.user.isDelete = false
            """)
    List<Collaborator> findAllByList(ListEntity list);

    void deleteAllByList(ListEntity list);

    @Modifying
    @Query("delete from Collaborator c where c.list in :lists")
    void deleteAllByListIn(@Param("lists") List<ListEntity> lists);
}
