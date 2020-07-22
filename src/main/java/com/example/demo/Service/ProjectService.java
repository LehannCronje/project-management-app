package com.example.demo.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.demo.Domain.TaskPOJO;
import com.example.demo.Entity.Project;
import com.example.demo.Entity.TxnUpdateReport;

import org.springframework.http.ResponseEntity;

public interface ProjectService {

    public void extractProject(String fileName, String username);

    public ResponseEntity<String> deleteProject(Long uid);

    public Set<Map<String, String>> getAllProjects(String username);

    public Set<Map<String, String>> getAllResources(Long uid);

    public List<TaskPOJO> getAllTasks(Long uid) throws ParseException;

    public void updateProject(String fileName, Long puid, String username);

    public void lockProject(Long projectId);

    public void unlockProject(Long projectId);

    public Project findProjectById(Long projectId);

    public Set<TxnUpdateReport> findTxnUpdateReportByProcessed(int value, Long projectId);

    public boolean isProjectLocked(Long projectId);

    public void addUserProjects(List<Long> projectList, Long userId);

    public void removeUserProjects(List<Long> projectList, Long userId);

}