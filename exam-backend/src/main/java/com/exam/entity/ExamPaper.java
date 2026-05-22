package com.exam.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_paper")
public class ExamPaper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    private Integer duration;  // 考试时长，分钟

    @Column(name = "total_score")
    private Integer totalScore;

    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(length = 30)
    private String subject;  // 对应科目

    @Column(name = "choice_count")
    private Integer choiceCount;  // 随机抽几道选择题

    @Column(name = "blank_count")
    private Integer blankCount;  // 随机抽几道填空题

    @Column(name = "essay_count")
    private Integer essayCount;  // 随机抽几道简答题

    @Column(name = "judge_count")
    private Integer judgeCount;  // 随机抽几道判断题

    @Column(name = "multi_choice_count")
    private Integer multiChoiceCount;  // 随机抽几道多选题

    public ExamPaper() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public Integer getChoiceCount() { return choiceCount; }
    public void setChoiceCount(Integer choiceCount) { this.choiceCount = choiceCount; }

    public Integer getBlankCount() { return blankCount; }
    public void setBlankCount(Integer blankCount) { this.blankCount = blankCount; }

    public Integer getEssayCount() { return essayCount; }
    public void setEssayCount(Integer essayCount) { this.essayCount = essayCount; }

    public Integer getJudgeCount() { return judgeCount; }
    public void setJudgeCount(Integer judgeCount) { this.judgeCount = judgeCount; }

    public Integer getMultiChoiceCount() { return multiChoiceCount; }
    public void setMultiChoiceCount(Integer multiChoiceCount) { this.multiChoiceCount = multiChoiceCount; }
}
