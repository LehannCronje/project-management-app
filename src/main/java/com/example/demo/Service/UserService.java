package com.example.demo.Service;

import java.util.List;

import com.example.demo.Domain.UserAccountPojo;

public interface UserService {

	public void createUsers();

	public void createNewUser(String username, String password, String role);

	public void createUsersAccount(String username, UserAccountPojo userAccountPojo);

	public List<UserAccountPojo> getUserAccounts(String username);

	public void changeUserPassword(String username, String password);

	public void deactivateUser(String username);

	public void activateUser(String username);
}
