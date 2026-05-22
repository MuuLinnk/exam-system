package com.exam.dto;

public class RegisterRequest {
    private String studentNo;
    private String name;
    private String password;
    private String className;

    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}
