package com.cmcglobal.service.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cmcglobal.entity.Candidate;
import com.cmcglobal.entity.CandidateTest;
import com.cmcglobal.entity.Exam;
import com.cmcglobal.entity.SemesterExam;
import com.cmcglobal.entity.SemesterInformation;
import com.cmcglobal.entity.Test;
import com.cmcglobal.entity.User;
import com.cmcglobal.repository.CandidateRepository;
import com.cmcglobal.repository.CandidateTestRepository;
import com.cmcglobal.repository.ExamRepository;
import com.cmcglobal.repository.SemesterExamRepository;
import com.cmcglobal.repository.TestRepository;
import com.cmcglobal.repository.UserRepository;
import com.cmcglobal.service.SemesterExamService;
import com.cmcglobal.service.ServiceResult;
import com.cmcglobal.service.ServiceResult.Status;
import com.cmcglobal.utils.SemesterExamHelper;

@Service
public class SemesterExamServiceImpl implements SemesterExamService {

	@Autowired
	EntityManager entityManager;

	@Autowired
	private SemesterExamRepository examRepository;

	@Autowired
	CandidateRepository candidateRepository;
	@Autowired
	UserRepository userRepository;

	@Autowired
	TestRepository testRepository;
	@Autowired
	ExamRepository exam_repository;
	@Autowired
	CandidateTestRepository candidateTestRepository;

	@Override
	public ServiceResult getAllSemesterExam() {
		ServiceResult result = new ServiceResult();
		result.setTotalRecord((int) examRepository.count());
		result.setData(examRepository.findAllByOrderByCreatedDateDesc());
		return result;
	}

	@Override
	public ServiceResult create(SemesterExam semesterExam) {
		ServiceResult result = new ServiceResult();
		result.setData(examRepository.save(semesterExam));
		return result;
	}

	@Override
	public ServiceResult search(String keyword) {
		ServiceResult result = new ServiceResult();
		result.setData(examRepository.findByNameContaining(keyword));
		return result;
	}

	@Override
	public ServiceResult delete(String id) {
		ServiceResult result = new ServiceResult();
		SemesterExam semesterExam = examRepository.findById(id).orElse(null);
		if (semesterExam == null) {
			result.setMessage("Not Found object");
			result.setStatus(Status.FAILED);
		} else {
			examRepository.delete(semesterExam);
			result.setStatus(Status.SUCCESS);
			result.setData(examRepository.findAllByOrderByCreatedDateDesc());
			result.setTotalRecord((int) examRepository.count());
		}
		return result;
	}

	@Override
	public ServiceResult findById(String id) {
		ServiceResult result = new ServiceResult();
		result.setData(examRepository.findById(id));
		return result;
	}

	@Override
	public ServiceResult filter(String name, Integer status, String fullname, Date startTime, Date endTime) {
		System.err.println("THUAN: " + name);
		ServiceResult result = new ServiceResult();
		int check = 0;

		StringBuilder stringBuilder = new StringBuilder("SELECT s FROM SemesterExam s ");

		if (!"null".equals(name)) {
			stringBuilder.append("WHERE s.name like '%" + name + "%'");
			check++;
		}

		if (status != -1) {
			if (check == 0) {
				stringBuilder.append("WHERE s.status like '%" + status + "%'");
				check++;
			} else {
				stringBuilder.append("and s.status like '%" + status + "%'");
			}
		}

		if (!"null".equals(fullname)) {
			if (check == 0) {
				stringBuilder.append("WHERE s.user.fullName like '%" + fullname + "%'");
				check++;
			} else {
				stringBuilder.append("and s.user.fullName like '%" + fullname + "%'");
			}
		}

		Query query = entityManager.createQuery(stringBuilder.toString(), SemesterExam.class);
		List<SemesterExam> lists = query.getResultList();
		result.setData(lists);
		result.setTotalRecord(lists.size());
		return result;
	}

	/**
	 * Create by: pvhao - CMC
	 * Create date: Feb 18, 2019
	 * Modifier: User
	 * Modified date: Feb 18, 2019
	 * Description: ....
	 * Version 1.0
	 * 
	 * @param id
	 * @return
	 */
	public SemesterInformation getInformationOfSemester(String id) {

		SemesterInformation semesterInformation = null;
		try {
			if (examRepository.existsById(id)) {
				semesterInformation = new SemesterInformation();
				SemesterExam semesterExam = examRepository.findById(id).get();
				List<Candidate> list_candidate = candidateRepository.findBySemesterExam(semesterExam);
				List<Test> list_test = testRepository.findBySemesterExam(semesterExam);
				List<User> user_join = new ArrayList<User>();
				List<User> user_test = new ArrayList<User>();
				List<Exam> exams = new ArrayList<Exam>();
				int total_number_question = 0;
				int total_user_test = 0;
				float mean_mark = 0;
				float[] start_mark = SemesterExamHelper.startMarkclassification();
				float[] end_mark = SemesterExamHelper.endMarkclassification();
				int[] rate_mark = new int[SemesterExamHelper.classificationSemesterExam().length];
				for (Candidate candidate : list_candidate) {
					User user = userRepository.findById(candidate.getUser().getUserId()).get();
					user_join.add(user);
					List<CandidateTest> listCandidateTest = candidateTestRepository.findByCandidates(candidate);
					if (!listCandidateTest.isEmpty()) {
						user_test.add(candidate.getUser());
						total_user_test++;
					}
					for (CandidateTest candidateTest : listCandidateTest) {
						
						mean_mark += candidateTest.getMark();
						for (int i = 0; i < start_mark.length; i++) {
							if (start_mark[i] <= candidateTest.getMark() && candidateTest.getMark() < end_mark[i]) {
								rate_mark[i]++;
							}
						}

					}
				}
				for (Test test : list_test) {
					Exam exam = exam_repository.findById(test.getExam().getExamId()).get();
					exams.add(exam);
					total_number_question += exam.getNumberOfQuestion();
				}
				if (total_user_test > 0)
					mean_mark /= total_user_test;
				semesterInformation.setSemesterExam(semesterExam);
				semesterInformation.setStatus(
						String.valueOf(SemesterExamHelper.statusSemesterExam().get(semesterExam.getStatus())));
				semesterInformation.setTotal_number_exam(exams.size());
				semesterInformation.setTotal_number_question(total_number_question);
				semesterInformation.setTotal_user_join(user_join.size());
				semesterInformation.setTotal_user_test(total_user_test);
				semesterInformation.setMean_mark(mean_mark);
				semesterInformation.setRate_title(SemesterExamHelper.classificationSemesterExam());
				semesterInformation.setRate_mark(rate_mark);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return semesterInformation;
	}

	@Override
	public ServiceResult getSemesterListByUserId(int id) {
		ServiceResult result = new ServiceResult();
		List<SemesterExam> semesterExams = new ArrayList<SemesterExam>();
		User user = userRepository.findById(id).get();
		List<Candidate> candidates = candidateRepository.findByUser(user);
		for (Candidate candidate : candidates) {
			semesterExams.add(candidate.getSemesterExam());
		}
		result.setData(semesterExams);
		return result;
	}

}
