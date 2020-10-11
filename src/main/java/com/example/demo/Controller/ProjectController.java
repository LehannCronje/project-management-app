package com.example.demo.Controller;

import static org.springframework.http.ResponseEntity.ok;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import com.example.demo.Domain.TaskPOJO;
import com.example.demo.Service.FileService;
import com.example.demo.Service.ProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@RequestMapping("/project")
@RestController
public class ProjectController {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private FileService fileService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/all")
	public ResponseEntity all(@AuthenticationPrincipal UserDetails userDetails) {

		Set s = projectService.getAllProjects(userDetails.getUsername());

		return ok(s);
	}

	@PostMapping("/create")
	public void Create(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserDetails userDetails,
			HttpServletResponse response) {

		// fileService.uploadFile(file, userDetails.getUsername());
		// projectService.extractProject(file.getOriginalFilename(),
		// userDetails.getUsername());

		if (fileService.uploadFile(file, userDetails.getUsername()) == 1) {

			projectService.extractProject(file.getOriginalFilename(), userDetails.getUsername());

		} else {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			System.out.println("File already exists");

		}

	}

	@PostMapping("/update")
	public void updateProject(@RequestParam("file") MultipartFile file, @RequestParam("puid") Long puid,
			@AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response) {
		// fileService.uploadFile(file, userDetails.getUsername());
		if (fileService.uploadFile(file, userDetails.getUsername()) == 1) {
			projectService.updateProject(file.getOriginalFilename(), puid, userDetails.getUsername());

		} else {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			System.out.println("File already exists");

		}
	}

	@PostMapping("/delete")
	public ResponseEntity<String> delete(@RequestParam("uid") Long uid) {

		return projectService.deleteProject(uid);

	}

	@GetMapping("/resources/{id}")
	public Set<Map<String, String>> getResources(@PathVariable("id") Long uid) throws ParseException {

		return projectService.getAllResources(uid);

	}

	@PostMapping("/multiple-resources")
	public Set<Map<String, String>> getMultipleResources(@RequestBody List<Long> projectIds) {

		return projectService.getAllResources(projectIds);

	}

	@GetMapping("/tasks/{id}")
	public List<TaskPOJO> getTasks(@PathVariable("id") Long uid) throws ParseException {

		return projectService.getAllTasks(uid, "Weeks", 2);

	}

	@PostMapping("/tasks/filter/{projectId}")
	public List<TaskPOJO> getTasksWithFilter(@PathVariable("projectId") Long uid, @RequestParam("filterType") String filterType, @RequestParam("timeValue") int timeValue) throws ParseException {

		return projectService.getAllTasks(uid, filterType, timeValue);

	}

	@GetMapping("/lock/{projectId}")
	public void lockProject(@PathVariable("projectId") Long projectId) {
		projectService.lockProject(projectId);
	}

	@GetMapping("/unlock/{projectId}")
	public void unlockProject(@PathVariable("projectId") Long projectId) {
		projectService.unlockProject(projectId);
	}
}