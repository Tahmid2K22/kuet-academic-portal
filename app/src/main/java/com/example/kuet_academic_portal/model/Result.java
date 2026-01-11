package com.example.kuet_academic_portal.model;

import java.util.Map;

public class Result {
    private String roll;
    private int year;
    private int term;
    private Double gpa;
    private Map<String, Float> ctMarks; // Course Name -> Marks

    public Result() {
        // Required empty public constructor
    }

    public Result(String roll, int year, int term, Double gpa, Map<String, Float> ctMarks) {
        this.roll = roll;
        this.year = year;
        this.term = term;
        this.gpa = gpa;
        this.ctMarks = ctMarks;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public Double getGpa() {
        return gpa;
    }

    public void setGpa(Double gpa) {
        this.gpa = gpa;
    }

    public Map<String, Float> getCtMarks() {
        return ctMarks;
    }

    public void setCtMarks(Map<String, Float> ctMarks) {
        this.ctMarks = ctMarks;
    }
}
