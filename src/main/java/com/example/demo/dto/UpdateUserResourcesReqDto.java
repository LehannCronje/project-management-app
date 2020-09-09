package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateUserResourcesReqDto {

    private Long userId;

    private List<Long> removedResources;

    private List<Long> addedResources;

}
