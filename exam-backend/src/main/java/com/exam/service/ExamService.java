package com.exam.service;

import com.exam.dto.SubmitRequest;
import com.exam.entity.ExamPaper;
import com.exam.entity.ExamRecord;
import com.exam.entity.Question;
import com.exam.repository.ExamPaperRepository;
import com.exam.repository.ExamRecordRepository;
import com.exam.repository.QuestionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ExamService {

    private final ExamPaperRepository paperRepository;
    private final ExamRecordRepository recordRepository;
    private final QuestionRepository questionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ExamService(ExamPaperRepository pr, ExamRecordRepository rr, QuestionRepository qr) {
        this.paperRepository = pr;
        this.recordRepository = rr;
        this.questionRepository = qr;
    }

    /** 获取所有考试列表 */
    public List<Map<String, Object>> getExamList(Long studentId) {
        List<ExamPaper> papers = paperRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (ExamPaper paper : papers) {
            Map<String, Object> item = new HashMap<>();
            item.put("paperId", paper.getId());
            item.put("title", paper.getTitle());
            item.put("subject", paper.getSubject());
            item.put("duration", paper.getDuration());
            item.put("totalScore", paper.getTotalScore());

            ExamRecord submitted = recordRepository
                .findByStudentIdAndPaperIdAndStatus(studentId, paper.getId(), "SUBMITTED");
            if (submitted != null) {
                item.put("status", "SUBMITTED");
                item.put("recordId", submitted.getId());
                item.put("score", submitted.getScore());
            } else {
                ExamRecord doing = recordRepository
                    .findByStudentIdAndPaperIdAndStatus(studentId, paper.getId(), "DOING");
                if (doing != null) {
                    item.put("status", "DOING");
                    item.put("recordId", doing.getId());
                } else {
                    item.put("status", "NOT_STARTED");
                }
            }
            result.add(item);
        }
        return result;
    }

    /** 开始考试：随机抽题 */
    @Transactional
    public Map<String, Object> startExam(Long studentId, Long paperId) {
        ExamPaper paper = paperRepository.findById(paperId)
            .orElseThrow(() -> new RuntimeException("试卷不存在"));

        // 检查已有记录
        ExamRecord record = recordRepository
            .findByStudentIdAndPaperIdAndStatus(studentId, paperId, "DOING");
        if (record == null) {
            ExamRecord submitted = recordRepository
                .findByStudentIdAndPaperIdAndStatus(studentId, paperId, "SUBMITTED");
            if (submitted != null) {
                throw new RuntimeException("你已提交过该试卷");
            }

            // 随机抽题（按类型分组：单选→多选→判断→填空→简答）
            List<Question> choicePool = questionRepository
                .findBySubjectAndType(paper.getSubject(), "CHOICE");
            List<Question> multiChoicePool = questionRepository
                .findBySubjectAndType(paper.getSubject(), "MULTI_CHOICE");
            List<Question> judgePool = questionRepository
                .findBySubjectAndType(paper.getSubject(), "JUDGE");
            List<Question> blankPool = questionRepository
                .findBySubjectAndType(paper.getSubject(), "BLANK");
            List<Question> essayPool = questionRepository
                .findBySubjectAndType(paper.getSubject(), "ESSAY");

            List<Question> selected = new ArrayList<>();
            selected.addAll(randomPick(choicePool, paper.getChoiceCount()));
            selected.addAll(randomPick(multiChoicePool, paper.getMultiChoiceCount() != null ? paper.getMultiChoiceCount() : 0));
            selected.addAll(randomPick(judgePool, paper.getJudgeCount() != null ? paper.getJudgeCount() : 0));
            selected.addAll(randomPick(blankPool, paper.getBlankCount()));
            selected.addAll(randomPick(essayPool, paper.getEssayCount()));
            // 不 shuffle，保持题型分组顺序

            // 存抽题结果
            List<Long> questionIds = selected.stream().map(Question::getId).toList();
            record = new ExamRecord();
            record.setStudentId(studentId);
            record.setPaperId(paperId);
            record.setStatus("DOING");
            record.setStartTime(LocalDateTime.now());
            record.setAnswers("[]");
            try {
                record.setQuestionIds(objectMapper.writeValueAsString(questionIds));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("序列化失败");
            }
            record = recordRepository.save(record);
        }

        // 解析抽到的题目ID
        List<Long> qids;
        try {
            qids = objectMapper.readValue(record.getQuestionIds(), new TypeReference<List<Long>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("解析题目ID失败");
        }

        // 构建返回
        Map<String, Object> result = new HashMap<>();
        result.put("recordId", record.getId());
        result.put("paperTitle", paper.getTitle());
        result.put("duration", paper.getDuration());
        result.put("totalScore", paper.getTotalScore());

        List<Map<String, Object>> questionList = new ArrayList<>();
        for (Long qid : qids) {
            Question q = questionRepository.findById(qid).orElse(null);
            if (q == null) continue;
            Map<String, Object> qmap = new HashMap<>();
            qmap.put("questionId", q.getId());
            qmap.put("type", q.getType());
            qmap.put("content", q.getContent());
            qmap.put("score", q.getScore());
            if ("CHOICE".equals(q.getType()) || "MULTI_CHOICE".equals(q.getType())) {
                Map<String, String> options = new LinkedHashMap<>();
                options.put("A", q.getOptionA());
                options.put("B", q.getOptionB());
                options.put("C", q.getOptionC());
                options.put("D", q.getOptionD());
                qmap.put("options", options);
            }
            if ("JUDGE".equals(q.getType())) {
                Map<String, String> options = new LinkedHashMap<>();
                options.put("T", "正确");
                options.put("F", "错误");
                qmap.put("options", options);
            }
            questionList.add(qmap);
        }
        result.put("questions", questionList);
        return result;
    }

    private List<Question> randomPick(List<Question> pool, int count) {
        if (pool.size() <= count) return new ArrayList<>(pool);
        List<Question> copy = new ArrayList<>(pool);
        Collections.shuffle(copy);
        return copy.subList(0, count);
    }

    /** 提交答案并评分 */
    @Transactional
    public Map<String, Object> submit(Long studentId, SubmitRequest req) {
        ExamRecord record = recordRepository.findById(req.getRecordId())
            .orElseThrow(() -> new RuntimeException("考试记录不存在"));
        if (!record.getStudentId().equals(studentId)) {
            throw new RuntimeException("无权操作");
        }
        if ("SUBMITTED".equals(record.getStatus())) {
            throw new RuntimeException("已提交过");
        }

        // 保存答案
        try {
            record.setAnswers(objectMapper.writeValueAsString(req.getAnswers()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("答案格式错误");
        }

        // 用已存的题目ID查题判分
        List<Long> qids;
        try {
            qids = objectMapper.readValue(record.getQuestionIds(), new TypeReference<List<Long>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("题目ID解析失败");
        }

        List<Question> questions = questionRepository.findAllById(qids);
        Map<Long, Question> qmap = new HashMap<>();
        for (Question q : questions) qmap.put(q.getId(), q);

        int objectiveScore = 0;
        List<Map<String, Object>> details = new ArrayList<>();
        Map<Long, String> answerMap = new HashMap<>();
        for (SubmitRequest.AnswerItem item : req.getAnswers()) {
            answerMap.put(item.getQuestionId(), item.getAnswer());
        }

        for (Long qid : qids) {
            Question q = qmap.get(qid);
            String studentAnswer = answerMap.getOrDefault(qid, "");
            Map<String, Object> d = new HashMap<>();
            d.put("questionId", qid);
            d.put("yourAnswer", studentAnswer);
            d.put("correctAnswer", q.getAnswer());
            d.put("content", q.getContent());
            d.put("type", q.getType());

            if ("ESSAY".equals(q.getType())) {
                d.put("correct", null);
                d.put("score", 0);
                d.put("remark", "待批改");
            } else if ("BLANK".equals(q.getType())) {
                boolean ok = q.getAnswer().trim()
                    .equals(studentAnswer != null ? studentAnswer.trim() : "");
                d.put("correct", ok);
                d.put("score", ok ? q.getScore() : 0);
                if (ok) objectiveScore += q.getScore();
            } else if ("JUDGE".equals(q.getType())) {
                boolean ok = q.getAnswer().equals(studentAnswer);
                d.put("correct", ok);
                d.put("score", ok ? q.getScore() : 0);
                if (ok) objectiveScore += q.getScore();
            } else if ("MULTI_CHOICE".equals(q.getType())) {
                Set<String> correctSet = new HashSet<>(Arrays.asList(q.getAnswer().split(",")));
                Set<String> studentSet = new HashSet<>();
                if (studentAnswer != null && !studentAnswer.isBlank()) {
                    studentSet.addAll(Arrays.asList(studentAnswer.split(",")));
                }
                boolean ok = correctSet.equals(studentSet);
                d.put("correct", ok);
                d.put("score", ok ? q.getScore() : 0);
                if (ok) objectiveScore += q.getScore();
            } else {
                boolean ok = q.getAnswer().equals(studentAnswer);
                d.put("correct", ok);
                d.put("score", ok ? q.getScore() : 0);
                if (ok) objectiveScore += q.getScore();
            }
            details.add(d);
        }

        record.setScore(objectiveScore);
        record.setStatus("SUBMITTED");
        record.setSubmitTime(LocalDateTime.now());
        recordRepository.save(record);

        Map<String, Object> result = new HashMap<>();
        result.put("totalScore", paperRepository.findById(record.getPaperId()).get().getTotalScore());
        result.put("objectiveScore", objectiveScore);
        result.put("details", details);
        return result;
    }

    /** 错题本 */
    public List<Map<String, Object>> getWrongBook(Long studentId) {
        List<ExamRecord> records = recordRepository.findByStudentIdAndStatus(studentId, "SUBMITTED");
        List<Map<String, Object>> wrongList = new ArrayList<>();
        for (ExamRecord rec : records) {
            ExamPaper paper = paperRepository.findById(rec.getPaperId()).orElse(null);
            if (paper == null) continue;
            List<Long> qids;
            try {
                qids = objectMapper.readValue(rec.getQuestionIds(), new TypeReference<List<Long>>() {});
            } catch (JsonProcessingException e) { continue; }
            List<Question> questions = questionRepository.findAllById(qids);
            Map<Long, Question> qmap = new HashMap<>();
            for (Question q : questions) qmap.put(q.getId(), q);
            try {
                SubmitRequest.AnswerItem[] items = objectMapper.readValue(
                    rec.getAnswers(), SubmitRequest.AnswerItem[].class);
                for (SubmitRequest.AnswerItem item : items) {
                    Question q = qmap.get(item.getQuestionId());
                    if (q == null || "ESSAY".equals(q.getType())) continue;
                    boolean ok;
                    if ("BLANK".equals(q.getType())) {
                        ok = q.getAnswer().trim().equals(item.getAnswer() != null ? item.getAnswer().trim() : "");
                    } else if ("MULTI_CHOICE".equals(q.getType())) {
                        Set<String> correctSet = new HashSet<>(Arrays.asList(q.getAnswer().split(",")));
                        Set<String> studentSet = new HashSet<>();
                        if (item.getAnswer() != null && !item.getAnswer().isBlank()) {
                            studentSet.addAll(Arrays.asList(item.getAnswer().split(",")));
                        }
                        ok = correctSet.equals(studentSet);
                    } else {
                        ok = q.getAnswer().equals(item.getAnswer());
                    }
                    if (!ok) {
                        Map<String, Object> w = new HashMap<>();
                        w.put("paperTitle", paper.getTitle());
                        w.put("content", q.getContent());
                        w.put("yourAnswer", item.getAnswer());
                        w.put("correctAnswer", q.getAnswer());
                        w.put("type", q.getType());
                        w.put("subject", q.getSubject());
                        w.put("explanation", q.getExplanation() != null ? q.getExplanation() : "");
                        if ("CHOICE".equals(q.getType()) || "MULTI_CHOICE".equals(q.getType())) {
                            Map<String, String> opts = new LinkedHashMap<>();
                            opts.put("A", q.getOptionA());
                            opts.put("B", q.getOptionB());
                            opts.put("C", q.getOptionC());
                            opts.put("D", q.getOptionD());
                            w.put("options", opts);
                        } else if ("JUDGE".equals(q.getType())) {
                            Map<String, String> opts = new LinkedHashMap<>();
                            opts.put("T", "正确");
                            opts.put("F", "错误");
                            w.put("options", opts);
                        }
                        wrongList.add(w);
                    }
                }
            } catch (JsonProcessingException e) {}
        }
        return wrongList;
    }

    /** 成绩统计 */
    public List<Map<String, Object>> getStats(Long studentId) {
        List<ExamRecord> records = recordRepository.findByStudentIdAndStatus(studentId, "SUBMITTED");
        // 按科目合并
        Map<String, List<Integer>> subjectScores = new LinkedHashMap<>();
        for (ExamRecord rec : records) {
            ExamPaper paper = paperRepository.findById(rec.getPaperId()).orElse(null);
            if (paper == null) continue;
            subjectScores.computeIfAbsent(paper.getSubject(), k -> new ArrayList<>()).add(rec.getScore());
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> e : subjectScores.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("subject", e.getKey());
            item.put("scores", e.getValue());
            int avg = (int) e.getValue().stream().mapToInt(Integer::intValue).average().orElse(0);
            item.put("avg", avg);
            result.add(item);
        }
        return result;
    }

    /** 查看成绩 */
    public Map<String, Object> getRecord(Long studentId, Long recordId) {
        ExamRecord record = recordRepository.findById(recordId)
            .orElseThrow(() -> new RuntimeException("记录不存在"));
        if (!record.getStudentId().equals(studentId)) {
            throw new RuntimeException("无权查看");
        }

        List<Long> qids;
        try {
            qids = objectMapper.readValue(record.getQuestionIds(), new TypeReference<List<Long>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("题目ID解析失败");
        }

        List<Question> questions = questionRepository.findAllById(qids);
        Map<Long, Question> qmap = new HashMap<>();
        for (Question q : questions) qmap.put(q.getId(), q);

        Map<String, Object> result = new HashMap<>();
        result.put("totalScore", paperRepository.findById(record.getPaperId()).get().getTotalScore());
        result.put("objectiveScore", record.getScore());
        result.put("submitTime", record.getSubmitTime() != null ? record.getSubmitTime().toString() : "");

        List<Map<String, Object>> details = new ArrayList<>();
        try {
            SubmitRequest.AnswerItem[] items = objectMapper.readValue(
                record.getAnswers(), SubmitRequest.AnswerItem[].class);
            for (SubmitRequest.AnswerItem item : items) {
                Question q = qmap.get(item.getQuestionId());
                if (q == null) continue;
                Map<String, Object> d = new HashMap<>();
                d.put("questionId", item.getQuestionId());
                d.put("yourAnswer", item.getAnswer());
                d.put("correctAnswer", q.getAnswer());
                d.put("content", q.getContent());
                d.put("type", q.getType());
                d.put("score", q.getScore());
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
                if ("ESSAY".equals(q.getType())) {
                    d.put("correct", null);
                    d.put("remark", "待批改");
                } else if ("BLANK".equals(q.getType())) {
                    d.put("correct", q.getAnswer().trim()
                        .equals(item.getAnswer() != null ? item.getAnswer().trim() : ""));
                } else if ("MULTI_CHOICE".equals(q.getType())) {
                    Set<String> correctSet = new HashSet<>(Arrays.asList(q.getAnswer().split(",")));
                    Set<String> studentSet = new HashSet<>();
                    if (item.getAnswer() != null && !item.getAnswer().isBlank()) {
                        studentSet.addAll(Arrays.asList(item.getAnswer().split(",")));
                    }
                    d.put("correct", correctSet.equals(studentSet));
                } else {
                    d.put("correct", q.getAnswer().equals(item.getAnswer()));
                }
                details.add(d);
            }
        } catch (JsonProcessingException e) {}
        result.put("details", details);
        return result;
    }
}
