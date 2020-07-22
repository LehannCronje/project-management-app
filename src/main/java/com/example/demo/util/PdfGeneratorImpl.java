package com.example.demo.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import com.example.demo.Domain.TaskListReportPOJO;
import com.example.demo.Entity.Report;
import com.example.demo.Service.ProjectService;
import com.example.demo.Service.ReportService;
import com.example.demo.dto.UpdateReportResDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

@Service
public class PdfGeneratorImpl implements PdfGenerartor {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ReportService reportService;

    private static final String UTF_8 = "UTF-8";

    public <T> void createPdf(TaskListReportPOJO taskListReport) throws Exception {

        Context context = new Context();
        context.setVariable("data", taskListReport);
        context.setVariable("baseUrl", getCurrentBaseUrl());

        String processHTML = templateEngine.process("template", context);

        String xHtml = convertToXhtml(processHTML);

        ITextRenderer renderer = new ITextRenderer();

        String baseUrl = getClass().getResource("/templates/").toString();

        renderer.setDocumentFromString(xHtml, baseUrl);
        renderer.layout();

        // And finally, we create the PDF:
        OutputStream outputStream = new FileOutputStream("./pdf/" + taskListReport.getPdfName() + ".pdf");
        renderer.createPDF(outputStream);
        outputStream.close();

    }

    public <T> Report createUpdatePdf(UpdateReportResDTO updateReportResDTO) throws Exception {

        Report report = new Report();

        report.setLocation("./pdf/" + updateReportResDTO.getPdfName() + ".pdf");
        report.setName(updateReportResDTO.getPdfName());
        report.setProject(projectService.findProjectById(updateReportResDTO.getProjectId()));
        report.setType("updatetaskreport");

        reportService.insertReport(report);

        Context context = new Context();
        context.setVariable("data", updateReportResDTO);
        context.setVariable("baseUrl", getCurrentBaseUrl());

        String processHTML = templateEngine.process("updateReport", context);

        String xHtml = convertToXhtml(processHTML);

        ITextRenderer renderer = new ITextRenderer();

        String baseUrl = getClass().getResource("/templates/").toString();

        renderer.setDocumentFromString(xHtml, baseUrl);
        renderer.layout();

        // And finally, we create the PDF:
        OutputStream outputStream = new FileOutputStream(report.getLocation());
        renderer.createPDF(outputStream);
        outputStream.close();

        return report;
    }

    private static String getCurrentBaseUrl() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest req = sra.getRequest();
        return req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
    }

    private String convertToXhtml(String html) throws UnsupportedEncodingException {
        Tidy tidy = new Tidy();
        tidy.setInputEncoding(UTF_8);
        tidy.setOutputEncoding(UTF_8);
        tidy.setXHTML(true);

        tidy.setShowErrors(0);
        tidy.setShowWarnings(false);
        tidy.setQuiet(true);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(html.getBytes(UTF_8));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        tidy.parseDOM(inputStream, outputStream);
        return outputStream.toString(UTF_8);
    }

}