package com.example.demo.util;

import com.example.demo.Domain.TaskListReportPOJO;
import com.example.demo.Entity.Report;
import com.example.demo.dto.UpdateReportResDTO;

public interface PdfGenerartor {

    public <T> void createPdf(TaskListReportPOJO taskListReport) throws Exception;

    public <T> Report createUpdatePdf(UpdateReportResDTO updateReportResDTO) throws Exception;
}