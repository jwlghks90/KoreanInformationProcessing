package QnASystem.Classification;

import org.snu.ids.ha.index.Keyword;
import org.snu.ids.ha.index.KeywordExtractor;
import org.snu.ids.ha.index.KeywordList;

public class Classifiactor
{
	// 질문에 대한 띄어쓰기 가중치 부여
	public static final int 띄어쓰기 = 100;
	public static final int 띄어쓰는 = 70;
	public static final int 띄어야 = 70;
	public static final int 띄어 = 50;
	public static final int 띄운 = 30;

	public static final int 붙여쓰기 = 100;
	public static final int 붙여쓰는 = 70;
	public static final int 붙여야 = 70;
	public static final int 붙여 = 50;
	public static final int 붙은 = 30;
	// ///////////////////////////////

	// 질문에 대한 의미 가중치 부여
	public static final int 의미 = 30;
	public static final int 차이 = 30;
	public static final int 뜻 = 30;

	public static final int 의미인가요 = 100;
	public static final int 의미는 = 65;
	public static final int 의미로 = 65;
	public static final int 의미를 = 65;

	public static final int 뜻인가요 = 100;
	public static final int 뜻은 = 65;
	public static final int 뜻을 = 65;
	public static final int 뜻으로 = 55;

	public static final int 차이점 = 60;
	public static final int 차이를 = 50;
	public static final int 차이는 = 50;
	
	public static double grammarScoreCal(String sentence) // 키워드만 이용
	{
		double keywordScore = 0;

		// 문법에 관련된 키워드
		if (sentence.matches(".*합성어.*") || sentence.matches(".*파생어.*") || sentence.matches(".*접미사.*")
				|| sentence.matches(".*형용사.*") || sentence.matches(".*동사.*") || sentence.matches(".*조사.*")
				|| sentence.matches(".*부사.*") || sentence.matches(".*어간.*") || sentence.matches(".*어미.*")
				|| sentence.matches(".*어법.*") || sentence.matches(".*문법.*") || sentence.matches(".*형태소.*")
				|| sentence.matches(".*주동.*") || sentence.matches(".*사동.*") || sentence.matches(".*능동.*")
				|| sentence.matches(".*피동.*") || sentence.matches(".*용언.*") || sentence.matches(".*관형사.*")
				|| sentence.matches(".*양성모음.*") || sentence.matches(".*자음동화.*") || sentence.matches(".*과거형.*")
				|| sentence.matches(".*의존 명사.*") || sentence.matches(".*의존명사.*"))
		{
			keywordScore = 100;
		}

		return keywordScore;
	}

	public static double 외래어검사(String question)
	{

		KeywordExtractor ke = new KeywordExtractor(); // 꼬꼬마 분석기
		double 외래어점수 = 0.0;

		// 외래어와 로마자를 포함할 경우 70점
		if (question.contains("외래어") || question.contains("로마자"))
			외래어점수 += 80.0;

		// 표기를 포함할 경우 25점
		if (question.contains("표기"))
			외래어점수 += 25.0;

		// 영어(연속4개이상 알파벳)를 포함 25점
		// 영어를 포함하면서 형태소 분석 결과 적(VV 또는 VA) 쓰(VV)가 있을 경우 25점
		// 외래어나 로마자 표기 관련 문장을 보면 적나요 적는데 쓰나요 쓰는 방법 등 많은데 형태소 분석을 하며
		if (question.matches(".*[a-zA-Z]{4,}+.*"))
		{
			외래어점수 += 25.0;
			// 꼬꼬마형태소
			KeywordList keyList = ke.extractKeyword(question, false);
			for (Keyword keyword : keyList)
			{
				if (keyword.getString().equals("적") && keyword.getTag().equals("VV") || keyword.getString().equals("적")
						&& keyword.getTag().equals("VA") || keyword.getString().equals("쓰")
						&& keyword.getTag().equals("VV"))
				{
					외래어점수 += 25.0;
					break;
				}
			}
		}

		if (외래어점수 >= 100.0)
			return 100.0;
		else if (외래어점수 < 50.0)
			return 0.0;
		else
			return 외래어점수;
	}

