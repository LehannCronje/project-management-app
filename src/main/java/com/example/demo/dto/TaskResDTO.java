package com.example.demo.dto;

import lombok.Data;

@Data
public class TaskResDTO implements Comparable<TaskResDTO>{
    
    private String name;

    private String id;

    private String duration;

    private String percentageComplete;

    private String remainingDuration;

    private String start;

    private String finish;

    private String notes;

    private String isUpdated;

    private String isStarted;

    private UpdatedTaskResDTO updatedTaskResDTO;

    @Override
    public int compareTo(TaskResDTO o) {
        
        return this.getId().compareTo(o.getId());
    }

}