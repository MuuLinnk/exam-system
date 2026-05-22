package com.exam.service;

import com.exam.dto.LoginRequest;
import com.exam.dto.RegisterRequest;
import com.exam.entity.Student;
import com.exam.repository.StudentRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    // 简单的 token 存储（生产环境应该用 Redis）
    private final Map<String, Long> tokenStore = new HashMap<>();

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /** 注册 */
    public String register(RegisterRequest req) {
        if (req.getStudentNo() == null || req.getStudentNo().isBlank()) {
            throw new RuntimeException("学号不能为空");
        }
        if (req.getPassword() == null || req.getPassword().length() < 6) {
            throw new RuntimeException("密码至少6位");
        }
        if (studentRepository.existsByStudentNo(req.getStudentNo())) {
            throw new RuntimeException("学号已存在");
        }
        Student student = new Student(
            req.getStudentNo(),
            req.getName(),
            passwordEncoder.encode(req.getPassword()),
            req.getClassName()
        );
        studentRepository.save(student);
        return "注册成功";
    }

    /** 登录，返回 token */
    public Map<String, String> login(LoginRequest req) {
        Student student = studentRepository.findByStudentNo(req.getStudentNo())
            .orElseThrow(() -> new RuntimeException("学号或密码错误"));
        if (!passwordEncoder.matches(req.getPassword(), student.getPassword())) {
            throw new RuntimeException("学号或密码错误");
        }
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, student.getId());
        Map<String, String> result = new HashMap<>();
        result.put("token", token);
        result.put("studentName", student.getName());
        return result;
    }

    /** 根据 token 获取学生 ID */
    public Long getStudentIdByToken(String token) {
        return tokenStore.get(token);
    }

    /** 获取学生信息 */
    public Map<String, Object> getInfo(Long studentId) {
        Student s = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("学生不存在"));
        Map<String, Object> info = new HashMap<>();
        info.put("studentNo", s.getStudentNo());
        info.put("name", s.getName());
        info.put("className", s.getClassName());
        info.put("createTime", s.getCreateTime() != null ? s.getCreateTime().toString() : "");
        return info;
    }

    /** 修改密码 */
    public void changePassword(Long studentId, String oldPassword, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("新密码至少6位");
        }
        Student s = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("学生不存在"));
        if (!passwordEncoder.matches(oldPassword, s.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }
        s.setPassword(passwordEncoder.encode(newPassword));
        studentRepository.save(s);
    }

    /** 找回密码 */
    public void resetPassword(String studentNo, String name) {
        Student s = studentRepository.findByStudentNo(studentNo)
            .orElseThrow(() -> new RuntimeException("学号不存在"));
        if (!s.getName().equals(name)) {
            throw new RuntimeException("姓名不匹配");
        }
        s.setPassword(passwordEncoder.encode("123456"));
        studentRepository.save(s);
    }
}
