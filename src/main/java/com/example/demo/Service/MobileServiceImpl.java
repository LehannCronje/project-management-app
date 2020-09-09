package com.example.demo.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.example.demo.Entity.PResource;
import com.example.demo.Entity.Project;
import com.example.demo.Entity.User;
import com.example.demo.Entity.UserAcount;
import com.example.demo.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MobileServiceImpl implements MobileService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProjectService projectService;

    @Override
    public List<Project> getAccountProjects(String username) {
        User user = userRepo.findByUsername(username).get();
        UserAcount userAccount = user.getUserAcount();
        if(userAccount.getResources().isEmpty()){
            return user.getProjects().stream().collect(Collectors.toList());
        }else{
            return userAccount.getResources().stream().map(PResource::getProject).distinct().collect(Collectors.toList());
        }
    }

    @Override
    public List<PResource> getAccountResources(String username, Long projectId) {
        User user = userRepo.findByUsername(username).get();
        UserAcount userAccount = user.getUserAcount();
        if(userAccount.getResources().isEmpty()){
            return user.getProjects().stream().flatMap(project -> project.getResources().stream()).collect(Collectors.toList());
        }
        return userAccount.getResources().stream().collect(Collectors.toList());
    }
    
}