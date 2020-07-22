package com.example.demo.Domain;

import java.util.List;

import lombok.Data;

@Data
public class TaskListReportPOJO {

    private String resourceName;
    private String projectName;
    private String date;
    private String pdfName;

    private List<TaskPOJO> tasks;
}