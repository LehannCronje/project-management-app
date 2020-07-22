package com.example.demo.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import com.example.demo.Domain.TaskListReportPOJO;
import com.example.demo.Domain.UpdateTask;
import com.example.demo.Domain.UpdateTaskReport;
import com.example.demo.Entity.PResource;
import com.example.demo.Entity.Project;
import com.example.demo.Entity.Report;
import com.example.demo.Entity.TxnUpdateReport;
import com.example.demo.Repository.ProjectRepository;
import com.example.demo.Repository.ReportRepository;
import com.example.demo.Repository.ResourceRepository;
import com.example.demo.Repository.TxnUpdateReportRepository;
import com.example.demo.dto.UpdateReportResDTO;
import com.example.demo.util.PdfGenerartor;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    ProjectService projectService;

    @Autowired
    PdfGenerartor pdfGenUtil;

    @Autowired
    ResourceRepository resourceRepo;

    @Autowired
    ReportRepository reportRepo;

    @Autowired
    ProjectRepository projectRepo;

    @Autowired
    TxnUpdateReportRepository txnUpdateReportRepo;

    @Override
    public void taskListReport(List<Long> uidList) throws Exception {

        for (Long uid : uidList) {
            PResource resource = resourceRepo.findById(uid).get();
            Report report = new Report();

            String pattern = "dd MMMM yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            TaskListReportPOJO taskListReport = new TaskListReportPOJO();
            taskListReport.setTasks(projectService.getAllTasks(uid));
            taskListReport.setDate(simpleDateFormat.format(new Date()));
            taskListReport.setResourceName(resource.getName());
            taskListReport.setProjectName(resource.getProject().getName());
            taskListReport
                    .setPdfName((taskListReport.getDate() + taskListReport.getResourceName() + uid).replace(" ", ""));

            pdfGenUtil.createPdf(taskListReport);

            report.setName(taskListReport.getPdfName());
            report.setLocation("./pdf/" + report.getName() + ".pdf");
            report.setResource(resource);
            report.setType("tasklistreport");
            resource.setReport(report);
            reportRepo.save(report);
            resourceRepo.save(resource);

        }
    }

    public List<UpdateTaskReport> getUpdateReports(Long id) {

        Project project = projectRepo.findById(id).get();
        UpdateTaskReport updateTaskReport = new UpdateTaskReport();
        List<UpdateTaskReport> reports = new ArrayList<UpdateTaskReport>();
        for (Report report : project.getReports()) {
            updateTaskReport = new UpdateTaskReport();
            updateTaskReport.setId("" + report.getId());
            updateTaskReport.setLocation(report.getLocation());
            updateTaskReport.setName(report.getName());
            updateTaskReport.setProjectid(id);
            reports.add(updateTaskReport);
        }

        return reports;

    }

    public void updateReport(UpdateTask updateTask) throws Exception {

        // Set<Report> reports = new HashSet<Report>();
        // Report report = new Report();
        // String pattern = "dd MMMM yyyy";
        // SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        // PResource resource = resourceRepo.findById(updateTask.getResourceID()).get();
        // Project project = resource.getProject();

        // updateTask.setProjectName(resource.getProject().getName());
        // updateTask.setResourceName(resource.getName());
        // updateTask.setDate(simpleDateFormat.format(new Date()));

        // updateTask.setPdfName(updateTask.getTaskID() + "taskUpdateReport" +
        // updateTask.getDate().replace(" ", ""));

        // pdfGenUtil.createUpdatePdf(updateTask);

        // report.setName(updateTask.getPdfName());
        // report.setLocation("./pdf/" + report.getName() + ".pdf");
        // report.setType("updatetaskreport");
        // reports = project.getReports();
        // reports.add(report);
        // report.setProject(project);
        // project.setReports(reports);
        // projectRepo.save(project);

        TxnUpdateReport txnUpdateReport = new TxnUpdateReport();
        PResource resource = resourceRepo.findById(updateTask.getResourceID()).get();
        Project project = resource.getProject();

        for (TxnUpdateReport txnUpdateReportEntry : projectService.findTxnUpdateReportByProcessed(0, project.getId())) {
            if (txnUpdateReportEntry.getTaskID().equals(updateTask.getTaskID())) {
                txnUpdateReport = txnUpdateReportEntry;
            }
        }
        BeanUtils.copyProperties(updateTask, txnUpdateReport);
        if (!updateTask.isChangeRemainingDuration()) {
            txnUpdateReport.setRemainingDuration("");
        }
        txnUpdateReport.setProject(project);
        txnUpdateReport.setProcessed(0);
        txnUpdateReportRepo.save(txnUpdateReport);
    }

    public void downloadPdf(HttpServletResponse response, List<Long> uidList) {

        response.setContentType("application/octet-stream");
        response.setHeader("Access-Control-Allow-Headers",
                "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
        response.setHeader("Content-Disposition", "attachment;filename=download.zip");
        response.setStatus(HttpServletResponse.SC_OK);

        PResource dbResource = new PResource();

        try (ZipOutputStream zippedOut = new ZipOutputStream(response.getOutputStream())) {
            for (Long uid : uidList) {

                dbResource = resourceRepo.findById(uid).get();
                Report report = dbResource.getReport();
                FileSystemResource resource = new FileSystemResource(report.getLocation());

                ZipEntry e = new ZipEntry(resource.getFilename());
                // Configure the zip entry, the properties of the file
                e.setSize(resource.contentLength());
                e.setTime(System.currentTimeMillis());
                // etc.
                zippedOut.putNextEntry(e);
                // And the content of the resource:
                StreamUtils.copy(resource.getInputStream(), zippedOut);

                zippedOut.closeEntry();
            }
            zippedOut.finish();
        } catch (Exception e) {
            e.printStackTrace();
            // Exception handling goes here
        }

    }

    public void downloadReportPdf(HttpServletResponse response, Long projectId) throws Exception {

        UpdateReportResDTO updateReportResDTO = new UpdateReportResDTO();

        Project project = projectService.findProjectById(projectId);

        updateReportResDTO.setPdfName(projectId + "taskUpdateReport");
        updateReportResDTO.setProjectId(projectId);
        updateReportResDTO.setStatusDate(project.getStatusDate());
        updateReportResDTO.setProjectName(project.getName());

        updateReportResDTO.setUpdateReportList(projectService.findTxnUpdateReportByProcessed(1, projectId));
        for (TxnUpdateReport txnUpdateReport : projectService.findTxnUpdateReportByProcessed(1, projectId)) {
            txnUpdateReport.setProcessed(2);
            txnUpdateReportRepo.save(txnUpdateReport);
        }
        if (updateReportResDTO.getUpdateReportList().isEmpty()) {
            updateReportResDTO.setUpdateReportList(projectService.findTxnUpdateReportByProcessed(2, projectId));
        }
        Report report = pdfGenUtil.createUpdatePdf(updateReportResDTO);

        response.setContentType("application/octet-stream");
        response.setHeader("Access-Control-Allow-Headers",
                "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
        response.setHeader("Content-Disposition", "attachment;filename=download.zip");
        response.setStatus(HttpServletResponse.SC_OK);

        try (ZipOutputStream zippedOut = new ZipOutputStream(response.getOutputStream())) {
            // report = reportRepo.findById(id).get();
            FileSystemResource resource = new FileSystemResource(report.getLocation());

            ZipEntry e = new ZipEntry(resource.getFilename());
            // Configure the zip entry, the properties of the file
            e.setSize(resource.contentLength());
            e.setTime(System.currentTimeMillis());
            // etc.
            zippedOut.putNextEntry(e);
            // And the content of the resource:
            StreamUtils.copy(resource.getInputStream(), zippedOut);

            zippedOut.closeEntry();
            zippedOut.finish();
        } catch (Exception e) {
            e.printStackTrace();
            // Exception handling goes here
        }

    }

    @Override
    public void insertReport(Report report) {

        reportRepo.save(report);

    }

}