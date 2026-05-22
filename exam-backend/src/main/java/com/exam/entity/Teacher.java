package com.exam.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "teacher")
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, unique = true, nullable = false)
    private String username;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(length = 100, nullable = false)
    private String password;

    public Teacher() {}

    public Teacher(String username, String name, String password) {
        this.username = username;
        this.name = name;
        this.password = password;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
