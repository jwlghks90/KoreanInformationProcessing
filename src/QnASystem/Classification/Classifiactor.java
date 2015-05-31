package QnASystem.Classification;

import org.snu.ids.ha.index.Keyword;
import org.snu.ids.ha.index.KeywordExtractor;
import org.snu.ids.ha.index.KeywordList;

public class Classifiactor
{
	// Áú¹®¿¡ ´ëÇÑ ¶ç¾î¾²±â °¡ÁßÄ¡ ºÎ¿©
	public static final int ¶ç¾î¾²±â = 100;
	public static final int ¶ç¾î¾²´Â = 70;
	public static final int ¶ç¾î¾ß = 70;
	public static final int ¶ç¾î = 50;
	public static final int ¶ç¿î = 30;

	public static final int ºÙ¿©¾²±â = 100;
	public static final int ºÙ¿©¾²´Â = 70;
	public static final int ºÙ¿©¾ß = 70;
	public static final int ºÙ¿© = 50;
	public static final int ºÙÀº = 30;
	// ///////////////////////////////

	// Áú¹®¿¡ ´ëÇÑ ÀÇ¹Ì °¡ÁßÄ¡ ºÎ¿©
	public static final int ÀÇ¹Ì = 30;
	public static final int Â÷ÀÌ = 30;
	public static final int ¶æ = 30;

	public static final int ÀÇ¹ÌÀÎ°¡¿ä = 100;
	public static final int ÀÇ¹Ì´Â = 65;
	public static final int ÀÇ¹Ì·Î = 65;
	public static final int ÀÇ¹Ì¸¦ = 65;

	public static final int ¶æÀÎ°¡¿ä = 100;
	public static final int ¶æÀº = 65;
	public static final int ¶æÀ» = 65;
	public static final int ¶æÀ¸·Î = 55;

	public static final int Â÷ÀÌÁ¡ = 60;
	public static final int Â÷ÀÌ¸¦ = 50;
	public static final int Â÷ÀÌ´Â = 50;
	
	public static double grammarScoreCal(String sentence) // Å°¿öµå¸¸ ÀÌ¿ë
	{
		double keywordScore = 0;

		// ¹®¹ý¿¡ °ü·ÃµÈ Å°¿öµå
		if (sentence.matches(".*ÇÕ¼º¾î.*") || sentence.matches(".*ÆÄ»ý¾î.*") || sentence.matches(".*Á¢¹Ì»ç.*")
				|| sentence.matches(".*Çü¿ë»ç.*") || sentence.matches(".*µ¿»ç.*") || sentence.matches(".*Á¶»ç.*")
				|| sentence.matches(".*ºÎ»ç.*") || sentence.matches(".*¾î°£.*") || sentence.matches(".*¾î¹Ì.*")
				|| sentence.matches(".*¾î¹ý.*") || sentence.matches(".*¹®¹ý.*") || sentence.matches(".*ÇüÅÂ¼Ò.*")
				|| sentence.matches(".*ÁÖµ¿.*") || sentence.matches(".*»çµ¿.*") || sentence.matches(".*´Éµ¿.*")
				|| sentence.matches(".*ÇÇµ¿.*") || sentence.matches(".*¿ë¾ð.*") || sentence.matches(".*°üÇü»ç.*")
				|| sentence.matches(".*¾ç¼º¸ðÀ½.*") || sentence.matches(".*ÀÚÀ½µ¿È­.*") || sentence.matches(".*°ú°ÅÇü.*")
				|| sentence.matches(".*ÀÇÁ¸ ¸í»ç.*") || sentence.matches(".*ÀÇÁ¸¸í»ç.*"))
		{
			keywordScore = 100;
		}

		return keywordScore;
	}

