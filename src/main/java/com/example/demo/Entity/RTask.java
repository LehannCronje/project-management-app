package com.example.demo.Entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Entity
@SQLDelete(sql = "UPDATE rtask SET delete_date = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "delete_date IS NULL")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    private boolean isStarted;

    private Boolean isUpdated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presource_id")
    private PResource resource;

    public void setIsUpdated(boolean val){
        this.isUpdated = val;
    }

    public boolean getIsUpdated(){
        return this.isUpdated;
    }

    public void setIsStarted(boolean val){
        this.isStarted = val;
    }

    public boolean getIsStarted(){
        return this.isStarted;
    }

}