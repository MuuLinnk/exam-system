package com.exam.repository;

import com.exam.entity.ExamPaper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamPaperRepository extends JpaRepository<ExamPaper, Long> {
}
