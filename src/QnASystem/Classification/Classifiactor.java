package QnASystem.Classification;

import org.snu.ids.ha.index.Keyword;
import org.snu.ids.ha.index.KeywordExtractor;
import org.snu.ids.ha.index.KeywordList;

public class Classifiactor
{
	// ������ ���� ���� ����ġ �ο�
	public static final int ���� = 100;
	public static final int ���� = 70;
	public static final int ���� = 70;
	public static final int ��� = 50;
	public static final int ��� = 30;

	public static final int �ٿ����� = 100;
	public static final int �ٿ����� = 70;
	public static final int �ٿ��� = 70;
	public static final int �ٿ� = 50;
	public static final int ���� = 30;
	// ///////////////////////////////

	// ������ ���� �ǹ� ����ġ �ο�
	public static final int �ǹ� = 30;
	public static final int ���� = 30;
	public static final int �� = 30;

	public static final int �ǹ��ΰ��� = 100;
	public static final int �ǹ̴� = 65;
	public static final int �ǹ̷� = 65;
	public static final int �ǹ̸� = 65;

	public static final int ���ΰ��� = 100;
	public static final int ���� = 65;
	public static final int ���� = 65;
	public static final int ������ = 55;

	public static final int ������ = 60;
	public static final int ���̸� = 50;
	public static final int ���̴� = 50;
	
	public static double grammarScoreCal(String sentence) // Ű���常 �̿�
	{
		double keywordScore = 0;

		// ������ ���õ� Ű����
		if (sentence.matches(".*�ռ���.*") || sentence.matches(".*�Ļ���.*") || sentence.matches(".*���̻�.*")
				|| sentence.matches(".*�����.*") || sentence.matches(".*����.*") || sentence.matches(".*����.*")
				|| sentence.matches(".*�λ�.*") || sentence.matches(".*�.*") || sentence.matches(".*���.*")
				|| sentence.matches(".*���.*") || sentence.matches(".*����.*") || sentence.matches(".*���¼�.*")
				|| sentence.matches(".*�ֵ�.*") || sentence.matches(".*�絿.*") || sentence.matches(".*�ɵ�.*")
				|| sentence.matches(".*�ǵ�.*") || sentence.matches(".*���.*") || sentence.matches(".*������.*")
				|| sentence.matches(".*�缺����.*") || sentence.matches(".*������ȭ.*") || sentence.matches(".*������.*")
				|| sentence.matches(".*���� ���.*") || sentence.matches(".*�������.*"))
		{
			keywordScore = 100;
		}

		return keywordScore;
	}

	public static double �ܷ���˻�(String question)
	{

		KeywordExtractor ke = new KeywordExtractor(); // ������ �м���
		double �ܷ������� = 0.0;

		// �ܷ���� �θ��ڸ� ������ ��� 70��
		if (question.contains("�ܷ���") || question.contains("�θ���"))
			�ܷ������� += 80.0;

		// ǥ�⸦ ������ ��� 25��
		if (question.contains("ǥ��"))
			�ܷ������� += 25.0;

		// ����(����4���̻� ���ĺ�)�� ���� 25��
		// ��� �����ϸ鼭 ���¼� �м� ��� ��(VV �Ǵ� VA) ��(VV)�� ���� ��� 25��
		// �ܷ�� �θ��� ǥ�� ���� ������ ���� ������ ���µ� ������ ���� ��� �� ������ ���¼� �м��� �ϸ�
		if (question.matches(".*[a-zA-Z]{4,}+.*"))
		{
			�ܷ������� += 25.0;
			// ���������¼�
			KeywordList keyList = ke.extractKeyword(question, false);
			for (Keyword keyword : keyList)
			{
				if (keyword.getString().equals("��") && keyword.getTag().equals("VV") || keyword.getString().equals("��")
						&& keyword.getTag().equals("VA") || keyword.getString().equals("��")
						&& keyword.getTag().equals("VV"))
				{
					�ܷ������� += 25.0;
					break;
				}
			}
		}

		if (�ܷ������� >= 100.0)
			return 100.0;
		else if (�ܷ������� < 50.0)
			return 0.0;
		else
			return �ܷ�������;
	}

