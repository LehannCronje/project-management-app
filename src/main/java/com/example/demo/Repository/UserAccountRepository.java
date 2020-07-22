package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entity.User;
import com.example.demo.Entity.UserAcount;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAcount, Long>{

	public List<UserAcount> findAllByOwner(User owner);
}
