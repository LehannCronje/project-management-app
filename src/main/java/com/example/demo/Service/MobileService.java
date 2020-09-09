package com.example.demo.Service;

import java.util.List;

import com.example.demo.Entity.PResource;
import com.example.demo.Entity.Project;

public interface MobileService {
    
    List<Project> getAccountProjects(String username);

    List<PResource> getAccountResources(String username, Long projectId);

}