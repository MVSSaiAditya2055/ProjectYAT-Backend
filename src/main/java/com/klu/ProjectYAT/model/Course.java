package com.klu.ProjectYAT.model;

import jakarta.persistence.*;

@Entity
@Table(name="courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    
    @Column(length = 2000)
    private String description;

    @Column(name = "registered_students", columnDefinition = "integer default 0")
    private int registeredStudents = 0;

    @Column(columnDefinition = "LONGTEXT")
    private String modules;

    private String assignmentFileName;
    private String assignmentFileType;
    
    @Column(columnDefinition = "LONGTEXT")
    private String assignmentFileDataUrl;

    public Course() {}

    public Course(long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRegisteredStudents() {
        return registeredStudents;
    }

    public void setRegisteredStudents(int registeredStudents) {
        this.registeredStudents = registeredStudents;
    }

    public String getModules() {
        return modules;
    }

    public void setModules(String modules) {
        this.modules = modules;
    }

    public String getAssignmentFileName() {
        return assignmentFileName;
    }

    public void setAssignmentFileName(String assignmentFileName) {
        this.assignmentFileName = assignmentFileName;
    }

    public String getAssignmentFileType() {
        return assignmentFileType;
    }

    public void setAssignmentFileType(String assignmentFileType) {
        this.assignmentFileType = assignmentFileType;
    }

    public String getAssignmentFileDataUrl() {
        return assignmentFileDataUrl;
    }

    public void setAssignmentFileDataUrl(String assignmentFileDataUrl) {
        this.assignmentFileDataUrl = assignmentFileDataUrl;
    }
}
