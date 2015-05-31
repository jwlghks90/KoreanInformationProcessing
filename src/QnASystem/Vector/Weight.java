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
				//���� 0 / ���� 1 / �ܷ��� 2 / �ǹ� 3
				question.set�з�����(Classifiactor.spacingScoreCal(question.getQuestion()), 0);
				question.set�з�����(Classifiactor.�����˻�(question.getQuestion()), 1);
				question.set�з�����(Classifiactor.�ܷ���˻�(question.getQuestion()), 2);
				question.set�з�����(Classifiactor.meanScoreCal(question.getQuestion()), 3);
				question.set�з�����(Classifiactor.grammarScoreCal(question.getQuestion()), 4);

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
				//���� 0 / ���� 1 / �ܷ��� 2 / �ǹ� 3
				question.set�з�����(Classifiactor.spacingScoreCal(question.getQuestion()), 0);
				question.set�з�����(Classifiactor.�����˻�(question.getQuestion()), 1);
				question.set�з�����(Classifiactor.�ܷ���˻�(question.getQuestion()), 2);
				question.set�з�����(Classifiactor.meanScoreCal(question.getQuestion()), 3);
				question.set�з�����(Classifiactor.grammarScoreCal(question.getQuestion()), 4);

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
			
			System.out.println(String.format("%d/%d �м��Ϸ�", i++, morphemeList.size()));
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
		
		System.out.println("��������\t" + docSetSize);
		
		morphIDFmap.put(new Morpheme(DOC_SET_SIZE, DOC_SET_SIZE_TAG), (double)docSetSize);
		
		for(i=0; i<keyList.size(); i++)
		{
			System.out.println(keyList.get(i).getMorph() + "\t" + keyList.get(i).getTag() + "\t" + valueList.get(i));
		}
		
		return morphIDFmap;
	}
	
	/**
	 * �޼ҵ� getTFMap()
	 * �ϳ��� ������ ���¼� ����Ʈ�� input�� ������
	 * �� term�� �󵵸� ����ؼ� HashMap���� ����;
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
	 * @param tfMap : getTFMap()�� ���Ͽ� ���� term�� �󵵼� Map
	 * @param term : TF*IDF���� ��� ���ϴ� term (String)
	 * @return TF*IDF �Ǽ� ��;
	 */
	public static double getTFIDF(HashMap<Morpheme, Integer> morphTFmap
							,HashMap<Morpheme, Double> morphIDFmap, Morpheme term)
	{
		return getTF(morphTFmap, term) * getIDF(morphIDFmap, term);
	}
	
	/**
	 * @param tfMap : getTFMap()�� ���Ͽ� ���� term�� �󵵼� Map
	 * @param term : TF���� ��� ���ϴ� term (String)
	 * @return TF �Ǽ� ��;
	 */
	public static double getTF(HashMap<Morpheme, Integer> morphTFmap, Morpheme term)
	{
		int max = Collections.max(morphTFmap.values()); //�ִ� TFã��
		return computeTF(morphTFmap.get(term), max);
	}
	
	/**
	 * �̸� �м��� idfMap�� Ȱ���Ͽ�
	 * �ش� term�� idf���� ����;
	 * @param term : idf���� ���� term(String)
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
        
        Collections.reverse(list); // �ּ� ����� ����
        return list;
    }
}