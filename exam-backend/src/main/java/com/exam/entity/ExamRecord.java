package com.exam.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_record")
public class ExamRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "paper_id", nullable = false)
    private Long paperId;

    @Column(columnDefinition = "TEXT")
    private String answers;  // 学生答案 JSON

    @Column(name = "question_ids", columnDefinition = "TEXT")
    private String questionIds;  // 本次抽到的题目ID列表 JSON

    private Integer score = 0;

    @Column(length = 10)
    private String status;  // DOING / SUBMITTED

    @Column(name = "start_time")
    private LocalDateTime startTime = LocalDateTime.now();

    @Column(name = "submit_time")
    private LocalDateTime submitTime;

    public ExamRecord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getPaperId() { return paperId; }
    public void setPaperId(Long paperId) { this.paperId = paperId; }

    public String getAnswers() { return answers; }
    public void setAnswers(String answers) { this.answers = answers; }

    public String getQuestionIds() { return questionIds; }
    public void setQuestionIds(String questionIds) { this.questionIds = questionIds; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getSubmitTime() { return submitTime; }
    public void setSubmitTime(LocalDateTime submitTime) { this.submitTime = submitTime; }
}
