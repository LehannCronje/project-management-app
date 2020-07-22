package com.example.demo.Controller;

import com.example.demo.Domain.UpdateTask;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/task")
@RestController
public class TaskController {

    @PostMapping("/mupdate")
    public void mobileUpdate(@RequestBody UpdateTask data) {

    }

}