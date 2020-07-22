package com.example.demo.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.demo.Domain.TaskPOJO;
import com.example.demo.Entity.MpxFile;
import com.example.demo.Entity.PResource;
import com.example.demo.Entity.Project;
import com.example.demo.Entity.RTask;
import com.example.demo.Entity.TxnUpdateReport;
import com.example.demo.Entity.User;
import com.example.demo.Repository.FileRepository;
import com.example.demo.Repository.ProjectRepository;
import com.example.demo.Repository.ResourceRepository;
import com.example.demo.Repository.TaskRepository;
import com.example.demo.Repository.TxnUpdateReportRepository;
import com.example.demo.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceType;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.UniversalProjectReader;

@Transactional
@Service
public class ProjectServiceImpl implements ProjectService {

	@Autowired
	FileRepository fileRepo;

	@Autowired
	UserRepository userRepo;

	@Autowired
	ResourceRepository resourceRepo;

	@Autowired
	ProjectRepository projectRepo;

	@Autowired
	private TaskRepository taskRepo;

	@Autowired
	private TxnUpdateReportRepository txnReportUpdateRepo;

	@Autowired
	private FileService fileService;

	@Override
	public void extractProject(String fileName, String username) {

		ProjectReader reader = new UniversalProjectReader();

		Project p = new Project();
		MpxFile f = new MpxFile();
		User u = userRepo.findByUsername(username).get();
		for (MpxFile file : u.getFiles()) {
			if (file.getName().equals(fileName)) {
				f = file;
			}
		}
		PResource r = new PResource();
		RTask t = new RTask();

		Set<PResource> ress = new HashSet<PResource>();
		Set<Project> projects = new HashSet<Project>();
		Set<RTask> tasks = new HashSet<RTask>();

		String pattern = "dd MMMM yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		if (p.getMpxFile() == null) {
			p.setMpxFile(f);
		}

