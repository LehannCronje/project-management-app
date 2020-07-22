package com.example.demo.Domain;

import java.util.List;

import lombok.Data;

@Data
public class UserAccountPojo {

	private String username;

	private String password;

	private Long id;

	private String name;

	private String Role;

	private List<Long> projects;

	private String isActive;

}
