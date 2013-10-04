package edu.cmu.deiis.cpe;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;


import edu.cmu.deiis.types.AnswerScore;
import edu.cmu.deiis.types.Evaluation;
import edu.cmu.deiis.types.Question;

public class CasConsumer extends CasConsumer_ImplBase {

	private double totalPrecision;
	private double numberOfDoc;

	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		totalPrecision = 0;
		numberOfDoc = 0;
	}

	public void destroy() {
		System.out.println("Average Precision:" + totalPrecision / numberOfDoc);
	}

	@Override
	public void processCas(CAS aCas) throws ResourceProcessException {
		// Read the Evaluator index and print out the precision
		JCas aJCas;
		try {
			aJCas = aCas.getJCas();
		} catch (CASException e) {
			throw new ResourceProcessException(e);
		}
		FSIndex questionIndex = aJCas.getAnnotationIndex(Question.type);
		Iterator questionIter = questionIndex.iterator();
		
		// Print question
		Question question = (Question) questionIter.next();
		System.out.println(question.getCoveredText().trim());

		// Print all Answers
		FSIndex answerScoreIndex = aJCas.getAnnotationIndex(AnswerScore.type);
		Iterator answerScoreIter = answerScoreIndex.iterator();
		AnswerScore answerScore = null;
		List<AnswerScore> AnswerList = new ArrayList<AnswerScore>();
		
		while (answerScoreIter.hasNext()) {
			answerScore = (AnswerScore) answerScoreIter.next(); 
			if (answerScore.getAnswer().getIsCorrect())
				System.out.println("+ " + answerScore.getScore() + " "
						+ answerScore.getAnswer().getCoveredText().trim());
			else
				System.out.println("- " + answerScore.getScore() + " "
						+ answerScore.getAnswer().getCoveredText().trim());
			
		}
		// Print Final Evaluation
		FSIndex evaluationIndex = aJCas.getAnnotationIndex(Evaluation.type);
		Iterator evaluationIter = evaluationIndex.iterator();
		if (evaluationIter.hasNext()) {
			Evaluation evl = (Evaluation) evaluationIter.next();
			System.out.println("Precision at " + evl.getN() + ":"
					+ evl.getPrecision() + "\n");
			totalPrecision += evl.getPrecision();
			numberOfDoc++;
		} else
			System.out.println("No Evaluation present");

	}

}