package com.example.demo.Entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.Data;

@Entity
@SQLDelete(sql = "UPDATE report SET delete_date = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "delete_date IS NULL")
@Data
public class Report {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String location;

    private String type;
    
    private Date deleteDate;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "report")
    private PResource resource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

}