	public static double �����˻�(String question)
	{
		int cnt = 1;
		double �������� = 0.0;

		// ������ ���� �̶�� ���ڸ� ���� �� ��� 80��
		if (question.contains("����"))
			�������� += 80.0;

		// ������ [ ���� �� ��� 80 // �� [] �ȿ� 2���� �̻��� �ѱ��� ��� ���� ��쿡��
		// ���� [�� ������ �����Ƿ� [�� üũ��
		if (question.contains("["))
		{
			// [ �� ���ʿ��� �߻��ϴ� index
			int startIndex = question.indexOf("[");
			int bsIndex = question.lastIndexOf("[");

			// ] �� ���ʿ��� �߻��ϴ� index
			int endIndex = question.indexOf("]");
			int beIndex = question.lastIndexOf("]");

			// []�� �Ѱ��ϰ��
			if (startIndex == bsIndex)
			{
				String tempStr = question.substring(startIndex + 1, endIndex);
				// [] �ȿ� �ѱ��� 2���� �̻� �־����
				// [] �� �������ϰ�� �Ǿ� 1�� �� �� 1���� �˻��� (Ư����ġ ���� ���ڰ˻��� �𸣰���)
				if (tempStr.matches(".*[��-�R]{2,}+.*"))
				{
					�������� += 80.0;
				}
			} else
			{
				String tempStr = question.substring(startIndex + 1, endIndex);
				String tempStr2 = question.substring(bsIndex + 1, beIndex);
				// [] �ȿ� �ѱ��� 2���� �̻� �־����
				// [] �� �������ϰ�� �Ǿ� 1�� �� �� 1���� �˻��� (Ư����ġ ���� ���ڰ˻��� �𸣰���)
				if (tempStr.matches(".*[��-�R]{2,}+.*") || tempStr2.matches(".*[��-�R]{2,}+.*"))
				{
					�������� += 80.0;
				}
			}
			// []���� �ܾ ������
		}

		if (�������� >= 100.0)
			return 100.0;
		else if (�������� < 50.0)
			return 0.0;
		else
			return ��������;
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
		double keywordScore = 0; // Ű���忡 ���� ����

		if (sentence.matches(".*����.*"))
		{
			keywordScore += ����;
		} else if (sentence.matches(".*����.*"))
		{
			keywordScore += ����;
		} else if (sentence.matches(".*����.*"))
		{
			keywordScore += ����;
		} else if (sentence.matches(".*���.*"))
		{
			keywordScore += ���;
		}

		if (sentence.matches(".*�ٿ�����.*"))
		{
			keywordScore += �ٿ�����;
		} else if (sentence.matches(".*�ٿ�����.*"))
		{
			keywordScore += �ٿ�����;
		} else if (sentence.matches(".*�ٿ���.*"))
		{
			keywordScore += �ٿ���;
		} else if (sentence.matches(".*�ٿ�.*"))
		{
			keywordScore += �ٿ�;
		}

		return keywordScore;
	}

	public static double spacingLengthCheck(String sentence, double score)
	{
		double lengthScore = score; // ���̿� ���� ����

		if (sentence.length() > 400)
		{
			lengthScore = (lengthScore * 0.4);
		}

		return lengthScore;
	}

	public static double meanKewordCheck(String sentence)
	{
		double kewordScore = 0;

		if (sentence.matches(".*�ǹ��ΰ���.*"))
		{
			kewordScore += �ǹ��ΰ���;
		} else if (sentence.matches(".*�ǹ̴�.*"))
		{
			kewordScore += �ǹ̴�;
		} else if (sentence.matches(".*�ǹ��ΰ���.*"))
		{
			kewordScore += �ǹ̷�;
		} else if (sentence.matches(".*�ǹ��ΰ���.*"))
		{
			kewordScore += �ǹ̸�;
		} else if (sentence.matches(".*�ǹ�.*"))
		{
			kewordScore += �ǹ�;
		}

		if (sentence.matches(".*���ΰ���.*"))
		{
			kewordScore += ���ΰ���;
		} else if (sentence.matches(".*����.*"))
		{
			kewordScore += ����;
		} else if (sentence.matches(".*����.*"))
		{
			kewordScore += ����;
		} else if (sentence.matches(".*������.*"))
		{
			kewordScore += ������;
		} else if (sentence.matches(".*��.*"))
		{
			kewordScore += ��;
		}

		if (sentence.matches(".*������.*"))
		{
			kewordScore += ������;
		} else if (sentence.matches(".*���̸�.*"))
		{
			kewordScore += ���̸�;
		} else if (sentence.matches(".*���̴�.*"))
		{
			kewordScore += ���̴�;
		} else if (sentence.matches(".*����.*"))
		{
			kewordScore += ����;
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
