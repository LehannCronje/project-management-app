package com.example.demo.Controller;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.ok;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.demo.Domain.StefanDomain;
import com.example.demo.Domain.StefanResDTO;
import com.example.demo.Domain.TaskPOJO;
import com.example.demo.Domain.UpdateTask;
import com.example.demo.Entity.PResource;
import com.example.demo.Entity.Project;
import com.example.demo.Entity.User;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.MobileService;
import com.example.demo.Service.ProjectService;
import com.example.demo.Service.ReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mobile")
public class MobileController {

	@Autowired
	ProjectService projectService;

	@Autowired
	ReportService reportService;

	@Autowired
	MobileService mobileService;

	@Autowired
	UserRepository userRepo;

	@GetMapping("/project/all")
	public List<Map<String,String>> all(@AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response) {
		User user = userRepo.findByUsername(userDetails.getUsername()).get();
		user.setIsUpdated(false);
		userRepo.save(user);
		if (!user.isEnabled()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return new ArrayList<Map<String,String>>();
		}
		List<Map<String,String>> projectList = new ArrayList<Map<String,String>>();
		Map<String,String> projectDetailsMap = new HashMap<String,String>();
		for(Project project : mobileService.getAccountProjects(userDetails.getUsername())){
			projectDetailsMap = new HashMap<String,String>();
			projectDetailsMap.put("id", "" + project.getId());
			projectDetailsMap.put("name", "" + project.getName());
			projectDetailsMap.put("statusDate", "" + project.getStatusDate());
			projectList.add(projectDetailsMap);
		}
		return projectList;
	}

	@GetMapping("/project/resources/{id}")
	public List<Map<String,String>> getResources(@PathVariable("id") Long uid, HttpServletResponse response,
			@AuthenticationPrincipal UserDetails userDetails) {

		User user = userRepo.findByUsername(userDetails.getUsername()).get();
		if (!user.isEnabled()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return new ArrayList<Map<String,String>>();
		}
		if (projectService.isProjectLocked(uid) || user.isUpdated()) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return new ArrayList<Map<String,String>>();
		} else {
			
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

	}

	@GetMapping("/project/tasks/{id}")
	public List<TaskPOJO> getTasks(@PathVariable("id") Long uid, HttpServletResponse response,
			HttpServletRequest request, @AuthenticationPrincipal UserDetails userDetails) throws ParseException {

		User user = userRepo.findByUsername(userDetails.getUsername()).get();
		if (!user.isEnabled()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return new ArrayList<TaskPOJO>();
		}
		if (projectService.isProjectLocked(Long.valueOf(request.getHeader("projectId"))) || user.isUpdated()) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return new ArrayList<TaskPOJO>();
		} else {
			return projectService.getAllTasks(uid, "Weeks", 2);
		}
	}

	@PostMapping("/report/taskUpdate")
	public void mobileUpdate(@RequestBody UpdateTask data, HttpServletResponse response, HttpServletRequest request,
			@AuthenticationPrincipal UserDetails userDetails) throws Exception {

		User user = userRepo.findByUsername(userDetails.getUsername()).get();
		if (!user.isEnabled()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);

		}
		if (projectService.isProjectLocked(Long.valueOf(request.getHeader("projectId"))) || user.isUpdated()) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
		} else {
			reportService.updateReport(data);
		}
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

	@PostMapping("/stefan")
	public ResponseEntity stefanRoute(@RequestBody StefanDomain stefanData) {
		return ok(stefanData);
	}

	@PostMapping("/file/upload")
	public ResponseEntity stefanRoute2(@RequestBody StefanDomain multipartFile) throws IOException {

		// Ideally you shall read bytes using multipartFile.getInputStream() and store
		// it appropriately

		Path path = Paths.get("./stefan.PNG");
		Files.write(path, multipartFile.getImageData());
		StefanResDTO response = new StefanResDTO();
		response.setSetupImageUrl("http://105.224.234.6:8081/images/stefan.png");
		return ok(response);
	}

	@GetMapping("download/outglb")
	public ResponseEntity downloadFileFromLocal() throws IOException {

		File file = new File("./IronMan.obj");
		Path path = Paths.get(file.getAbsolutePath());
		ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=IronMan.obj");
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");

		return ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);

	}

}
