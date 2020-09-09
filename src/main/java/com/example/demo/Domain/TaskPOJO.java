package com.example.demo.Domain;

import java.util.List;

import com.example.demo.dto.TaskResDTO;

import lombok.Data;

@Data
public class TaskPOJO implements Comparable<TaskPOJO>{

    private Long id;

    private String name;

    private List<TaskResDTO> tasks;

    @Override
    public int compareTo(TaskPOJO o) {
        return this.getId().compareTo(o.getId());
    }

    
}