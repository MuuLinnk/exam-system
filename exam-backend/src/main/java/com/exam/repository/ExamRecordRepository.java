package com.exam.repository;

import com.exam.entity.ExamRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExamRecordRepository extends JpaRepository<ExamRecord, Long> {
    List<ExamRecord> findByStudentIdOrderByStartTimeDesc(Long studentId);
    ExamRecord findByStudentIdAndPaperIdAndStatus(Long studentId, Long paperId, String status);
    List<ExamRecord> findByStudentIdAndStatus(Long studentId, String status);
    List<ExamRecord> findByStatus(String status);
    List<ExamRecord> findByStatusAndPaperId(String status, Long paperId);
}
