package com.exam.service;

import com.exam.entity.*;
import com.exam.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final QuestionRepository questionRepository;
    private final ExamPaperRepository paperRepository;
    private final ExamRecordRepository recordRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Map<String, Long> tokenStore = new HashMap<>();

    public TeacherService(TeacherRepository tr, StudentRepository sr, QuestionRepository qr,
                          ExamPaperRepository pr, ExamRecordRepository rr) {
        this.teacherRepository = tr;
        this.studentRepository = sr;
        this.questionRepository = qr;
        this.paperRepository = pr;
        this.recordRepository = rr;
    }

    /** 登录 */
    public Map<String, String> login(String username, String password) {
        Teacher t = teacherRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        if (!passwordEncoder.matches(password, t.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, t.getId());
        Map<String, String> result = new HashMap<>();
        result.put("token", token);
        result.put("name", t.getName());
        return result;
    }

    public Long getTeacherIdByToken(String token) {
        return tokenStore.get(token);
    }

    public Map<String, Object> getInfo(Long teacherId) {
        Teacher t = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("教师不存在"));
        Map<String, Object> info = new HashMap<>();
        info.put("username", t.getUsername());
        info.put("name", t.getName());
        return info;
    }

    // ========== 学生管理 ==========

    public List<Map<String, Object>> getStudents(String className) {
        List<Student> students;
        if (className != null && !className.isBlank()) {
            students = studentRepository.findAll().stream()
                .filter(s -> className.equals(s.getClassName())).toList();
        } else {
            students = studentRepository.findAll();
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Student s : students) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", s.getId());
            item.put("studentNo", s.getStudentNo());
            item.put("name", s.getName());
            item.put("className", s.getClassName());
            item.put("createTime", s.getCreateTime() != null ? s.getCreateTime().toString() : "");
            result.add(item);
        }
        return result;
    }

    public void resetStudentPassword(Long studentId) {
        Student s = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("学生不存在"));
        s.setPassword(passwordEncoder.encode("123456"));
        studentRepository.save(s);
    }

    // ========== 题库管理 ==========

    public List<Question> getQuestions(String subject, String type) {
        if (subject != null && !subject.isBlank() && type != null && !type.isBlank()) {
            return questionRepository.findBySubjectAndType(subject, type);
        }
        List<Question> all = questionRepository.findAll();
        if (subject != null && !subject.isBlank()) {
            all = all.stream().filter(q -> subject.equals(q.getSubject())).toList();
        }
        if (type != null && !type.isBlank()) {
            all = all.stream().filter(q -> type.equals(q.getType())).toList();
        }
        return all;
    }

    public Question saveQuestion(Question q) {
        if (q.getContent() == null || q.getContent().isBlank())
            throw new RuntimeException("题目内容不能为空");
        if (q.getType() == null || q.getType().isBlank())
            throw new RuntimeException("题目类型不能为空");
        if (q.getAnswer() == null || q.getAnswer().isBlank())
            throw new RuntimeException("答案不能为空");
        return questionRepository.save(q);
    }

    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    // ========== 试卷管理 ==========

    public List<ExamPaper> getPapers() {
        return paperRepository.findAll();
    }

    public ExamPaper savePaper(ExamPaper paper) {
        if (paper.getTitle() == null || paper.getTitle().isBlank())
            throw new RuntimeException("试卷名称不能为空");
        return paperRepository.save(paper);
    }

    public void deletePaper(Long id) {
        paperRepository.deleteById(id);
    }

    // ========== 成绩查看 ==========

    public List<Map<String, Object>> getRecords(String subject, String className, String status) {
        List<ExamRecord> records;
        if (status != null && !status.isBlank()) {
            records = recordRepository.findByStatus(status);
        } else {
            records = recordRepository.findAll();
        }

        // 构建学生和试卷缓存
        Map<Long, Student> studentMap = new HashMap<>();
        Map<Long, ExamPaper> paperMap = new HashMap<>();

        List<Map<String, Object>> result = new ArrayList<>();
        for (ExamRecord rec : records) {
            Student s = studentMap.computeIfAbsent(rec.getStudentId(),
                id -> studentRepository.findById(id).orElse(null));
            ExamPaper p = paperMap.computeIfAbsent(rec.getPaperId(),
                id -> paperRepository.findById(id).orElse(null));
            if (s == null || p == null) continue;

            // 按条件筛选
            if (subject != null && !subject.isBlank() && !subject.equals(p.getSubject())) continue;
            if (className != null && !className.isBlank() && !className.equals(s.getClassName())) continue;

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("recordId", rec.getId());
            item.put("studentName", s.getName());
            item.put("studentNo", s.getStudentNo());
            item.put("className", s.getClassName());
            item.put("paperTitle", p.getTitle());
            item.put("subject", p.getSubject());
            item.put("status", rec.getStatus());
            item.put("score", rec.getScore());
            item.put("totalScore", p.getTotalScore());
            item.put("submitTime", rec.getSubmitTime() != null ? rec.getSubmitTime().toString() : "");
            item.put("startTime", rec.getStartTime() != null ? rec.getStartTime().toString() : "");
            result.add(item);
        }
        return result;
    }

    /** 获取某条记录的完整答题详情（供教师查看+批改） */
    public Map<String, Object> getRecordDetail(Long recordId) {
        ExamRecord record = recordRepository.findById(recordId)
            .orElseThrow(() -> new RuntimeException("记录不存在"));
        ExamPaper paper = paperRepository.findById(record.getPaperId()).orElse(null);
        Student student = studentRepository.findById(record.getStudentId()).orElse(null);

        List<Long> qids;
        try {
            qids = objectMapper.readValue(record.getQuestionIds(), new TypeReference<List<Long>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("题目ID解析失败");
        }
        List<Question> questions = questionRepository.findAllById(qids);
        Map<Long, Question> qmap = new HashMap<>();
        for (Question q : questions) qmap.put(q.getId(), q);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("recordId", record.getId());
        result.put("studentName", student != null ? student.getName() : "");
        result.put("studentNo", student != null ? student.getStudentNo() : "");
        result.put("paperTitle", paper != null ? paper.getTitle() : "");
        result.put("totalScore", paper != null ? paper.getTotalScore() : 0);
        result.put("score", record.getScore());
        result.put("status", record.getStatus());

        // 解析答案
        Map<Long, String> answerMap = new HashMap<>();
        try {
            List<Map<String, Object>> answers = objectMapper.readValue(record.getAnswers(),
                new TypeReference<List<Map<String, Object>>>() {});
            for (Map<String, Object> a : answers) {
                Long qid = a.get("questionId") instanceof Integer
                    ? ((Integer) a.get("questionId")).longValue() : (Long) a.get("questionId");
                answerMap.put(qid, (String) a.get("answer"));
            }
        } catch (JsonProcessingException e) {}

        List<Map<String, Object>> details = new ArrayList<>();
        for (Long qid : qids) {
            Question q = qmap.get(qid);
            if (q == null) continue;
            Map<String, Object> d = new LinkedHashMap<>();
            d.put("questionId", qid);
            d.put("content", q.getContent());
            d.put("type", q.getType());
            d.put("score", q.getScore());
            d.put("correctAnswer", q.getAnswer());
            d.put("explanation", q.getExplanation() != null ? q.getExplanation() : "");
            d.put("studentAnswer", answerMap.getOrDefault(qid, ""));
            if ("CHOICE".equals(q.getType()) || "MULTI_CHOICE".equals(q.getType())) {
                Map<String, String> opts = new LinkedHashMap<>();
                opts.put("A", q.getOptionA()); opts.put("B", q.getOptionB());
                opts.put("C", q.getOptionC()); opts.put("D", q.getOptionD());
                d.put("options", opts);
            } else if ("JUDGE".equals(q.getType())) {
                Map<String, String> opts = new LinkedHashMap<>();
                opts.put("T", "正确"); opts.put("F", "错误");
                d.put("options", opts);
            }
            // 计算客观题正确性
            if ("CHOICE".equals(q.getType()) || "JUDGE".equals(q.getType())) {
                d.put("isCorrect", q.getAnswer().equals(answerMap.getOrDefault(qid, "")));
            } else if ("MULTI_CHOICE".equals(q.getType())) {
                Set<String> correctSet = new HashSet<>(Arrays.asList(q.getAnswer().split(",")));
                Set<String> studentSet = new HashSet<>();
                String sa = answerMap.getOrDefault(qid, "");
                if (!sa.isBlank()) studentSet.addAll(Arrays.asList(sa.split(",")));
                d.put("isCorrect", correctSet.equals(studentSet));
            } else if ("BLANK".equals(q.getType())) {
                String sa = answerMap.getOrDefault(qid, "");
                d.put("isCorrect", q.getAnswer().trim().equals(sa != null ? sa.trim() : ""));
            }
            details.add(d);
        }
        result.put("details", details);
        return result;
    }

    /** 简答题批改 */
    public void gradeEssay(Long recordId, Long questionId, int essayScore) {
        ExamRecord record = recordRepository.findById(recordId)
            .orElseThrow(() -> new RuntimeException("记录不存在"));
        // 题目分数验证
        Question q = questionRepository.findById(questionId).orElse(null);
        if (q == null || !"ESSAY".equals(q.getType()))
            throw new RuntimeException("只能批改简答题");
        if (essayScore < 0 || essayScore > q.getScore())
            throw new RuntimeException("分数范围 0~" + q.getScore());

        // 更新总分：原客观题得分 + 新批改分数
        // 先计算原客观题得分（从之前自动评分的题型）
        int objectiveScore = 0;
        List<Long> qids;
        try {
            qids = objectMapper.readValue(record.getQuestionIds(), new TypeReference<List<Long>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("题目ID解析失败");
        }
        List<Question> questions = questionRepository.findAllById(qids);
        Map<Long, Question> qmap = new HashMap<>();
        for (Question qu : questions) qmap.put(qu.getId(), qu);

        Map<Long, String> answerMap = new HashMap<>();
        try {
            List<Map<String, Object>> answers = objectMapper.readValue(record.getAnswers(),
                new TypeReference<List<Map<String, Object>>>() {});
            for (Map<String, Object> a : answers) {
                Long qid = a.get("questionId") instanceof Integer
                    ? ((Integer) a.get("questionId")).longValue() : (Long) a.get("questionId");
                answerMap.put(qid, (String) a.get("answer"));
            }
        } catch (JsonProcessingException e) {}

        for (Long qid : qids) {
            Question qu = qmap.get(qid);
            if (qu == null) continue;
            if (!"ESSAY".equals(qu.getType())) {
                String sa = answerMap.getOrDefault(qid, "");
                if ("CHOICE".equals(qu.getType()) || "JUDGE".equals(qu.getType())) {
                    if (qu.getAnswer().equals(sa)) objectiveScore += qu.getScore();
                } else if ("MULTI_CHOICE".equals(qu.getType())) {
                    Set<String> cs = new HashSet<>(Arrays.asList(qu.getAnswer().split(",")));
                    Set<String> ss = new HashSet<>();
                    if (!sa.isBlank()) ss.addAll(Arrays.asList(sa.split(",")));
                    if (cs.equals(ss)) objectiveScore += qu.getScore();
                } else if ("BLANK".equals(qu.getType())) {
                    if (qu.getAnswer().trim().equals(sa != null ? sa.trim() : ""))
                        objectiveScore += qu.getScore();
                }
            }
        }
        record.setScore(objectiveScore + essayScore);
        recordRepository.save(record);
    }

    /** 全局统计 */
    public Map<String, Object> getStats() {
        long studentCount = studentRepository.count();
        List<ExamRecord> submitted = recordRepository.findByStatus("SUBMITTED");
        long examCount = submitted.size();
        double avgScore = submitted.stream().mapToInt(ExamRecord::getScore).average().orElse(0);

        // 待批改数量（有简答题的已提交记录）
        long pendingGrade = 0;
        for (ExamRecord rec : submitted) {
            ExamPaper p = paperRepository.findById(rec.getPaperId()).orElse(null);
            if (p != null && p.getEssayCount() != null && p.getEssayCount() > 0) {
                pendingGrade++;
            }
        }

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("studentCount", studentCount);
        stats.put("examCount", examCount);
        stats.put("avgScore", Math.round(avgScore * 10) / 10.0);
        stats.put("pendingGrade", pendingGrade);
        stats.put("paperCount", paperRepository.count());
        stats.put("questionCount", questionRepository.count());
        return stats;
    }
}
