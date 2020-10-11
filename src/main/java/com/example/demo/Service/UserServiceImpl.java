package com.example.demo.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.demo.Domain.UserAccountPojo;
import com.example.demo.Entity.PResource;
import com.example.demo.Entity.Project;
import com.example.demo.Entity.User;
import com.example.demo.Entity.UserAcount;
import com.example.demo.Repository.ProjectRepository;
import com.example.demo.Repository.ResourceRepository;
import com.example.demo.Repository.UserAccountRepository;
import com.example.demo.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	UserRepository users;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	UserAccountRepository userAccRepo;

	@Autowired
	ProjectRepository projectRepo;
	
	@Autowired
	ResourceRepository resourceRepo;

	@Autowired
	UserRepository userRepo;

	public void createUsers() {
		try {
			this.users.save(User.builder().username("user").password(this.passwordEncoder.encode("password"))
					.roles(Arrays.asList("ROLE_USER")).build());

			this.users.save(User.builder().username("admin").password(this.passwordEncoder.encode("password"))
					.roles(Arrays.asList("ROLE_USER", "ROLE_ADMIN")).build());
		} catch (Exception e) {
			System.out.println("Duplicate entries");
		}

	}

	@Override
	public void createNewUser(String username, String password, String role) {
		try {
			List roles = new ArrayList();
			if(role.equals("ROLE_ADMIN")){
				roles.add("ROLE_ADMIN");
				roles.add("ROLE_USER");
				this.users.save(User.builder().username(username).password(this.passwordEncoder.encode(password))
						.roles(roles).build());
			}else{
				this.users.save(User.builder().username(username).password(this.passwordEncoder.encode(password))
						.roles(Arrays.asList(role)).build());
			}

		} catch (Exception e) {
			System.out.println("Duplicate entries");
		}
	}

	public void createUsersAccount(String username, UserAccountPojo userAccountPojo) {
		try {

			User u = users.findByUsername(username).orElseGet(null);

			UserAcount uA = new UserAcount();


			Set<PResource> resourceList = new HashSet<PResource>();

			for (Long id : userAccountPojo.getResources()) {
				resourceList.add(resourceRepo.findById(id).get());
			}

			this.users.save(User.builder().username(userAccountPojo.getUsername()).password(this.passwordEncoder.encode(userAccountPojo.getPassword()))
					.roles(Arrays.asList(userAccountPojo.getRole())).build());
			User newUser = users.findByUsername(userAccountPojo.getUsername()).get();
			uA.setUser(newUser);
			uA.setResources(resourceList);
			uA.setOwner(u);

			Set<UserAcount> acountSet = new HashSet<UserAcount>();

			if (u.getUserAcounts() == null) {
				acountSet.add(uA);
				u.setUserAcounts(acountSet);
			} else {
				u.getUserAcounts().add(uA);
			}

			userAccRepo.save(uA);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public List<UserAccountPojo> getUserAccounts(String username) {

		List<UserAccountPojo> userAcc = new ArrayList<UserAccountPojo>();

		UserAccountPojo uAP = new UserAccountPojo();
		for (UserAcount uAcc : userAccRepo.findAllByOwner(users.findByUsername(username).get())) {
			uAP = new UserAccountPojo();
			uAP.setId(uAcc.getId());
			uAP.setName(uAcc.getUser().getUsername());
			uAP.setRole(uAcc.getUser().getRoles().get(0));
			uAP.setIsActive(uAcc.getUser().getIsActive());
			uAP.setProjects(uAcc.getUser().getProjects().stream().map(Project::getId).collect(Collectors.toList()));
			userAcc.add(uAP);
		}
		return userAcc;
	}

	@Override
	public void changeUserPassword(String username, String password) {

		User user = userRepo.findByUsername(username).get();

		user.setPassword(this.passwordEncoder.encode(password));

		userRepo.save(user);
	}

	@Override
	public void deactivateUser(String username) {
		User user = userRepo.findByUsername(username).get();

		user.setIsActive("DISABLED");

		userRepo.save(user);
	}

	@Override
	public void activateUser(String username) {
		User user = userRepo.findByUsername(username).get();

		user.setIsActive("ACTIVE");

		userRepo.save(user);
	}
}
