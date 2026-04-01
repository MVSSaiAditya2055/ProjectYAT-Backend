package com.klu.ProjectYAT.model;

import jakarta.persistence.*;

@Entity
@Table(name = "student_course", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "course_id"})
})
public class StudentCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(columnDefinition = "integer default 0")
    private int marks = 0;

    @Column(columnDefinition = "LONGTEXT")
    private String feedback;

    @Column(name = "enrollment_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private long enrollmentDate = System.currentTimeMillis();

    // No-args constructor
    public StudentCourse() {}

    // Constructor with student and course
    public StudentCourse(User student, Course course) {
        this.student = student;
        this.course = course;
        this.marks = 0;
        this.enrollmentDate = System.currentTimeMillis();
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public long getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(long enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
}