	public static double 발음검사(String question)
	{
		int cnt = 1;
		double 발음점수 = 0.0;

		// 질문중 발음 이라는 글자를 포함 할 경우 80점
		if (question.contains("발음"))
			발음점수 += 80.0;

		// 질문중 [ 포함 할 경우 80 // 단 [] 안에 2글자 이상의 한글이 들어 있을 경우에만
		// 보통 [는 쌍으로 나오므로 [만 체크함
		if (question.contains("["))
		{
			// [ 이 앞쪽에서 발생하는 index
			int startIndex = question.indexOf("[");
			int bsIndex = question.lastIndexOf("[");

			// ] 이 앞쪽에서 발생하는 index
			int endIndex = question.indexOf("]");
			int beIndex = question.lastIndexOf("]");

			// []가 한개일경우
			if (startIndex == bsIndex)
			{
				String tempStr = question.substring(startIndex + 1, endIndex);
				// [] 안에 한글이 2글자 이상 있어야함
				// [] 가 여러개일경우 맨앞 1개 맨 뒤 1개만 검사함 (특정위치 부터 문자검색을 모르겠음)
				if (tempStr.matches(".*[가-힣]{2,}+.*"))
				{
					발음점수 += 80.0;
				}
			} else
			{
				String tempStr = question.substring(startIndex + 1, endIndex);
				String tempStr2 = question.substring(bsIndex + 1, beIndex);
				// [] 안에 한글이 2글자 이상 있어야함
				// [] 가 여러개일경우 맨앞 1개 맨 뒤 1개만 검사함 (특정위치 부터 문자검색을 모르겠음)
				if (tempStr.matches(".*[가-힣]{2,}+.*") || tempStr2.matches(".*[가-힣]{2,}+.*"))
				{
					발음점수 += 80.0;
				}
			}
			// []안의 단어를 가져옴
		}

		if (발음점수 >= 100.0)
			return 100.0;
		else if (발음점수 < 50.0)
			return 0.0;
		else
			return 발음점수;
	}

	public static double spacingScoreCal(String sentence)
	{
		double totalScore = 0;

		totalScore = spacingKewordCheck(sentence);
		totalScore = spacingLengthCheck(sentence, totalScore);

		if (totalScore >= 100.0)
			return 100.0;
		else if (totalScore < 50.0)
			return 0.0;
		else
			return totalScore;

	}

	public static double meanScoreCal(String sentence)
	{
		double totalScore = 0;

		totalScore = meanKewordCheck(sentence);
		totalScore = meanLengthCheck(sentence, totalScore);

		if (totalScore >= 100.0)
			return 100.0;
		else if (totalScore < 50.0)
			return 0.0;
		else
			return totalScore;
	}

	public static double spacingKewordCheck(String sentence)
	{
		double keywordScore = 0; // 키워드에 대한 점수

		if (sentence.matches(".*띄어쓰기.*"))
		{
			keywordScore += 띄어쓰기;
		} else if (sentence.matches(".*띄어쓰는.*"))
		{
			keywordScore += 띄어쓰는;
		} else if (sentence.matches(".*띄어야.*"))
		{
			keywordScore += 띄어야;
		} else if (sentence.matches(".*띄어.*"))
		{
			keywordScore += 띄어;
		}

		if (sentence.matches(".*붙여쓰기.*"))
		{
			keywordScore += 붙여쓰기;
		} else if (sentence.matches(".*붙여쓰는.*"))
		{
			keywordScore += 붙여쓰는;
		} else if (sentence.matches(".*붙여야.*"))
		{
			keywordScore += 붙여야;
		} else if (sentence.matches(".*붙여.*"))
		{
			keywordScore += 붙여;
		}

		return keywordScore;
	}

	public static double spacingLengthCheck(String sentence, double score)
	{
		double lengthScore = score; // 길이에 대한 점수

		if (sentence.length() > 400)
		{
			lengthScore = (lengthScore * 0.4);
		}

		return lengthScore;
	}

	public static double meanKewordCheck(String sentence)
	{
		double kewordScore = 0;

		if (sentence.matches(".*의미인가요.*"))
		{
			kewordScore += 의미인가요;
		} else if (sentence.matches(".*의미는.*"))
		{
			kewordScore += 의미는;
		} else if (sentence.matches(".*의미인가요.*"))
		{
			kewordScore += 의미로;
		} else if (sentence.matches(".*의미인가요.*"))
		{
			kewordScore += 의미를;
		} else if (sentence.matches(".*의미.*"))
		{
			kewordScore += 의미;
		}

		if (sentence.matches(".*뜻인가요.*"))
		{
			kewordScore += 뜻인가요;
		} else if (sentence.matches(".*뜻은.*"))
		{
			kewordScore += 뜻은;
		} else if (sentence.matches(".*뜻을.*"))
		{
			kewordScore += 뜻을;
		} else if (sentence.matches(".*뜻으로.*"))
		{
			kewordScore += 뜻으로;
		} else if (sentence.matches(".*뜻.*"))
		{
			kewordScore += 뜻;
		}

		if (sentence.matches(".*차이점.*"))
		{
			kewordScore += 차이점;
		} else if (sentence.matches(".*차이를.*"))
		{
			kewordScore += 차이를;
		} else if (sentence.matches(".*차이는.*"))
		{
			kewordScore += 차이는;
		} else if (sentence.matches(".*차이.*"))
		{
			kewordScore += 차이;
		}

		return kewordScore;
	}

	public static double meanLengthCheck(String sentence, double score)
	{
		double lengthScore = score;

		if (sentence.length() > 400)
		{
			score = (int) (score * 0.4);
		} else if (sentence.length() > 200)
		{
			score = (int) (score * 0.8);
		}

		return lengthScore;
	}

}
