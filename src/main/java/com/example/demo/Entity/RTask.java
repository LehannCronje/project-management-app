package com.example.demo.Entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@SQLDelete(sql = "UPDATE rtask SET delete_date = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "delete_date IS NULL")
@Setter
@Getter
@NoArgsConstructor
public class RTask {

    @Id
    @GeneratedValue
    private Long id;

    private Long uid;

    private String name;

    private Long parentTask;

    private String parentWBS;

    private String parentTaskName;

    private String WBS;

    private String durationComplete;

    private String remainingDuration;

    private String percentageComplete;

    private String start;

    private String Finish;

    private String notes;

    private Date deleteDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presource_id")
    private PResource resource;

}