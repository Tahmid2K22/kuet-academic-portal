package com.example.kuet_academic_portal.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.kuet_academic_portal.model.StudentSession;

public class SessionManager {

    private static final String PREF_NAME = "student_session_pref";
    private static final String KEY_DEPARTMENT = "department";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_ROLL = "roll";
    private static final String KEY_SECTION = "section";
    private static final String KEY_TERM = "term";
    private static final String KEY_YEAR = "year";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(StudentSession student) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_DEPARTMENT, student.getDepartment());
        editor.putString(KEY_EMAIL, student.getEmail());
        editor.putString(KEY_NAME, student.getName());
        editor.putString(KEY_PHONE, student.getPhone());
        editor.putString(KEY_ROLL, student.getRoll());
        editor.putString(KEY_SECTION, student.getSection());
        editor.putInt(KEY_TERM, student.getTerm());
        editor.putInt(KEY_YEAR, student.getYear());
        editor.apply();
    }

    public StudentSession getSession() {
        String email = prefs.getString(KEY_EMAIL, null);
        if (email == null) {
            return null;
        }

        StudentSession student = new StudentSession();
        student.setDepartment(prefs.getString(KEY_DEPARTMENT, ""));
        student.setEmail(email);
        student.setName(prefs.getString(KEY_NAME, ""));
        student.setPhone(prefs.getString(KEY_PHONE, ""));
        student.setRoll(prefs.getString(KEY_ROLL, ""));
        student.setSection(prefs.getString(KEY_SECTION, ""));
        student.setTerm(prefs.getInt(KEY_TERM, 0));
        student.setYear(prefs.getInt(KEY_YEAR, 0));
        return student;
    }

    public boolean isLoggedIn() {
        return prefs.getString(KEY_EMAIL, null) != null;
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }

    public String getStudentName() {
        return prefs.getString(KEY_NAME, "");
    }

    public String getStudentEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public String getStudentDepartment() {
        return prefs.getString(KEY_DEPARTMENT, "");
    }

    public String getStudentRoll() {
        return prefs.getString(KEY_ROLL, "");
    }
}

