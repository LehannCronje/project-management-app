package com.example.demo.Domain;

import java.util.List;

import lombok.Data;

@Data
public class UpdateProjectsReqDTO {

    private List<Long> addedProjects;

    private List<Long> removedProjects;

    private Long userId;

}