package com.example.kuet_academic_portal.model;

import com.google.firebase.Timestamp;

public class Attendance {
    private String id;
    private String roll;
    private String studentName;
    private String course;
    private String teacher;
    private Timestamp date;
    private String status;
    private String department;
    private String semester;

    public Attendance() {
    }

    public Attendance(String roll, String studentName, String course, String teacher,
                     Timestamp date, String status, String department, String semester) {
        this.roll = roll;
        this.studentName = studentName;
        this.course = course;
        this.teacher = teacher;
        this.date = date;
        this.status = status;
        this.department = department;
        this.semester = semester;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}

