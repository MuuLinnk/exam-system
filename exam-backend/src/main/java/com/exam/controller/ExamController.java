package com.exam.controller;

import com.exam.dto.ApiResponse;
import com.exam.dto.SubmitRequest;
import com.exam.service.ExamService;
import com.exam.service.StudentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exam")
public class ExamController {

    private final ExamService examService;
    private final StudentService studentService;

    public ExamController(ExamService examService, StudentService studentService) {
        this.examService = examService;
        this.studentService = studentService;
    }

    /** 获取考试列表 */
    @GetMapping("/list")
    public ApiResponse<?> list(@RequestHeader("token") String token) {
        try {
            Long studentId = studentService.getStudentIdByToken(token);
            if (studentId == null) return ApiResponse.error(401, "请先登录");
            return ApiResponse.success(examService.getExamList(studentId));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    /** 开始考试 */
    @GetMapping("/start/{paperId}")
    public ApiResponse<?> start(@RequestHeader("token") String token,
                                @PathVariable Long paperId) {
        try {
            Long studentId = studentService.getStudentIdByToken(token);
            if (studentId == null) return ApiResponse.error(401, "请先登录");
            return ApiResponse.success(examService.startExam(studentId, paperId));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    /** 提交答案 */
    @PostMapping("/submit")
    public ApiResponse<?> submit(@RequestHeader("token") String token,
                                  @RequestBody SubmitRequest req) {
        try {
            Long studentId = studentService.getStudentIdByToken(token);
            if (studentId == null) return ApiResponse.error(401, "请先登录");
            return ApiResponse.success("提交成功", examService.submit(studentId, req));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    /** 查看成绩 */
    @GetMapping("/record/{recordId}")
    public ApiResponse<?> record(@RequestHeader("token") String token,
                                  @PathVariable Long recordId) {
        try {
            Long studentId = studentService.getStudentIdByToken(token);
            if (studentId == null) return ApiResponse.error(401, "请先登录");
            return ApiResponse.success(examService.getRecord(studentId, recordId));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    /** 错题本 */
    @GetMapping("/wrong-book")
    public ApiResponse<?> wrongBook(@RequestHeader("token") String token) {
        try {
            Long studentId = studentService.getStudentIdByToken(token);
            if (studentId == null) return ApiResponse.error(401, "请先登录");
            return ApiResponse.success(examService.getWrongBook(studentId));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    /** 成绩统计 */
    @GetMapping("/stats")
    public ApiResponse<?> stats(@RequestHeader("token") String token) {
        try {
            Long studentId = studentService.getStudentIdByToken(token);
            if (studentId == null) return ApiResponse.error(401, "请先登录");
            return ApiResponse.success(examService.getStats(studentId));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
}
