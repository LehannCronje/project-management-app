package com.example.demo.Entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@SQLDelete(sql = "UPDATE project SET delete_date = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "delete_date IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class Project {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    private Date deleteDate;

    private Date statusDate;

    private boolean isLocked;

    @ManyToMany(mappedBy = "projects")
    private Set<User> users = new HashSet<User>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private Set<PResource> resources = new HashSet<PResource>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mpxfile_id", referencedColumnName = "id")
    private MpxFile mpxFile;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    private Set<Report> reports = new HashSet<Report>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    private Set<TxnUpdateReport> txnUpdateReports = new HashSet<TxnUpdateReport>();

    public void removeResource(PResource resource) {
        resources.remove(resource);
        resource.setTasks(null);
        resource.setReport(null);
    }

    public void removeUsers() {
        for (User user : this.users) {
            user.removeProject(this);
        }
        this.users = new HashSet<User>();
    }

    public void removeMpxFile(MpxFile mpxFile) {
        this.mpxFile = null;
    }

}