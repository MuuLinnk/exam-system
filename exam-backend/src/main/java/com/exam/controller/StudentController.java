package com.exam.controller;

import com.exam.dto.ApiResponse;
import com.exam.dto.LoginRequest;
import com.exam.dto.RegisterRequest;
import com.exam.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /** 注册 */
    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody RegisterRequest req) {
        try {
            studentService.register(req);
            return ApiResponse.success("注册成功", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    /** 登录 */
    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody LoginRequest req) {
        try {
            return ApiResponse.success("登录成功", studentService.login(req));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    /** 获取个人信息 */
    @GetMapping("/info")
    public ApiResponse<?> info(@RequestHeader("token") String token) {
        try {
            Long studentId = studentService.getStudentIdByToken(token);
            if (studentId == null) return ApiResponse.error(401, "请先登录");
            return ApiResponse.success(studentService.getInfo(studentId));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    /** 修改密码 */
    @PutMapping("/password")
    public ApiResponse<?> changePassword(@RequestHeader("token") String token,
                                          @RequestBody Map<String, String> body) {
        try {
            Long studentId = studentService.getStudentIdByToken(token);
            if (studentId == null) return ApiResponse.error(401, "请先登录");
            studentService.changePassword(studentId,
                body.get("oldPassword"), body.get("newPassword"));
            return ApiResponse.success("密码修改成功", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    /** 找回密码 */
    @PostMapping("/reset-password")
    public ApiResponse<?> resetPassword(@RequestBody Map<String, String> body) {
        try {
            studentService.resetPassword(body.get("studentNo"), body.get("name"));
            return ApiResponse.success("密码已重置为 123456，请登录后修改", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
}