	public static double ¿Ü·¡¾î°Ë»ç(String question)
	{

		KeywordExtractor ke = new KeywordExtractor(); // ²¿²¿¸¶ ºÐ¼®±â
		double ¿Ü·¡¾îÁ¡¼ö = 0.0;

		// ¿Ü·¡¾î¿Í ·Î¸¶ÀÚ¸¦ Æ÷ÇÔÇÒ °æ¿ì 70Á¡
		if (question.contains("¿Ü·¡¾î") || question.contains("·Î¸¶ÀÚ"))
			¿Ü·¡¾îÁ¡¼ö += 80.0;

		// Ç¥±â¸¦ Æ÷ÇÔÇÒ °æ¿ì 25Á¡
		if (question.contains("Ç¥±â"))
			¿Ü·¡¾îÁ¡¼ö += 25.0;

		// ¿µ¾î(¿¬¼Ó4°³ÀÌ»ó ¾ËÆÄºª)¸¦ Æ÷ÇÔ 25Á¡
		// ¿µ¾î¸¦ Æ÷ÇÔÇÏ¸é¼­ ÇüÅÂ¼Ò ºÐ¼® °á°ú Àû(VV ¶Ç´Â VA) ¾²(VV)°¡ ÀÖÀ» °æ¿ì 25Á¡
		// ¿Ü·¡¾î³ª ·Î¸¶ÀÚ Ç¥±â °ü·Ã ¹®ÀåÀ» º¸¸é Àû³ª¿ä Àû´Âµ¥ ¾²³ª¿ä ¾²´Â ¹æ¹ý µî ¸¹Àºµ¥ ÇüÅÂ¼Ò ºÐ¼®À» ÇÏ¸ç
		if (question.matches(".*[a-zA-Z]{4,}+.*"))
		{
			¿Ü·¡¾îÁ¡¼ö += 25.0;
			// ²¿²¿¸¶ÇüÅÂ¼Ò
			KeywordList keyList = ke.extractKeyword(question, false);
			for (Keyword keyword : keyList)
			{
				if (keyword.getString().equals("Àû") && keyword.getTag().equals("VV") || keyword.getString().equals("Àû")
						&& keyword.getTag().equals("VA") || keyword.getString().equals("¾²")
						&& keyword.getTag().equals("VV"))
				{
					¿Ü·¡¾îÁ¡¼ö += 25.0;
					break;
				}
			}
		}

		if (¿Ü·¡¾îÁ¡¼ö >= 100.0)
			return 100.0;
		else if (¿Ü·¡¾îÁ¡¼ö < 50.0)
			return 0.0;
		else
			return ¿Ü·¡¾îÁ¡¼ö;
	}

	public static double ¹ßÀ½°Ë»ç(String question)
	{
		int cnt = 1;
		double ¹ßÀ½Á¡¼ö = 0.0;

		// Áú¹®Áß ¹ßÀ½ ÀÌ¶ó´Â ±ÛÀÚ¸¦ Æ÷ÇÔ ÇÒ °æ¿ì 80Á¡
		if (question.contains("¹ßÀ½"))
			¹ßÀ½Á¡¼ö += 80.0;

		// Áú¹®Áß [ Æ÷ÇÔ ÇÒ °æ¿ì 80 // ´Ü [] ¾È¿¡ 2±ÛÀÚ ÀÌ»óÀÇ ÇÑ±ÛÀÌ µé¾î ÀÖÀ» °æ¿ì¿¡¸¸
		// º¸Åë [´Â ½ÖÀ¸·Î ³ª¿À¹Ç·Î [¸¸ Ã¼Å©ÇÔ
		if (question.contains("["))
		{
			// [ ÀÌ ¾ÕÂÊ¿¡¼­ ¹ß»ýÇÏ´Â index
			int startIndex = question.indexOf("[");
			int bsIndex = question.lastIndexOf("[");

			// ] ÀÌ ¾ÕÂÊ¿¡¼­ ¹ß»ýÇÏ´Â index
			int endIndex = question.indexOf("]");
			int beIndex = question.lastIndexOf("]");

			// []°¡ ÇÑ°³ÀÏ°æ¿ì
			if (startIndex == bsIndex)
			{
				String tempStr = question.substring(startIndex + 1, endIndex);
				// [] ¾È¿¡ ÇÑ±ÛÀÌ 2±ÛÀÚ ÀÌ»ó ÀÖ¾î¾ßÇÔ
				// [] °¡ ¿©·¯°³ÀÏ°æ¿ì ¸Ç¾Õ 1°³ ¸Ç µÚ 1°³¸¸ °Ë»çÇÔ (Æ¯Á¤À§Ä¡ ºÎÅÍ ¹®ÀÚ°Ë»öÀ» ¸ð¸£°ÚÀ½)
				if (tempStr.matches(".*[°¡-ÆR]{2,}+.*"))
				{
					¹ßÀ½Á¡¼ö += 80.0;
				}
			} else
			{
				String tempStr = question.substring(startIndex + 1, endIndex);
				String tempStr2 = question.substring(bsIndex + 1, beIndex);
				// [] ¾È¿¡ ÇÑ±ÛÀÌ 2±ÛÀÚ ÀÌ»ó ÀÖ¾î¾ßÇÔ
				// [] °¡ ¿©·¯°³ÀÏ°æ¿ì ¸Ç¾Õ 1°³ ¸Ç µÚ 1°³¸¸ °Ë»çÇÔ (Æ¯Á¤À§Ä¡ ºÎÅÍ ¹®ÀÚ°Ë»öÀ» ¸ð¸£°ÚÀ½)
				if (tempStr.matches(".*[°¡-ÆR]{2,}+.*") || tempStr2.matches(".*[°¡-ÆR]{2,}+.*"))
				{
					¹ßÀ½Á¡¼ö += 80.0;
				}
			}
			// []¾ÈÀÇ ´Ü¾î¸¦ °¡Á®¿È
		}

		if (¹ßÀ½Á¡¼ö >= 100.0)
			return 100.0;
		else if (¹ßÀ½Á¡¼ö < 50.0)
			return 0.0;
		else
			return ¹ßÀ½Á¡¼ö;
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
		double keywordScore = 0; // Å°¿öµå¿¡ ´ëÇÑ Á¡¼ö

		if (sentence.matches(".*¶ç¾î¾²±â.*"))
		{
			keywordScore += ¶ç¾î¾²±â;
		} else if (sentence.matches(".*¶ç¾î¾²´Â.*"))
		{
			keywordScore += ¶ç¾î¾²´Â;
		} else if (sentence.matches(".*¶ç¾î¾ß.*"))
		{
			keywordScore += ¶ç¾î¾ß;
		} else if (sentence.matches(".*¶ç¾î.*"))
		{
			keywordScore += ¶ç¾î;
		}

		if (sentence.matches(".*ºÙ¿©¾²±â.*"))
		{
			keywordScore += ºÙ¿©¾²±â;
		} else if (sentence.matches(".*ºÙ¿©¾²´Â.*"))
		{
			keywordScore += ºÙ¿©¾²´Â;
		} else if (sentence.matches(".*ºÙ¿©¾ß.*"))
		{
			keywordScore += ºÙ¿©¾ß;
		} else if (sentence.matches(".*ºÙ¿©.*"))
		{
			keywordScore += ºÙ¿©;
		}

		return keywordScore;
	}

