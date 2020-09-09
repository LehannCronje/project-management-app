package com.example.demo.Repository;

import java.util.Optional;

import com.example.demo.Entity.RTask;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<RTask, Long>{

    @Query(value = "select t from RTask t where t.uid= ?1 AND t.resource.id= ?2")
    Optional<RTask> findByUidAndPResourceId(Long uid, Long pResourceId);

}
