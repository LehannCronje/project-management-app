package com.example.demo.Domain;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Data;

@Data
public class TaskPOJO {

    private Long id;

    private String name;

    private List<Map<String, String>> tasks;

}