package QnASystem.Vector;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import Kokoma.Kokoma;
import QnASystem.FileIO;
import QnASystem.Morpheme;
import QnASystem.Question;
import QnASystem.Classification.Classifiactor;

public class Weight
{
	private int TF;
	private int IDF;
	
	public static HashMap<Morpheme, Double> morphIDFmap;
	public static ArrayList<Question> qList;
	public static final String DOC_SET_SIZE = "docSetSize";
	public static final String DOC_SET_SIZE_TAG = "key";

	public Weight(){}
	
	public static double computeTF(int tf, int max)
	{
		return tf;
		//return Math.log(1 + tf);
		//return (double)tf/(double)max;
	}
	
	public static double computeIDF(int docSetSize, int df)
	{
		double idf = Math.log((double)(docSetSize/df));
		return idf; // IDF
	}
	
	
	public static ArrayList<Question> getQuestionList(int startFileNo, int endFileNo, Kokoma kokoma)
	{
		ArrayList<Question> qList = new ArrayList<Question>();
		
		StringBuilder fileNameCache = new StringBuilder();
		for(int i=startFileNo; i<=endFileNo; i++)
		{
			fileNameCache.setLength(0);
			fileNameCache.append(FileIO.Data_PATH + "allData/");
			fileNameCache.append(i);
			fileNameCache.append(".xml");
			System.out.println(fileNameCache.toString());
			Question question;
			try
			{
				question = FileIO.getQuestionFromFile(fileNameCache.toString());
				//띄어쓰기 0 / 발음 1 / 외래어 2 / 의미 3
				question.set분류점수(Classifiactor.spacingScoreCal(question.getQuestion()), 0);
				question.set분류점수(Classifiactor.발음검사(question.getQuestion()), 1);
				question.set분류점수(Classifiactor.외래어검사(question.getQuestion()), 2);
				question.set분류점수(Classifiactor.meanScoreCal(question.getQuestion()), 3);
				question.set분류점수(Classifiactor.grammarScoreCal(question.getQuestion()), 4);

			} catch (FileNotFoundException e)
			{
				continue;
			}
			question.setMorphemes(kokoma.getPhraseList(question.getQuestion()));
			qList.add(question);
		}
		
		return qList;
	}
	public static ArrayList<Question> getQuestionListForTest(int startFileNo, int endFileNo)
	{
		ArrayList<Question> qList = new ArrayList<Question>();
		
		StringBuilder fileNameCache = new StringBuilder();
		for(int i=startFileNo; i<=endFileNo; i++)
		{
			fileNameCache.setLength(0);
			fileNameCache.append(FileIO.Data_PATH + "sampleData/");
			fileNameCache.append(i);
			fileNameCache.append(".txt");
			System.out.println(fileNameCache.toString());
			Question question;
			try
			{
				question = FileIO.getQuestionFromFile(fileNameCache.toString());
				//띄어쓰기 0 / 발음 1 / 외래어 2 / 의미 3
				question.set분류점수(Classifiactor.spacingScoreCal(question.getQuestion()), 0);
				question.set분류점수(Classifiactor.발음검사(question.getQuestion()), 1);
				question.set분류점수(Classifiactor.외래어검사(question.getQuestion()), 2);
				question.set분류점수(Classifiactor.meanScoreCal(question.getQuestion()), 3);
				question.set분류점수(Classifiactor.grammarScoreCal(question.getQuestion()), 4);

			}
			catch (FileNotFoundException e){continue;}
			qList.add(question);
		}
		
		return qList;
	}
	
	public static ArrayList<Question> getQuestionList(int startFileNo, int endFileNo)
	{
		ArrayList<Question> qList = new ArrayList<Question>();
		
		StringBuilder fileNameCache = new StringBuilder();
		for(int i=startFileNo; i<=endFileNo; i++)
		{
			fileNameCache.setLength(0);
			fileNameCache.append(FileIO.Data_PATH);
			fileNameCache.append(i);
			fileNameCache.append(".xml");
			System.out.println(fileNameCache.toString());
			Question question;
			try
			{
				question = FileIO.getQuestionFromFile(fileNameCache.toString());
			} catch (FileNotFoundException e)
			{
				continue;
			}
			qList.add(question);
		}
		
		return qList;
	}
	
