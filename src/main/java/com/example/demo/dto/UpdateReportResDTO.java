package com.example.demo.dto;

import java.util.Date;
import java.util.Set;

import com.example.demo.Entity.TxnUpdateReport;

import lombok.Data;

@Data
public class UpdateReportResDTO {

    private String pdfName;

    private Long projectId;

    private String projectName;

    private Date statusDate;

    private Set<TxnUpdateReport> updateReportList;
}