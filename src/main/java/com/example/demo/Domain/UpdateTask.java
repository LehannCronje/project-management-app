package com.example.demo.Domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTask {

    private String pdfName;

    private String projectName;

    private String date;

    private String resourceName;

    // mobile data

    private Long resourceID;

    private Long taskID;

    private String taskName;

    private boolean started;

    private String start;

    private boolean finished;

    private String finish;

    private boolean requireMoreWork;

    private boolean changeRemainingDuration;

    private String remainingDuration;

    private String updateAsAt;

    private String notes;
}