	public static HashMap<Morpheme, Double> getMorphIDFMap(ArrayList<Question> qList)
	{
		morphIDFmap = new HashMap<Morpheme, Double>();

		ArrayList<Morpheme> morphemeList = new ArrayList<Morpheme>();
	
		int docSetSize = qList.size();

		for(Question document : qList)
		{
			morphemeList.addAll(document.getMorphemes());
		}
		
		int i=1;
		for(Morpheme term : morphemeList)
		{
			int df = 1;
			for(Question x : qList)
			{
				if(x.getMorphemes().contains(term))
					df++;
			}
			
			morphIDFmap.put(term, computeIDF(docSetSize, df));
			
			System.out.println(String.format("%d/%d 분석완료", i++, morphemeList.size()));
		}
		
		Iterator<Morpheme> it = sortByValue(morphIDFmap).iterator();
		ArrayList<Morpheme> keyList = new ArrayList<Morpheme>();
		ArrayList<Double> valueList = new ArrayList<Double>();
		while(it.hasNext())
		{
			Morpheme temp = it.next();
            keyList.add(temp);
            valueList.add((double)(morphIDFmap.get(temp)));
        }
		
		System.out.println("문서개수\t" + docSetSize);
		
		morphIDFmap.put(new Morpheme(DOC_SET_SIZE, DOC_SET_SIZE_TAG), (double)docSetSize);
		
		for(i=0; i<keyList.size(); i++)
		{
			System.out.println(keyList.get(i).getMorph() + "\t" + keyList.get(i).getTag() + "\t" + valueList.get(i));
		}
		
		return morphIDFmap;
	}
	
	/**
	 * 메소드 getTFMap()
	 * 하나의 문서의 형태소 리스트를 input에 넣으면
	 * 각 term의 빈도를 계산해서 HashMap으로 리턴;
	 */
	public static HashMap<Morpheme, Integer> getMorphTFMap(ArrayList<Morpheme> termList)
	{
		HashMap<Morpheme, Integer> morphTermMap = new HashMap<Morpheme, Integer>();

		for(Morpheme x : termList)
		{
			if(morphTermMap.containsKey(x))
			{
				Integer count = (Integer)morphTermMap.get(x);
				morphTermMap.put(x, new Integer(count.intValue() + 1));
			}

			else
				morphTermMap.put(x, new Integer(1));
		}
		
		return morphTermMap;
	}
	

	/**
	 * @param tfMap : getTFMap()을 통하여 얻은 term의 빈도수 Map
	 * @param term : TF*IDF값을 얻기 원하는 term (String)
	 * @return TF*IDF 실수 값;
	 */
	public static double getTFIDF(HashMap<Morpheme, Integer> morphTFmap
							,HashMap<Morpheme, Double> morphIDFmap, Morpheme term)
	{
		return getTF(morphTFmap, term) * getIDF(morphIDFmap, term);
	}
	
	/**
	 * @param tfMap : getTFMap()을 통하여 얻은 term의 빈도수 Map
	 * @param term : TF값을 얻기 원하는 term (String)
	 * @return TF 실수 값;
	 */
	public static double getTF(HashMap<Morpheme, Integer> morphTFmap, Morpheme term)
	{
		int max = Collections.max(morphTFmap.values()); //최대 TF찾기
		return computeTF(morphTFmap.get(term), max);
	}
	
	/**
	 * 미리 분석한 idfMap을 활용하여
	 * 해당 term의 idf값을 리턴;
	 * @param term : idf값을 얻을 term(String)
	 */
	public static double getIDF(HashMap<Morpheme, Double> morphIDFmap, Morpheme term)
	{
		Double idf = morphIDFmap.get(term);
		
		if(idf != null)
			return idf.doubleValue();
		else
			return computeIDF(morphIDFmap.get(new Morpheme(DOC_SET_SIZE, DOC_SET_SIZE_TAG)).intValue(), 1);
	}
	
	public static ArrayList sortByValue(final HashMap map)
	{
		ArrayList<String> list = new ArrayList();
		list.addAll(map.keySet());

		Collections.sort(list, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				Object v1 = map.get(o1);
				Object v2 = map.get(o2);

				return ((Comparable) v1).compareTo(v2);
			}

		});
        
        Collections.reverse(list); // 주석 지우면 역순
        return list;
    }
}