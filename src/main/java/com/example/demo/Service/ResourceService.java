package com.example.demo.Service;

import com.example.demo.Entity.PResource;

import java.util.List;

public interface ResourceService {

    public PResource findResourceById(Long resourceId);

    void addUserAccountResources(List<Long> resourceIds, Long userId);

    void removeUserAccountResources(List<Long> resourceIds, Long userId);

}