package com.exam.controller;

import com.exam.dto.ApiResponse;
import com.exam.service.TeacherService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    private Long auth(String token) {
        Long id = teacherService.getTeacherIdByToken(token);
        if (id == null) throw new RuntimeException("请先登录");
        return id;
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody Map<String, String> body) {
        try {
            return ApiResponse.success("登录成功",
                teacherService.login(body.get("username"), body.get("password")));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/info")
    public ApiResponse<?> info(@RequestHeader("token") String token) {
        try {
            return ApiResponse.success(teacherService.getInfo(auth(token)));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/students")
    public ApiResponse<?> students(@RequestHeader("token") String token,
                                   @RequestParam(required = false) String className) {
        try {
            auth(token);
            return ApiResponse.success(teacherService.getStudents(className));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/students/reset-password/{id}")
    public ApiResponse<?> resetStudentPassword(@RequestHeader("token") String token,
                                               @PathVariable Long id) {
        try {
            auth(token);
            teacherService.resetStudentPassword(id);
            return ApiResponse.success("密码已重置为 123456", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/questions")
    public ApiResponse<?> questions(@RequestHeader("token") String token,
                                    @RequestParam(required = false) String subject,
                                    @RequestParam(required = false) String type) {
        try {
            auth(token);
            return ApiResponse.success(teacherService.getQuestions(subject, type));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/questions")
    public ApiResponse<?> saveQuestion(@RequestHeader("token") String token,
                                       @RequestBody com.exam.entity.Question q) {
        try {
            auth(token);
            return ApiResponse.success("保存成功", teacherService.saveQuestion(q));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PutMapping("/questions/{id}")
    public ApiResponse<?> updateQuestion(@RequestHeader("token") String token,
                                         @PathVariable Long id,
                                         @RequestBody com.exam.entity.Question q) {
        try {
            auth(token);
            q.setId(id);
            return ApiResponse.success("更新成功", teacherService.saveQuestion(q));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @DeleteMapping("/questions/{id}")
    public ApiResponse<?> deleteQuestion(@RequestHeader("token") String token,
                                         @PathVariable Long id) {
        try {
            auth(token);
            teacherService.deleteQuestion(id);
            return ApiResponse.success("删除成功", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/papers")
    public ApiResponse<?> papers(@RequestHeader("token") String token) {
        try {
            auth(token);
            return ApiResponse.success(teacherService.getPapers());
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/papers")
    public ApiResponse<?> savePaper(@RequestHeader("token") String token,
                                    @RequestBody com.exam.entity.ExamPaper paper) {
        try {
            auth(token);
            return ApiResponse.success("保存成功", teacherService.savePaper(paper));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PutMapping("/papers/{id}")
    public ApiResponse<?> updatePaper(@RequestHeader("token") String token,
                                      @PathVariable Long id,
                                      @RequestBody com.exam.entity.ExamPaper paper) {
        try {
            auth(token);
            paper.setId(id);
            return ApiResponse.success("更新成功", teacherService.savePaper(paper));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @DeleteMapping("/papers/{id}")
    public ApiResponse<?> deletePaper(@RequestHeader("token") String token,
                                      @PathVariable Long id) {
        try {
            auth(token);
            teacherService.deletePaper(id);
            return ApiResponse.success("删除成功", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/records")
    public ApiResponse<?> records(@RequestHeader("token") String token,
                                  @RequestParam(required = false) String subject,
                                  @RequestParam(required = false) String className,
                                  @RequestParam(required = false) String status) {
        try {
            auth(token);
            return ApiResponse.success(teacherService.getRecords(subject, className, status));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/records/{id}")
    public ApiResponse<?> recordDetail(@RequestHeader("token") String token,
                                       @PathVariable Long id) {
        try {
            auth(token);
            return ApiResponse.success(teacherService.getRecordDetail(id));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PutMapping("/grade")
    public ApiResponse<?> grade(@RequestHeader("token") String token,
                                @RequestBody Map<String, Object> body) {
        try {
            auth(token);
            Long recordId = ((Number) body.get("recordId")).longValue();
            Long questionId = ((Number) body.get("questionId")).longValue();
            int score = ((Number) body.get("score")).intValue();
            teacherService.gradeEssay(recordId, questionId, score);
            return ApiResponse.success("批改成功", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/stats")
    public ApiResponse<?> stats(@RequestHeader("token") String token) {
        try {
            auth(token);
            return ApiResponse.success(teacherService.getStats());
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
}
