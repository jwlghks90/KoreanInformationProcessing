package QnASystem.Vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import QnASystem.Morpheme;

public class Vector implements Serializable
{	
	private Element[] morphVector;
	
	public Vector(){}

	public static double getCosSimilarity(Vector doc1, Vector doc2)
	{
		double cosSimilarity = 0f;
		
		Element[][] docsMorphVectors = {doc1.morphVector, doc2.morphVector};
		
		int shorter = docsMorphVectors[0].length <= docsMorphVectors[1].length? 0 : 1;
		int longer = 1 - shorter;
		int shortersLength = docsMorphVectors[shorter].length;
		int longersLength = docsMorphVectors[longer].length;
		
		double childScore = 0;
		
		for(int i=0; i < shortersLength; i++)
		{
			for(int j=0; j < longersLength; j++)
			{
				if(docsMorphVectors[shorter][i].element.equals(docsMorphVectors[longer][j].element))
				{
					childScore += docsMorphVectors[shorter][i].score * docsMorphVectors[longer][j].score;
				}
			}
		}

		double motherX = 0, motherY = 0;
		for(Element x : docsMorphVectors[0])
		{
			double squareX = 0;
			squareX += x.score * x.score + 1;
			motherX = Math.sqrt(squareX);
		}
		for(Element y : docsMorphVectors[1])
		{
			double squareY = 0;
			squareY += y.score * y.score + 1;
			motherY = Math.sqrt(squareY);
		}
		
		cosSimilarity = childScore / (motherX * motherY);
		
		return cosSimilarity;
	}

	public static Vector getVector(HashMap<Morpheme, Double> morphIDFmap, ArrayList<Morpheme> morphemes)
	{
		Vector instance = new Vector();
		HashMap<Morpheme, Integer> morphTFmap = Weight.getMorphTFMap(morphemes);
		
		int i=0;
		int mSize = morphTFmap.size();
		instance.morphVector = new Element[mSize];
	
		for(Morpheme x : morphTFmap.keySet())
		{
			double TFIDF = Weight.getTFIDF(morphTFmap, morphIDFmap, x);
			
			instance.morphVector[i++] = new Element(x, morphTFmap.get(x), TFIDF);
		}
		
		return instance;
	}
	
	public Element[] getMorphVector(){return morphVector;}
	public void setMorphVector(Element[] morphVector){this.morphVector = morphVector;}
	
	@Override
	public String toString()
	{
		StringBuilder v = new StringBuilder("morph : {");
		for(Element e : morphVector)
		{
			v.append(e.toString());
			v.append(", ");
		}
		v.append("}");

		return v.toString();
	}
}

class Element implements Serializable
{
	Morpheme element;
	int tf;
	double score;
	
	public Element(Morpheme m, double s)
	{
		element = m;
		score = s;
	}
	
	public Element(Morpheme m, int tf, double s)
	{
		element = m;
		this.tf = tf;
		score = s;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		Element target = (Element) obj;
		if (element.equals(target.element))
			return true;
		return false;
	}
	
	@Override
	public String toString()
	{
		return String.format("(%s(%s), %d, %f)", element.getMorph(), element.getTag(), tf, score);
	}
}
