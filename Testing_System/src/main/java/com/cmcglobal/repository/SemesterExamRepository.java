package com.cmcglobal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cmcglobal.entity.SemesterExam;

@Repository
public interface SemesterExamRepository extends JpaRepository<SemesterExam, String> {
//	@Query(value = "SELECT * FROM semester_exam as a JOIN semester_exam_code as b ON a.semester_exam_id = b.semester_exam_id where concat(a.semester_exam_name, b.semester_exam_codecol) like %:keyword%", nativeQuery = true)
	List<SemesterExam> findByNameContaining(String keyword);

	@Query(value = "select * from semester_exam p where p.semester_exam_name like %:name%", nativeQuery = true)
	List<SemesterExam> filterByAll(String name);
	
	List<SemesterExam> findAllByOrderByCreatedDateDesc();
	
}
