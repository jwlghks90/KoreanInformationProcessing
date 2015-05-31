package QnASystem;

import java.io.Serializable;
import java.util.ArrayList;

public class Morpheme implements Serializable
{
	private String morph;
	private String tag;
	private int index;
	private double score;

	public Morpheme(){}
	public Morpheme(String morph, String tag)
	{
		this.morph = morph;
		this.tag = tag;
	}
	public Morpheme(String morph, String tag, double score)
	{
		this.morph = morph;
		this.tag = tag;
		this.score = score;
	}
	
	public Morpheme(String morph, String tag, int index)
	{
		this.morph = morph;
		this.tag = tag;
		this.index = index;
	}
	
	public ArrayList<Morpheme> nounSplitter(Morpheme divider)
	{
		ArrayList<Morpheme> nounList = new ArrayList<Morpheme>(2);
		
		String div = divider.morph;
		int index = morph.indexOf(div);

		String noun[] = morph.split(div);
		
		if(index == 0)
		{
			nounList.add(new Morpheme(div, tag));
			if(noun.length == 2)
				nounList.add(new Morpheme(noun[1], tag));
		}
		else
		{
			nounList.add(new Morpheme(noun[0], tag));
			nounList.add(new Morpheme(div, tag));
		}
		
		return nounList;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        
		Morpheme target = (Morpheme)obj;
		
		if(this.morph.equals(target.morph))
		{
			if(this.tag.substring(0, 1).equals(target.tag.substring(0, 1)))
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(morph);
		sb.append("(");
		sb.append(tag);
		sb.append(") ");
		
		return sb.toString();
	}
	
	@Override
	public int hashCode()
	{
        return morph.hashCode() * tag.substring(0,1).hashCode();
	}
	
	public String getMorph(){return morph;}
	public String getTag(){return tag;}
	public void setMorph(String morph){this.morph = morph;}
	public void setTag(String tag){this.tag = tag;}
	public double getScore(){return score;}
	public void setScore(double score){this.score = score;}
	public int getIndex(){return index;}
	public void setIndex(int index){this.index = index;}
}
