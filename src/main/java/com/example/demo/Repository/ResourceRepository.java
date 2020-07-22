package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.Entity.PResource;

@Repository
public interface ResourceRepository extends JpaRepository<PResource, Long> {

	
	@Modifying
	@Query("delete from PResource p where p.id in ?1")
	void deleteResourcesWithIds(List<Long> ids);
	
}