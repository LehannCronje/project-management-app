package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entity.MpxFile;

@Repository
public interface FileRepository extends JpaRepository<MpxFile, Long> {
    MpxFile findByName(String name);
}