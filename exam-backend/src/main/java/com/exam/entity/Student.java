package com.exam.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_no", length = 20, unique = true, nullable = false)
    private String studentNo;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(name = "class_name", length = 50)
    private String className;

    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();

    public Student() {}

    public Student(String studentNo, String name, String password, String className) {
        this.studentNo = studentNo;
        this.name = name;
        this.password = password;
        this.className = className;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
