package com.exam.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(length = 20, nullable = false)
    private String type;  // CHOICE / MULTI_CHOICE / JUDGE / BLANK / ESSAY

    @Column(name = "option_a", length = 200)
    private String optionA;

    @Column(name = "option_b", length = 200)
    private String optionB;

    @Column(name = "option_c", length = 200)
    private String optionC;

    @Column(name = "option_d", length = 200)
    private String optionD;

    @Column(length = 500, nullable = false)
    private String answer;

    private Integer score = 5;

    @Column(length = 10)
    private String difficulty;  // EASY / MEDIUM / HARD

    @Column(length = 30)
    private String subject;  // 所属科目：Java / 高数 / 英语 / 数据结构

    @Column(columnDefinition = "TEXT")
    private String explanation;  // 题目解析

    public Question() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }

    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }

    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }

    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}
