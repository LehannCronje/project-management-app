package com.example.demo.Service;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import com.example.demo.Domain.UpdateTask;
import com.example.demo.Domain.UpdateTaskReport;
import com.example.demo.Entity.Report;

public interface ReportService {

    public void taskListReport(List<Long> uidList, String calendarType, int value) throws Exception;

    public void updateReport(UpdateTask updateTask) throws Exception;

    public List<UpdateTaskReport> getUpdateReports(Long id);

    public void downloadPdf(HttpServletResponse response, List<Long> uidList);

    public void downloadReportPdf(HttpServletResponse response, Long projectId) throws Exception;

    public void insertReport(Report report);

}