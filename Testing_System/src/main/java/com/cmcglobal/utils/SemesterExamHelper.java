package com.cmcglobal.utils;

import java.util.HashMap;

public class SemesterExamHelper {
	
	public static HashMap<Integer, String> statusSemesterExam(){
		HashMap<Integer, String>  status = new HashMap<>();
		status.put(0, "Draft");
		status.put(1, "Public");
		status.put(2, "Done");
		return status;
	}
	public static String[] classificationSemesterExam(){
		String[] classification = {"Kém","Trung bình","Khá","Giỏi"};
		return classification;
	}
	public static float[] startMarkclassification(){
		float[] start_mark = {0,5,7,9};
		return start_mark;
	}
	public static float[] endMarkclassification(){
		float[] end_mark = {5,7,9,11};
		return end_mark;
	}
}
