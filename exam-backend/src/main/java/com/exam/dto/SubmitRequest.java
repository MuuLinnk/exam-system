package com.exam.dto;

import java.util.List;

public class SubmitRequest {
    private Long recordId;
    private List<AnswerItem> answers;

    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }

    public List<AnswerItem> getAnswers() { return answers; }
    public void setAnswers(List<AnswerItem> answers) { this.answers = answers; }

    public static class AnswerItem {
        private Long questionId;
        private String answer;

        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }

        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
    }
}
