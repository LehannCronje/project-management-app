package com.example.demo.Service;

import com.example.demo.Entity.PResource;
import com.example.demo.Entity.User;
import com.example.demo.Entity.UserAcount;
import com.example.demo.Repository.ResourceRepository;

import com.example.demo.Repository.UserAccountRepository;
import com.example.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceRepository resourcesRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    ResourceRepository resourceRepo;

    @Autowired
    UserAccountRepository userAccRepo;

    @Override
    public PResource findResourceById(Long resourceId) {

        return resourcesRepo.findById(resourceId).get();
    }

    @Override
    public void addUserAccountResources(List<Long> resourceIds, Long userId) {

        UserAcount userAcc = userRepo.findById(userId).get().getUserAcount();
        Set<PResource> userResourceList = userAcc.getResources();
        PResource dbResource;

        for(Long id : resourceIds){
            dbResource = resourceRepo.findById(id).get();
            if(!userAcc.getResources().contains(dbResource)){
                userResourceList.add(dbResource);
            }
        }

        userAcc.setResources(userResourceList);
        userAccRepo.save(userAcc);


    }

    @Override
    public void removeUserAccountResources(List<Long> resourceIds, Long userId) {
        UserAcount userAcc = userRepo.findById(userId).get().getUserAcount();
        Set<PResource> userResourceList = userAcc.getResources();
        PResource dbResource;

        for(Long id : resourceIds){
            dbResource = resourceRepo.findById(id).get();
            if(userAcc.getResources().contains(dbResource)){
                userResourceList.remove(dbResource);
            }
        }

        userAcc.setResources(userResourceList);
        userAccRepo.save(userAcc);



    }

}