		try {
			ProjectFile project = reader.read(f.getLocation());

			Date statusDate = project.getProjectProperties().getStatusDate();

			p.setStatusDate(statusDate);

			// if (project.getProjectProperties().getProjectTitle() == null) {
			// p.setName("No Assigned Name");
			// } else {
			// p.setName(project.getProjectProperties().getProjectTitle());
			// }

			p.setName(fileName.split(".mpp", 0)[0]);

			for (Resource resource : project.getResources()) {
				r = new PResource();
				if (resource.getType().equals(ResourceType.WORK)) {

					r.setProject(p);
					if (resource.getName() == null) {
						r.setName("No Assigned Name");
					} else {
						r.setName(resource.getName());
					}
					if (p.getResources() == null) {
						p.setResources(ress);
					}
					for (ResourceAssignment assignment : resource.getTaskAssignments()) {
						t = new RTask();
						t.setResource(r);
						t.setName(assignment.getTask().getName());
						t.setParentTask(Long.valueOf(assignment.getTask().getParentTask().getID()));
						t.setParentTaskName(assignment.getTask().getParentTask().getName());
						t.setParentWBS(assignment.getTask().getParentTask().getWBS());
						t.setWBS(assignment.getTask().getWBS());
						t.setUid(Long.valueOf(assignment.getTask().getID()));
						t.setPercentageComplete("" + assignment.getTask().getPercentageComplete().intValue() + " %");
						t.setDurationComplete("" + assignment.getTask().getDuration());
						t.setStart(simpleDateFormat.format(assignment.getTask().getStart()));
						t.setFinish(simpleDateFormat.format(assignment.getTask().getFinish()));
						t.setRemainingDuration("" + assignment.getTask().getRemainingDuration());
						t.setNotes(assignment.getTask().getNotes());
						if (r.getTasks() == null) {
							r.setTasks(tasks);
						}
						r.getTasks().add(t);

					}
					p.getResources().add(r);

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (u.getProjects() == null) {
			u.setProjects(projects);
		}
		u.getProjects().add(p);

		p.getUsers().add(u);

		projectRepo.save(p);
	}

	public Set<Map<String, String>> getAllProjects(String username) {

		String pattern = "dd MMMM yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

		Set<Map<String, String>> s = new HashSet<Map<String, String>>();
		User u = userRepo.findByUsername(username).get();

		for (Project project : u.getProjects()) {
			Map<String, String> m = new HashMap<String, String>();
			m.put("id", "" + project.getId());
			m.put("name", project.getName());
			m.put("statusDate", "" + simpleDateFormat.format(project.getStatusDate()));
			m.put("isLocked", "" + project.isLocked());

			s.add(m);
		}

		return s;
	}

	public ResponseEntity<String> deleteProject(Long uid) {

		try {
			Project p = projectRepo.findById(uid).get();
			MpxFile f = p.getMpxFile();
			User u = f.getUser();
			u.removeMpxFile(f);
			p.removeMpxFile(f);
			p.removeUsers();
			u.removeProject(p);
			fileRepo.delete(f);
			fileService.deleteFile(p.getMpxFile());
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

	}

	public Set<Map<String, String>> getAllResources(Long uid) {

		Set<Map<String, String>> s = new HashSet<Map<String, String>>();

		for (PResource resource : projectRepo.findById(uid).get().getResources()) {
			Map<String, String> m = new HashMap<String, String>();
			m.put("id", "" + resource.getId());
			m.put("name", resource.getName());
			try {
				if (this.getAllTasks(resource.getId()).isEmpty()) {

				} else {
					s.add(m);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return s;
	}

	@Override
	public List<TaskPOJO> getAllTasks(Long uid) throws ParseException {

		List<TaskPOJO> s = new ArrayList<TaskPOJO>();
		TaskPOJO tp = new TaskPOJO();
		List<Map<String, String>> ss = new ArrayList<Map<String, String>>();
		Map<String, String> m = new HashMap<String, String>();

		for (RTask task : resourceRepo.findById(uid).get().getTasks()) {
			m = new HashMap<String, String>();
			ss = new ArrayList<Map<String, String>>();

			if (testDate(task.getStart()) || testDate(task.getFinish())) {
				boolean flag = false;
				for (int i = 0; i < s.size(); i++) {
					if (s.get(i).getId().equals(task.getParentTask())) {

						tp = s.get(i);
						ss = tp.getTasks();
						m.put("name", task.getName());
						m.put("id", "" + task.getUid());
						m.put("duration", task.getDurationComplete());
						m.put("percentageComplete", task.getPercentageComplete());
						m.put("remainingDuration", task.getRemainingDuration());
						m.put("start", task.getStart());
						m.put("finish", task.getFinish());
						m.put("notes", task.getNotes());
						ss.add(m);
						s.get(i).setTasks(ss);
						flag = true;

						break;
					} else {

					}
				}

				if (!flag) {
					tp = new TaskPOJO();
					m.put("name", task.getName());
					m.put("id", "" + task.getUid());
					m.put("duration", task.getDurationComplete());
					m.put("percentageComplete", task.getPercentageComplete());
					m.put("start", task.getStart());
					m.put("finish", task.getFinish());
					m.put("notes", task.getNotes());
					tp.setName(task.getParentTaskName());
					tp.setId(task.getParentTask());
					ss.add(m);
					tp.setTasks(ss);
					s.add(tp);
				}

			}
		}

		return s;
	}

	@Override
	public void updateProject(String fileName, Long puid, String username) {

		ProjectReader reader = new UniversalProjectReader();

		Project p = projectRepo.findById(puid).get();
		MpxFile f = p.getMpxFile();
		p.removeMpxFile(f);
		projectRepo.save(p);
		fileService.deleteFile(f);
		f = new MpxFile();
		User u = userRepo.findByUsername(username).get();
		for (MpxFile file : u.getFiles()) {
			if (file.getName().equals(fileName)) {
				f = file;
			}
		}
		PResource r = new PResource();
		RTask t = new RTask();

		Set<PResource> ress = new HashSet<PResource>();
		Set<RTask> tasks = new HashSet<RTask>();

		String pattern = "dd MMMM yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		// Long mpxFileId = p.getMpxFile().getId();
		// p.removeMpxFile(p.getMpxFile());
		// fileRepo.deleteById(mpxFileId);
		p.setMpxFile(f);
		p.setName(fileName.split(".mpp", 0)[0]);
		for (PResource res : p.getResources()) {
			for (RTask task : res.getTasks()) {
				taskRepo.delete(task);
			}

			resourceRepo.delete(res);
		}

		p.setResources(ress);
		try {

			ProjectFile project = reader.read(f.getLocation());

			Date statusDate = project.getProjectProperties().getStatusDate();

			p.setStatusDate(statusDate);

			for (Resource resource : project.getResources()) {
				r = new PResource();
				if (resource.getType().equals(ResourceType.WORK)) {
					r.setProject(p);

					if (resource.getName() == null) {
						r.setName("No Assigned Name");
					} else {
						r.setName(resource.getName());
					}
					if (p.getResources() == null) {

					}
					for (ResourceAssignment assignment : resource.getTaskAssignments()) {
						t = new RTask();
						t.setResource(r);
						t.setName(assignment.getTask().getName());
						t.setParentTask(Long.valueOf(assignment.getTask().getParentTask().getUniqueID()));
						t.setParentTaskName(assignment.getTask().getParentTask().getName());
						t.setParentWBS(assignment.getTask().getParentTask().getWBS());
						t.setWBS(assignment.getTask().getWBS());
						t.setUid(Long.valueOf(assignment.getTask().getID()));
						t.setPercentageComplete("" + assignment.getTask().getPercentageComplete().intValue() + " %");
						t.setDurationComplete("" + assignment.getTask().getDuration());
						t.setStart(simpleDateFormat.format(assignment.getTask().getStart()));
						t.setFinish(simpleDateFormat.format(assignment.getTask().getFinish()));
						t.setRemainingDuration("" + assignment.getTask().getRemainingDuration());
						t.setNotes(assignment.getTask().getNotes());
						if (r.getTasks() == null) {
							r.setTasks(tasks);
						}
						r.getTasks().add(t);
					}
					p.getResources().add(r);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		projectRepo.save(p);

	}

	public void lockProject(Long projectId) {
		Project project = projectRepo.findById(projectId).get();
		project.setLocked(true);
		projectRepo.save(project);
		for (TxnUpdateReport txnUpdateReport : this.findTxnUpdateReportByProcessed(0, projectId)) {
			txnUpdateReport.setProcessed(1);
			txnReportUpdateRepo.save(txnUpdateReport);
		}
	}

	public void unlockProject(Long projectId) {
		Project project = projectRepo.findById(projectId).get();
		project.setLocked(false);
		projectRepo.save(project);
		for (TxnUpdateReport txnUpdateReport : this.findTxnUpdateReportByProcessed(2, projectId)) {
			txnUpdateReport.setProcessed(3);
			txnReportUpdateRepo.save(txnUpdateReport);
		}
	}

	public boolean testDate(String stringDate) throws ParseException {
		Date date = new SimpleDateFormat("dd MMMM yyyy").parse(stringDate);
		Date currentDate = new Date();
		Date twoWeeks = getDateTwoWeeks(currentDate);
		if (date.after(currentDate) && date.before(twoWeeks)) {
			return true;
		} else {
			return false;
		}

	}

	public boolean testStart(String stringDate) throws ParseException {
		// DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new SimpleDateFormat("dd MMMM yyyy").parse(stringDate);
		Date currentDate = new Date();

		if (date.before(currentDate)) {
			return true;
		} else {
			return false;
		}

	}

	public Date getDateTwoWeeks(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, 14); // 2 weeks
		return calendar.getTime();
	}

	public Project findProjectById(Long projectId) {
		return projectRepo.findById(projectId).get();
	}

	@Override
	public Set<TxnUpdateReport> findTxnUpdateReportByProcessed(int value, Long projectId) {
		Project project = projectRepo.findById(projectId).get();
		Set<TxnUpdateReport> txnList = new HashSet<TxnUpdateReport>();

		for (TxnUpdateReport txnUpdateReport : project.getTxnUpdateReports()) {
			if (txnUpdateReport.getProcessed() == value) {
				txnList.add(txnUpdateReport);
			}
		}
		return txnList;
	}

	@Override
	public boolean isProjectLocked(Long projectId) {

		try {
			return projectRepo.findById(projectId).get().isLocked();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void addUserProjects(List<Long> projectList, Long userId) {

		User user = userRepo.findById(userId).get();
		Project project = new Project();
		Set<Project> newUserProjectSet = user.getProjects();
		for (Long projectId : projectList) {
			project = projectRepo.findById(projectId).get();
			if (!user.getProjects().contains(project)) {
				newUserProjectSet.add(project);
			}
		}
		user.setProjects(newUserProjectSet);
		user.setIsUpdated(true);
		userRepo.save(user);

	}

	@Override
	public void removeUserProjects(List<Long> projectList, Long userId) {
		User user = userRepo.findById(userId).get();
		Project project = new Project();
		Set<Project> newUserProjectSet = user.getProjects();
		for (Long projectId : projectList) {
			project = projectRepo.findById(projectId).get();
			if (user.getProjects().contains(project)) {
				newUserProjectSet.remove(project);
			}
		}
		user.setProjects(newUserProjectSet);
		user.setIsUpdated(true);
		userRepo.save(user);

	}
}