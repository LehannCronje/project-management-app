package com.example.demo.Controller;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.ok;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.demo.Domain.UpdateProjectsReqDTO;
import com.example.demo.Domain.UserAccountPojo;
import com.example.demo.Service.ProjectService;
import com.example.demo.Service.UserService;
import com.example.demo.dto.UserReqDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	ProjectService projectService;

	@GetMapping("/create")
	public void createUsers() {

		userService.createUsers();
	}

	@PostMapping("/create-account")
	public void createUsersAccount(@AuthenticationPrincipal UserDetails userDetails,
			@RequestBody UserAccountPojo userAccountPojo) {

		userService.createUsersAccount(userDetails.getUsername(), userAccountPojo.getUsername(),
				userAccountPojo.getPassword(), userAccountPojo.getRole(), userAccountPojo.getProjects());
	}

	@GetMapping("/accounts")
	public List<UserAccountPojo> getAccounts(@AuthenticationPrincipal UserDetails userDetails) {

		return userService.getUserAccounts(userDetails.getUsername());
	}

	@GetMapping("/fake")
	public ResponseEntity fakeUser() {

		Map<Object, Object> model = new HashMap<>();
		model.put("username", "user");
		model.put("roles", "user");
		return ok(model);
	}

	@SuppressWarnings("rawtypes")
	@GetMapping("/me")
	public ResponseEntity currentUser(@AuthenticationPrincipal UserDetails userDetails) {
		Map<Object, Object> model = new HashMap<>();
		try {
			model.put("username", userDetails.getUsername());
			model.put("roles", userDetails.getAuthorities().stream().map(a -> ((GrantedAuthority) a).getAuthority())
					.collect(toList()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ok(model);
	}

	@PostMapping("/changepassword")
	public ResponseEntity changePassword(@RequestBody UserReqDTO userReqDTO) {

		userService.changeUserPassword(userReqDTO.getUsername(), userReqDTO.getPassword());

		return ok("Success");
	}

	@PostMapping("/deactivateuser")
	public ResponseEntity deactivateUser(@RequestBody UserReqDTO userReqDTO) {
		userService.deactivateUser(userReqDTO.getUsername());

		return ok("sucess");
	}

	@PostMapping("/activateuser")
	public ResponseEntity activateUser(@RequestBody UserReqDTO userReqDTO) {
		userService.activateUser(userReqDTO.getUsername());

		return ok("sucess");
	}

	@GetMapping("download/glb")
	public ResponseEntity test3() {
		return ok("ok");
	}

	@PostMapping("updateProjects")
	public ResponseEntity addProjects(@RequestBody UpdateProjectsReqDTO updateProjectsReqDTO) {

		projectService.addUserProjects(updateProjectsReqDTO.getAddedProjects(), updateProjectsReqDTO.getUserId());
		projectService.removeUserProjects(updateProjectsReqDTO.getRemovedProjects(), updateProjectsReqDTO.getUserId());
		return ok("ok");
	}

}