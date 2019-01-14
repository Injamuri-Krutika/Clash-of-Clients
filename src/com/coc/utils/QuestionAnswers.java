package com.coc.utils;

import java.util.HashMap;
import java.util.Map;

public class QuestionAnswers {
	private Map<String, String> Questions=new HashMap<String, String>();
	private Map<String, String> Answers=new HashMap<String, String>();
	public Map<String, String> getQuestions() {
		Questions.put("FFF", "Arrange the following in ascending order :99:30:60:12");
		Questions.put("QUE1", " Who was the first Prime Minister of India?\nA)Jawaharlal Nehru \tB)Gulzarilal Nanda\tC)Lal Bahadur Shastri\tD)Indira Gandhi");
		Questions.put("QUE2", "How many Cricket world cups does India have?\nA)1\tB)2\tC)3\tD)4");
		Questions.put("QUE3", "What colour symbolises peace?\nA)Blue\tB)White\tC)Green\tD)Red");
		Questions.put("QUE4", "Who wrote Romeo and Juliet?\nA)Charles Dickens\tB)William Blake\tC)William Shakespeare\tD)John Milton");
		Questions.put("QUE5", "Which of the following is the most loyal animal?\nA)DOG\tB)CAT\tC)RAT\tD)PIG");
		Questions.put("QUE6", " Which is the largest living bird on Earth?\nA)Emu\tB)Ostrich\tC)Albatross\tD)Siberian Crane");
		Questions.put("KP1", "QUESTION: What % of indians say '5 more minutes' on being woken up\n");
		Questions.put("KP2", "QUESTION: What % of indians think all heroines today look same\n");
		Questions.put("KP3", "QUESTION: What % of indian childrens say that their parents ban them from watching TV during exams\n");
		/*Questions.put("QUE6", " Which is the largest living bird on Earth?\nA)Emu\tB)Ostrich\tC)Albatross\tD)Siberian Crane");
		Questions.put("QUE6", " Which is the largest living bird on Earth?\nA)Emu\tB)Ostrich\tC)Albatross\tD)Siberian Crane");
		Questions.put("QUE6", " Which is the largest living bird on Earth?\nA)Emu\tB)Ostrich\tC)Albatross\tD)Siberian Crane");
		*/return Questions;
	}
	
	
	
	
	
	public Map<String, String> getAnswers() {
		Answers.put("FFF","12,30,60,99");
		Answers.put("ANS1","A");
		Answers.put("ANS2","B");
		Answers.put("ANS3","B");
		Answers.put("ANS4","C");
		Answers.put("ANS5","A");
		Answers.put("ANS6","B");
		Answers.put("KP1","64");
		Answers.put("KP2","48");
		Answers.put("KP3","58");
		return Answers;
	}
}
