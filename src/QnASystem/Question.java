package QnASystem;

import java.io.Serializable;
import java.util.ArrayList;


public class Question implements Serializable
{
	private int number;
	private String sim;
	private double score;

	private String question;
	private String answer;
	private ArrayList<Morpheme> morphemes;

	private double �з�����[];
	
	public Question(){}
	public Question(int number, String question, String answer, boolean doAnalyze)
	{
		this.number = number;
		this.question = question;
		this.answer = answer;

		�з����� = new double[5];
		
		if(doAnalyze)
		{
//			Hannanum analyzer = new Hannanum(FileIO.Hannanum_PATH);
//			analyzer.switchOnSimple22();
//			analyzer.activate();
//			morphemes = analyzer.getPhraseList(question);
//			
//			analyzer.close();
		}
	}
	
	public Question(int number, String sim, String question, String answer)
	{
		this.number = number;
		this.sim = sim;
		this.question = question;
		this.answer = answer;
		�з����� = new double[5];
	}
	
	public double get�з�����(int index){ return �з�����[index];}
	public void set�з�����(double ����, int index)	{ �з�����[index] = ����;}
	public String getSim(){return sim;}
	public void setSim(String sim){this.sim = sim;}
	public double getScore(){return score;}
	public void setScore(double score){this.score = score;}
	public void setMorphemes(ArrayList<Morpheme> morphemes){this.morphemes = morphemes;}
	public void setNumber(int d){this.number = d;}
	public int getNumber(){return number;}
	public ArrayList<Morpheme> getMorphemes(){return morphemes;}
	public String getQuestion(){return question;}
	public String getAnswer(){return answer;}
}