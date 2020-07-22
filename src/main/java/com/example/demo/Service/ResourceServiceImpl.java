package com.example.demo.Service;

import com.example.demo.Entity.PResource;
import com.example.demo.Repository.ResourceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceRepository resourcesRepo;

    @Override
    public PResource findResourceById(Long resourceId) {

        return resourcesRepo.findById(resourceId).get();
    }

}