package com.example.demo.Controller;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.ok;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.demo.Domain.UpdateProjectsReqDTO;
import com.example.demo.Domain.UserAccountPojo;
import com.example.demo.Entity.PResource;
import com.example.demo.Entity.User;
import com.example.demo.Entity.UserAcount;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.MobileService;
import com.example.demo.Service.ProjectService;
import com.example.demo.Service.ResourceService;
import com.example.demo.Service.UserService;
import com.example.demo.dto.UpdateUserResourcesReqDto;
import com.example.demo.dto.UserReqDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	ProjectService projectService;

	@Autowired
	UserRepository userRepo;

	@Autowired
	MobileService mobileService;

	@Autowired
	ResourceService resourceService;

	@GetMapping("/create")
	public void createUsers() {

		userService.createUsers();
	}

	@PostMapping("/create-account")
	public void createUsersAccount(@AuthenticationPrincipal UserDetails userDetails,
			@RequestBody UserAccountPojo userAccountPojo) {

		userService.createUsersAccount(userDetails.getUsername(), userAccountPojo);
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

	@PostMapping("/updateResources")
	public ResponseEntity addResources(@RequestBody UpdateUserResourcesReqDto updateUserResourcesReqDto){
		resourceService.addUserAccountResources(updateUserResourcesReqDto.getAddedResources(), updateUserResourcesReqDto.getUserId());
		resourceService.removeUserAccountResources(updateUserResourcesReqDto.getRemovedResources(), updateUserResourcesReqDto.getUserId());
		return ok("ok");
	}

	@GetMapping("/project/resources/{id}")
	public List<Map<String,String>> getResources(@PathVariable("id") Long uid, HttpServletResponse response,
												 @AuthenticationPrincipal UserDetails userDetails) {

		User user = userRepo.findByUsername(userDetails.getUsername()).get();
		List<Map<String,String>> resourceList = new ArrayList<Map<String,String>>();
		Map<String,String> resourceDetailsMap = new HashMap<String,String>();

		for(PResource resource : mobileService.getAccountResources(userDetails.getUsername(), uid)){
			resourceDetailsMap = new HashMap<String,String>();
			resourceDetailsMap.put("id", "" + resource.getId());
			resourceDetailsMap.put("name", resource.getName());
			resourceList.add(resourceDetailsMap);
		}
		return resourceList;

	}

	@GetMapping("account/project/resources/{uid}")
	public List<Map<String,String>> getResourcesByUserId(@PathVariable("uid") Long userId){
		User user = userRepo.findById(userId).get();
		List<Map<String,String>> resourceList = new ArrayList<Map<String,String>>();
		Map<String,String> resourceDetailsMap = new HashMap<String,String>();
		for(PResource resource : user.getUserAcount().getResources()){
			resourceDetailsMap = new HashMap<String,String>();
			resourceDetailsMap.put("id", "" + resource.getId());
			resourceDetailsMap.put("name", resource.getName());
			resourceDetailsMap.put("projectName", resource.getProject().getName());
			resourceList.add(resourceDetailsMap);
		}
		return resourceList;

	}
}