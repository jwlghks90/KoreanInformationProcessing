package Kokoma;
import java.util.ArrayList;
import java.util.List;

import org.snu.ids.ha.index.Keyword;
import org.snu.ids.ha.index.KeywordExtractor;
import org.snu.ids.ha.index.KeywordList;
import org.snu.ids.ha.ma.Eojeol;
import org.snu.ids.ha.ma.MExpression;
import org.snu.ids.ha.ma.MorphemeAnalyzer;
import org.snu.ids.ha.ma.Sentence;

import QnASystem.Morpheme;

public class Kokoma
{
	private MorphemeAnalyzer analyzer;
	
	public Kokoma()
	{
		analyzer = new MorphemeAnalyzer();
		analyzer.createLogger(null);
	}
	
	public void closeAnalyzer()
	{
		analyzer.closeLogger();
	}
	
	public ArrayList<Morpheme> getPhraseList(String document)
	{
//		String val = "[^∞°-∆R§°-§æ§ø-§”0-9a-zA-Z]+";
//		document = document.replaceAll(val, " ");
		ArrayList<Morpheme> morphList = getEssentialMorphList(document);
//		morphList = compoundNounDivider(morphList);

		return morphList;
	}
	
	public ArrayList<Morpheme> getMorphList(String document)
	{
		ArrayList<Morpheme> morphemeList = new ArrayList<Morpheme>();
		List<MExpression> ret;
		
		try
		{
			ret = analyzer.analyze(document);
			ret = analyzer.postProcess(ret);
			ret = analyzer.leaveJustBest(ret);

			List<Sentence> sentenceList = analyzer.divideToSentences(ret);
			
			for (Sentence sentence : sentenceList)
			{
				int endOfSentence = sentence.size();
				
				for(int i=0; i<endOfSentence;i++)
				{
					Eojeol eojeol = sentence.get(i);
					System.out.println(eojeol);
					for (org.snu.ids.ha.ma.Morpheme x : eojeol)
					{		
						morphemeList.add(new QnASystem.Morpheme(x.getTag(), x.getString(), x.getIndex()));
						System.out.println(x);
					}
				}
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return morphemeList;
	}
	public ArrayList<Morpheme> getEssentialMorphList(String document)
	{
		ArrayList<Morpheme> morphemeList = new ArrayList<Morpheme>();
		List<MExpression> ret;
		try
		{
			ret = analyzer.analyze(document);
			ret = analyzer.postProcess(ret);
			ret = analyzer.leaveJustBest(ret);
			
			List<Sentence> sentenceList = analyzer.divideToSentences(ret);
			
			for (Sentence sentence : sentenceList)
			{
				int endOfSentence = sentence.size()-1;
				
				for(int j=0; j<endOfSentence;j++)
				{
					Eojeol eoj = sentence.get(j);

					int length = eoj.size();
					for (int i = 0; i < length; i++)
					{
						org.snu.ids.ha.ma.Morpheme morph = eoj.get(i);
						char tagHead = morph.getTag().charAt(0);
						
						if(tagHead == 'N' || tagHead == 'V' || tagHead == 'M' || tagHead == 'J' || tagHead == 'O')//√ﬂ√‚µ…∞Õ
						{
							morphemeList.add(new Morpheme(morph.getString(), morph.getTag(), morph.getIndex()));
						}
					}
				}
			}
		} catch (Exception e){e.printStackTrace();}

		return morphemeList;
	}
	
	public ArrayList<Morpheme> compoundNounDivider(ArrayList<Morpheme> originList)
	{
		ArrayList<Morpheme> newList = new ArrayList<Morpheme>();
		
		int size = originList.size();
		
		for(Morpheme x : originList)
		{
			char tagHead = x.getTag().charAt(0);
			String curMorph = x.getMorph();
			
			if(tagHead == 'N')
			{
				for(int i=0; i<size; i++)
				{
					Morpheme noun = originList.get(i);
					String nounMorph = noun.getMorph();
					char nounTagHead = noun.getTag().charAt(0);
					if(i == size-1) newList.add(x);

					if(curMorph.length() > nounMorph.length())
					{
						if(curMorph.contains(nounMorph))
						{
							ArrayList<Morpheme> compoundNouns = x.nounSplitter(noun);
							//¬…∞µ∞Õ¿ª ∂« ¬…∞∂∞Õ¿Œ∞°?
							//compoundNouns = compoundNounDivider(compoundNouns);
							newList.add(x);
							newList.addAll(compoundNouns);
							break;
						}
					}
					if(i == size-1)
					{
						newList.add(x);
					}
				}
			}
			else
			{
				newList.add(x);
			}
		}
		
		return newList;
	}
}
