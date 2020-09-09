package com.example.demo.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import com.example.demo.Domain.UpdateTask;
import com.example.demo.Domain.UpdateTaskReport;
import com.example.demo.Entity.Report;
import com.example.demo.Service.ReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    ReportService reportService;

    @PostMapping(value = "/taskList")
    public void taskListReport(HttpServletResponse response, @RequestParam("data") List<Long> data, @RequestParam("filterType") String filterType, @RequestParam("timeValue") String timeValue) throws Exception {
        System.out.println(filterType + timeValue);
        reportService.taskListReport(data, filterType, Integer.parseInt(timeValue));
        reportService.downloadPdf(response, data);
    }

    @PostMapping("/taskUpdate")
    public void mobileUpdate(@RequestBody UpdateTask data) throws Exception {
        reportService.updateReport(data);
    }

    @GetMapping("/update-report/{id}")
    public List<UpdateTaskReport> getUpdateReport(@PathVariable("id") Long id) {
        return reportService.getUpdateReports(id);

    }

    @GetMapping("/update-report/download/{projectId}")
    public void updateReportDownload(HttpServletResponse response, @PathVariable("projectId") Long projectId)
            throws Exception {
        reportService.downloadReportPdf(response, projectId);
    }
}