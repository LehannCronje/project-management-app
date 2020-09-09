package com.example.demo.dto;

import lombok.Data;

@Data
public class UpdatedTaskResDTO {
 
    String id;

    String changeRemainingDuration;

    String remainingDuration;

    String finish;

    String finished;

    String notes;

    String processed;
    
    String requireMoreWork;

    String start;

    String started;
}