	public static double spacingLengthCheck(String sentence, double score)
	{
		double lengthScore = score; // ±æÀÌ¿¡ ´ëÇÑ Á¡¼ö

		if (sentence.length() > 400)
		{
			lengthScore = (lengthScore * 0.4);
		}

		return lengthScore;
	}

	public static double meanKewordCheck(String sentence)
	{
		double kewordScore = 0;

		if (sentence.matches(".*ÀÇ¹ÌÀÎ°¡¿ä.*"))
		{
			kewordScore += ÀÇ¹ÌÀÎ°¡¿ä;
		} else if (sentence.matches(".*ÀÇ¹Ì´Â.*"))
		{
			kewordScore += ÀÇ¹Ì´Â;
		} else if (sentence.matches(".*ÀÇ¹ÌÀÎ°¡¿ä.*"))
		{
			kewordScore += ÀÇ¹Ì·Î;
		} else if (sentence.matches(".*ÀÇ¹ÌÀÎ°¡¿ä.*"))
		{
			kewordScore += ÀÇ¹Ì¸¦;
		} else if (sentence.matches(".*ÀÇ¹Ì.*"))
		{
			kewordScore += ÀÇ¹Ì;
		}

		if (sentence.matches(".*¶æÀÎ°¡¿ä.*"))
		{
			kewordScore += ¶æÀÎ°¡¿ä;
		} else if (sentence.matches(".*¶æÀº.*"))
		{
			kewordScore += ¶æÀº;
		} else if (sentence.matches(".*¶æÀ».*"))
		{
			kewordScore += ¶æÀ»;
		} else if (sentence.matches(".*¶æÀ¸·Î.*"))
		{
			kewordScore += ¶æÀ¸·Î;
		} else if (sentence.matches(".*¶æ.*"))
		{
			kewordScore += ¶æ;
		}

		if (sentence.matches(".*Â÷ÀÌÁ¡.*"))
		{
			kewordScore += Â÷ÀÌÁ¡;
		} else if (sentence.matches(".*Â÷ÀÌ¸¦.*"))
		{
			kewordScore += Â÷ÀÌ¸¦;
		} else if (sentence.matches(".*Â÷ÀÌ´Â.*"))
		{
			kewordScore += Â÷ÀÌ´Â;
		} else if (sentence.matches(".*Â÷ÀÌ.*"))
		{
			kewordScore += Â÷ÀÌ;
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
