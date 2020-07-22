package com.example.demo.Entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Where;
import org.springframework.lang.Nullable;

import lombok.Data;

@Entity
@Data
// @Where(clause = "is_processed <> 1")
public class TxnUpdateReport {

    @Id
    @GeneratedValue
    private Long id;

    private Long taskID;

    private String taskName;

    private boolean started;

    private String start;

    private boolean finished;

    private String finish;

    private boolean requireMoreWork;

    private boolean changeRemainingDuration;

    private String remainingDuration;

    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;

    private int processed;

}