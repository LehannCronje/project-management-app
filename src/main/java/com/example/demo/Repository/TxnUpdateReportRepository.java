package com.example.demo.Repository;

import java.util.Optional;

import com.example.demo.Entity.TxnUpdateReport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TxnUpdateReportRepository extends JpaRepository<TxnUpdateReport, Long> {

    Optional<TxnUpdateReport> findByTaskID(Long taskId);
}