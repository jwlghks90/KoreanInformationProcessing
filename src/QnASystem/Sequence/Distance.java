package QnASystem.Sequence;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Scanner;

import QnASystem.Question;
import QnASystem.Vector.Weight;

public class Distance
{
	private static int minimum(int a, int b, int c)
	{
		int m;
		m = a;
		if (b < m)
			m = b;
		if (c < m)
			m = c;
		return m;
	}

	public static int editDistance(String s, String t)
	{
		int d[][];
		int n;
		int m;
		int i;
		int j;
		char s_i;
		char t_j;
		int cost;

		n = s.length();
		m = t.length();

		if (n == 0)
			return m;
		if (m == 0)
			return n;

		d = new int[n + 1][m + 1];

		for (i = 0; i <= n; i++)
		{
			d[i][0] = i;
		}

		for (j = 0; j <= m; j++)
		{
			d[0][j] = j;
		}

		for (i = 1; i <= n; i++)
		{
			s_i = s.charAt(i - 1);

			for (j = 1; j <= m; j++)
			{
				t_j = t.charAt(j - 1);
				if (s_i == t_j)
					cost = 0;
				else
					cost = 1;

				d[i][j] = minimum(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost);
			}
		}

		return d[n][m];
	}
	
	public static double getSimilarity(String query, String questionset)
	{
		if (questionset.contains("궁금합니다") || questionset.contains("궁금하") || questionset.contains("궁금해요")
				|| questionset.contains("알려주세요") || questionset.contains("주세요") || questionset.contains("무엇인가요"))
		{
			String[] tempQuery = questionset.split(" ");
			questionset = "";
			for (int i = 0; i < tempQuery.length - 1; i++)
			{
				if (!tempQuery[i].equals("궁금합니다") && tempQuery[i].equals("궁금하") && tempQuery[i].equals("궁금해요")
						&& tempQuery[i].equals("알려주세요") && tempQuery[i].equals("주세요") && tempQuery[i].equals("무엇인가요"))
				{
					questionset += tempQuery[i];
					questionset += " ";
				}
			}

		}

		String nfd1 = Normalizer.normalize(query, Normalizer.Form.NFD);
		String nfd2 = Normalizer.normalize(questionset, Normalizer.Form.NFD);
		
		int len;
		if (nfd1.length() > nfd2.length())
			len = nfd1.length();
		else
			len = nfd2.length();
		
		return (double) (len - editDistance(nfd1, nfd2)) / (double) len * 100;
	}
	
	public static double getStandardSimilarity(String s, String t)
	{
		
		int len;
		if (s.length() > t.length())
			len = s.length();
		else
			len = t.length();
		
		return (double) (len - editDistance(s, t)) / (double) len * 100;
	}

	public static int advancedEditDistance(String s, String t)
	{
		String nfd1 = Normalizer.normalize(s, Normalizer.Form.NFD);
		String nfd2 = Normalizer.normalize(t, Normalizer.Form.NFD);
		return editDistance(nfd1, nfd2);
	}
}
