package QnASystem;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

import Kokoma.Kokoma;
import QnASystem.Sequence.Distance;
import QnASystem.Vector.Vector;
import QnASystem.Vector.Weight;

public class QnASystem
{
	int ��Ȯ�� = -1;
	int ���� = 0 ;
	int ���� = 1;
	int �ܷ��� = 2;
	int �ǹ� = 3;
	int ���� = 4;
	
	Kokoma kokoma;
	ArrayList<Question> qList;
	HashMap<Morpheme, Double> morphIDFmap;
	Vector[] entireVector;
	
	public QnASystem()
	{
		kokoma = new Kokoma();
		
		qList = Weight.getQuestionList(1, 5336, kokoma);
		morphIDFmap = FileIO.getMorphIDFmap(FileIO.Data_PATH + "IDFmapKokoma.txt");
		
		entireVector = new Vector[qList.size()];
		
		int i=0;
		for(Question x : qList) entireVector[i++] = Vector.getVector(morphIDFmap, x.getMorphemes());
	}
	
	public QnASystem(String questionFilePath, String vectorFilePath, String idfmapFile)
	{
		System.out.println("*********�ý��� ��� �غ�*********");
		kokoma = new Kokoma();
		
		qList = readEntireQuestion(questionFilePath);
		morphIDFmap = FileIO.getMorphIDFmap(idfmapFile);
		entireVector = readEntireVector(vectorFilePath);
		
		System.out.println("*********�ý��� ��� �غ�Ϸ�*********");
	}
	
	public ArrayList<Question> readEntireQuestion(String filePath)
	{
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		ArrayList<Question> returnValue = null;
		
		System.out.println("�亯�� ���� �м��� ������ �д� ���Դϴ�...");
		
		try
		{
			// object.dat ���Ϸ� ���� ��ü�� �о���� ��Ʈ���� �����Ѵ�.
			fis = new FileInputStream(filePath);
			ois = new ObjectInputStream(fis);
			
			returnValue = ((ArrayList<Question>)ois.readObject());
		}
		catch(Exception e){e.printStackTrace();}
		finally
		{	
			// ��Ʈ���� �ݾ��ش�.
			if(fis != null) try{fis.close();}catch(IOException e){}
			if(ois != null) try{ois.close();}catch(IOException e){}
			
			return returnValue;
		}
	}
	
	public Vector[] readEntireVector(String filePath)
	{
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		Vector[] returnValue = null;
		
		try
		{
			// object.dat ���Ϸ� ���� ��ü�� �о���� ��Ʈ���� �����Ѵ�.
			fis = new FileInputStream(filePath);
			ois = new ObjectInputStream(fis);
			
			returnValue = ((Vector[])ois.readObject());
		}
		catch(Exception e){e.printStackTrace();}
		finally
		{	
			// ��Ʈ���� �ݾ��ش�.
			if(fis != null) try{fis.close();}catch(IOException e){}
			if(ois != null) try{ois.close();}catch(IOException e){}
			
			return returnValue;
		}
	}
	
	public void writeEntireQuestion()
	{
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try
		{
			// object.dat ������ ��ü �ƿ�ǲ��Ʈ���� �����Ѵ�.
			fos = new FileOutputStream(FileIO.Data_PATH + "entireQuestion.dat");
			oos = new ObjectOutputStream(fos);

			// �ش� ���Ͽ� 3���� ��ü�� ���������� ����
			oos.writeObject(qList);

			// object.dat ���Ͽ� 3���� ��ü ���� �Ϸ�.
			System.out.println("��ü�� �����߽��ϴ�.");

		}
		catch (Exception e){e.printStackTrace();}
		finally
		{
			if (fos != null)
			{
				try
				{
					fos.close();
				}	catch (IOException e){}
			}
			if (oos != null)
			{
				try
				{
					oos.close();
				}	catch (IOException e){}
			}
		}
	}

	public void writeEntireVector()
	{
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try
		{
			// object.dat ������ ��ü �ƿ�ǲ��Ʈ���� �����Ѵ�.
			fos = new FileOutputStream(FileIO.Data_PATH + "entireVector.dat");
			oos = new ObjectOutputStream(fos);

			// �ش� ���Ͽ� 3���� ��ü�� ���������� ����
			oos.writeObject(entireVector);

			// object.dat ���Ͽ� 3���� ��ü ���� �Ϸ�.
			System.out.println("��ü�� �����߽��ϴ�.");

		}
		catch (Exception e){e.printStackTrace();}
		finally
		{
			if (fos != null)
			{
				try
				{
					fos.close();
				}	catch (IOException e){}
			}
			if (oos != null)
			{
				try
				{
					oos.close();
				}	catch (IOException e){}
			}
		}
	}
	
	public double getMRR2(ArrayList<Question> testCase)
	{
		int firstScope = 0;
		int mainScope = 0;
		int totalRank = 0;
		double totalRR = 0f;
		double mrr = 0f;
		
		int testCaseSize = testCase.size();
		int count = 1;
		for(Question testQ : testCase)
		{
			double[] temp�����迭 = {0,0,0,0,0};
			double[] temp����arr = {0,0,0,0,0}; // ����ڰ� �Է�
			boolean ��Ÿ�κз� = true;
			String �з����� = "��Ȯ��";
			System.out.println("---------------------------------------");
			System.out.println();
			System.out.println("�׽�Ʈ���� : " + (count++) + "-" + testQ.getNumber());
			System.out.println(testQ.get�з�����(0) + " | " + testQ.get�з�����(1) + " | " +testQ.get�з�����(2) + " | " +testQ.get�з�����(3) + " | "+testQ.get�з�����(4) + " | "+testQ.getQuestion());
			testQ.setMorphemes(kokoma.getPhraseList(testQ.getQuestion()));
			System.out.println(Vector.getVector(morphIDFmap, testQ.getMorphemes()));
			System.out.println("-------------");
			
			//������ 1���� 0�� �ƴϸ� ��Ȯ�� �ƴ�
			//get�з����� - double �迭 / ���� 0 / ���� 1 / �ܷ��� 2 / �ǹ� 3 / ���� 4
			if(testQ.get�з�����(����) != 0 || testQ.get�з�����(����) != 0 || testQ.get�з�����(�ܷ���) != 0 || testQ.get�з�����(�ǹ�) != 0 || testQ.get�з�����(����) != 0 )
			{
				��Ÿ�κз� = false;
				for(int i=0; i<5; i++)
					temp�����迭[i] = testQ.get�з�����(i);
				
				Arrays.sort(temp�����迭);
				
				//������ ������ temp ������
				double temp;
				for(int i=0; i<temp�����迭.length/2 ; i++)
				{
					temp = temp�����迭[i];
					temp�����迭[i] = temp�����迭[(temp�����迭.length-1)-i];
					temp�����迭[(temp�����迭.length-1)-i] = temp;
				}	
				//������ ��
				
				int tempCnt = 1;
				for(int i = 0 ; i<5; i++)
				{
					if(temp�����迭[i] > 0.0)
					{
						if((i > 0 && temp�����迭[i] != temp�����迭[i-1]) || i == 0)
						{
							for(int j =0; j<5; j++)
							{
								if(temp�����迭[i] == testQ.get�з�����(j))
									temp����arr[j] = (tempCnt);
							}	
						}
					}
					else 
						break;
					tempCnt++;
				}
				//���� ���� �Ϸ�
			}
			
			Vector queryVector = Vector.getVector(morphIDFmap, testQ.getMorphemes());
		//	System.out.println("�������� : " + queryVector);
			
			String testQsim = testQ.getSim();
			
			ArrayList<Question> resultQList = (ArrayList<Question>) qList.clone();

			int i = 0;
			
			for (Vector x : entireVector)
			{
				double �з����� = 0;
				Question q = resultQList.get(i);
				
				double sim = Vector.getCosSimilarity(queryVector, x);
				double editDistance = Distance.getSimilarity(testQ.getQuestion(), q.getQuestion());
				
				double temp = 0;
				for (int j = 0; j < 5; j++)
				{
					double �����亯���� = 0;
					double thisClassScore = q.get�з�����(j);
					
					if (50.0 <= thisClassScore && thisClassScore < 60.0)
						�����亯���� = 1;
					else if (60.0 <= thisClassScore && thisClassScore < 70.0)
						�����亯���� = 1.1;
					else if (70.0 <= thisClassScore && thisClassScore < 80.0)
						�����亯���� = 1.2;
					else if (80.0 <= thisClassScore && thisClassScore < 90.0)
						�����亯���� = 1.4;
					else if (90.0 <= thisClassScore && thisClassScore < 100.0)
						�����亯���� = 2.0;
					else if (thisClassScore == 100.0)
						�����亯���� = 2.4;

					// �з����� += 5*(�����亯����);
					if (q.get�з�����(j) != 0 && temp����arr[j] != 0)
						�з����� += 2 * (�����亯���� + (double) (1 / temp����arr[j]));
					else
						�з����� += 0;

					temp += thisClassScore;
					if(��Ÿ�κз� && (temp == 0)) �з����� = 3; 
				}
				
				q.setScore((sim * 1) + (editDistance * 0.6) + �з����� );
			
			//	q.setScore((sim * 1) + (editDistance * 0.6));
				
				resultQList.set(i++, q);
			}
			
			//�����ؼ� ����ϱ�
			Collections.sort(resultQList, new Comparator<Question>()
			{
				@Override
				public int compare(Question arg0, Question arg1)
				{
					int compare = Double.compare(arg0.getScore(), arg1.getScore());
					if (compare > 0)
						return -1;
					else if (compare < 0)
						return 1;
					else
						return 0;
				}
			});

			int thisQrank = 0;
			for (Question x : resultQList)
			{
				double score = x.getScore();

				if (Double.isNaN(score) || score == 0 || Double.isInfinite(score))
					continue;
				
				thisQrank++;
				System.out.print(x.get�з�����(0) + " | " + x.get�з�����(1) + " | " +x.get�з�����(2) + " | " +x.get�з�����(3) + " | " );
				System.out.print(score + "-" + x.getNumber() + " : ");
				System.out.println(x.getQuestion());
				System.out.println(Vector.getVector(morphIDFmap, x.getMorphemes()));
				if(testQsim.contains(String.valueOf(x.getNumber())))
					break;
			}
			
			// ��� ��
			if(thisQrank == 0) thisQrank = 500;
			if(thisQrank <= 10) mainScope++;	//������ �Ǻ���
			if(thisQrank == 1) firstScope++;	//������ �Ǻ���
			double thisRR = 1f/(double)thisQrank;
			totalRank += thisQrank;
			totalRR += thisRR;
			
			System.out.println("========= " +thisQrank +" ==========");
			System.out.println("========= " +thisRR +" ==========");
			System.out.println("=������� " +totalRR +" ==========");
		}
		
		mrr = totalRR / (double)testCaseSize;
		double k = (double)mainScope / (double)testCaseSize;
		double kk = (double)firstScope / (double)testCaseSize;
		
		System.out.println("��ü������ " + totalRank);
		System.out.println("��ü���̽� " + testCaseSize);
		System.out.println("��ü RR " + totalRR);
		System.out.printf("����ý����� MRR�� %.5f �Դϴ�.%n", mrr);
		System.out.printf("����, ������ ��� %.3f���� ��ũ�˴ϴ�.%n", (double)1f/mrr);
		System.out.printf("������ 5���ǿ� ���� Ȯ���� %.3f �ۼ�Ʈ�Դϴ�.%n", (double)k*100);
		System.out.printf("������ 1���� Ȯ���� %.3f �ۼ�Ʈ�Դϴ�.%n", (double)kk*100);
		
		return mrr;
	}
	
	public double getMRR(ArrayList<Question> testCase)
	{
		double totalRR = 0;
		double mrr = 0;
		
		int testCaseSize = testCase.size();
		int count = 1;
		for(Question testQ : testCase)
		{
			System.out.println("�׽�Ʈ���� : " + count++);
			testQ.setMorphemes(kokoma.getPhraseList(testQ.getQuestion()));
			Vector queryVector = Vector.getVector(morphIDFmap, testQ.getMorphemes());
			System.out.println("���� : " + testQ.getQuestion());
			
			String testQsim = testQ.getSim();
			
			ArrayList<Question> resultQList = (ArrayList<Question>) qList.clone();

			int i = 0;
			for (Vector x : entireVector)
			{
				Question q = resultQList.get(i);

				double sim = Vector.getCosSimilarity(queryVector, x);
				
				double editDistance = Distance.getSimilarity(testQ.getQuestion(), q.getQuestion());
				q.setScore((sim * 1) + (editDistance * 0.05));
				
				resultQList.set(i++, q);
			}
			
			Collections.sort(resultQList, new Comparator<Question>()
			{
				@Override
				public int compare(Question arg0, Question arg1)
				{
					int compare = Double.compare(arg0.getScore(), arg1.getScore());
					if (compare > 0)
						return -1;
					else if (compare < 0)
						return 1;
					else
						return 0;
				}
			});

			int thisQrank = 0;
			for (Question x : resultQList)
			{
				double score = x.getScore();

				if (Double.isNaN(score) || score == 0 || Double.isInfinite(score))
					continue;
				
				thisQrank++;
				if(testQsim.contains(String.valueOf(x.getNumber())))
					break;
			}
			
			if(thisQrank == 0) thisQrank = 500;
			double thisRR = 1/thisQrank;
			totalRR += thisRR;
			System.out.println("���� " + thisQrank + "��");
		}
		
		mrr = totalRR / testCaseSize;
		
		System.out.println(String.format("����ý����� MRR�� %.3f �Դϴ�.", mrr));
		System.out.println(String.format("����, ������ ��� %.2f���� ��ũ�˴ϴ�.", 1/mrr));
		
		return mrr;
	}
	
	public void run()
	{
		Scanner sc = new Scanner(System.in);

		while (true)
		{
			System.out.print("���� �Է� : ");
			String queryString = sc.nextLine();

			System.out.println(queryString);
			Question query = new Question(0, queryString, "�亯", false);
			query.setMorphemes(kokoma.getPhraseList(queryString));
			Vector queryVector = Vector.getVector(morphIDFmap, query.getMorphemes());

			System.out.println("�������� : " + queryVector);

			double[] temp�����迭 = { 0, 0, 0, 0, 0 };
			double[] temp����arr = { 0, 0, 0, 0, 0 }; // ����ڰ� �Է�
			boolean ��Ÿ�κз� = true;

			// ������ 1���� 0�� �ƴϸ� ��Ȯ�� �ƴ�
			// get�з����� - double �迭 / ���� 0 / ���� 1 / �ܷ��� 2 / �ǹ� 3 / ���� 4
			if (query.get�з�����(����) != 0 || query.get�з�����(����) != 0 || query.get�з�����(�ܷ���) != 0 || query.get�з�����(�ǹ�) != 0
					|| query.get�з�����(����) != 0)
			{
				��Ÿ�κз� = false;
				for (int i = 0; i < 5; i++)
					temp�����迭[i] = query.get�з�����(i);

				Arrays.sort(temp�����迭);

				// ������ ������ temp ������
				double temp;
				for (int i = 0; i < temp�����迭.length / 2; i++)
				{
					temp = temp�����迭[i];
					temp�����迭[i] = temp�����迭[(temp�����迭.length - 1) - i];
					temp�����迭[(temp�����迭.length - 1) - i] = temp;
				}
				// ������ ��

				int tempCnt = 1;
				for (int i = 0; i < 5; i++)
				{
					if (temp�����迭[i] > 0.0)
					{
						if ((i > 0 && temp�����迭[i] != temp�����迭[i - 1]) || i == 0)
						{
							for (int j = 0; j < 5; j++)
							{
								if (temp�����迭[i] == query.get�з�����(j))
									temp����arr[j] = (tempCnt);
							}
						}
					} else
						break;
					tempCnt++;
				}
				// ���� ���� �Ϸ�
			}

			ArrayList<Question> resultQList = (ArrayList<Question>) qList.clone();

			int i = 0;

			for (Vector x : entireVector)
			{
				double �з����� = 0;
				Question q = resultQList.get(i);

				double sim = Vector.getCosSimilarity(queryVector, x);
				
				double editDistance = Distance.getSimilarity(query.getQuestion(), q.getQuestion());

				double temp = 0;
				for (int j = 0; j < 5; j++)
				{
					double �����亯���� = 0;
					double thisClassScore = q.get�з�����(j);

					if (50.0 <= thisClassScore && thisClassScore < 60.0)
						�����亯���� = 1;
					else if (60.0 <= thisClassScore && thisClassScore < 70.0)
						�����亯���� = 1.1;
					else if (70.0 <= thisClassScore && thisClassScore < 80.0)
						�����亯���� = 1.2;
					else if (80.0 <= thisClassScore && thisClassScore < 90.0)
						�����亯���� = 1.4;
					else if (90.0 <= thisClassScore && thisClassScore < 100.0)
						�����亯���� = 2.0;
					else if (thisClassScore == 100.0)
						�����亯���� = 2.4;

					if (q.get�з�����(j) != 0 && temp����arr[j] != 0)
						�з����� += 2 * (�����亯���� + (double) (1 / temp����arr[j]));
					else
						�з����� += 0;

					temp += thisClassScore;
					if (��Ÿ�κз� && (temp == 0))
						�з����� = 3;
				}

				q.setScore((sim * 1) + (editDistance * 0.6) + �з�����);

				resultQList.set(i++, q);
			}

			Collections.sort(resultQList, new Comparator<Question>()
			{
				@Override
				public int compare(Question arg0, Question arg1)
				{
					int compare = Double.compare(arg0.getScore(), arg1.getScore());
					if (compare > 0)
						return -1;
					else if (compare < 0)
						return 1;
					else
						return 0;
				}
			});

			for (Question x : resultQList)
			{
				double score = x.getScore();

				if (Double.isNaN(score) || score == 0 || Double.isInfinite(score))
					continue;

				System.out.print(score + "-" + x.getNumber() + " : ");
				System.out.println(x.getQuestion());
			}
		}
	}
	
	public void runForResult()
	{
		System.out.print("InputFile : ");
		Scanner sc = new Scanner(System.in);
		String queryFileName = sc.next();
		sc.close();
		
		String filePath = queryFileName.substring(0, queryFileName.lastIndexOf(System.getProperty("file.separator")));
		ArrayList<Question> queryList = FileIO.inputQueryFile(queryFileName);
		
		ArrayList<ArrayList<Result>> resultList = new ArrayList<ArrayList<Result>>(queryList.size());
		
		System.out.println("����Ǯ�̽���!");
		
		int s = 1;
		for(Question query : queryList)
		{
			ArrayList<Result> thisResults = new ArrayList<Result>(10);
			System.out.println(s++ + "��° ������ �ش��� ã�� �ֽ��ϴ�.");
			
			query.setMorphemes(kokoma.getPhraseList(query.getQuestion()));
			Vector queryVector = Vector.getVector(morphIDFmap, query.getMorphemes());


			double[] temp�����迭 = { 0, 0, 0, 0, 0 };
			double[] temp����arr = { 0, 0, 0, 0, 0 }; // ����ڰ� �Է�
			boolean ��Ÿ�κз� = true;

			if (query.get�з�����(����) != 0 || query.get�з�����(����) != 0 || query.get�з�����(�ܷ���) != 0 || query.get�з�����(�ǹ�) != 0
					|| query.get�з�����(����) != 0)
			{
				��Ÿ�κз� = false;
				for (int i = 0; i < 5; i++)
					temp�����迭[i] = query.get�з�����(i);

				Arrays.sort(temp�����迭);

				// ������ ������ temp ������
				double temp;
				for (int i = 0; i < temp�����迭.length / 2; i++)
				{
					temp = temp�����迭[i];
					temp�����迭[i] = temp�����迭[(temp�����迭.length - 1) - i];
					temp�����迭[(temp�����迭.length - 1) - i] = temp;
				}
				// ������ ��

				int tempCnt = 1;
				for (int i = 0; i < 5; i++)
				{
					if (temp�����迭[i] > 0.0)
					{
						if ((i > 0 && temp�����迭[i] != temp�����迭[i - 1]) || i == 0)
						{
							for (int j = 0; j < 5; j++)
							{
								if (temp�����迭[i] == query.get�з�����(j))
									temp����arr[j] = (tempCnt);
							}
						}
					} else
						break;
					tempCnt++;
				}
				// ���� ���� �Ϸ�
			}

			ArrayList<Question> resultQList = (ArrayList<Question>) qList.clone();

			int i = 0;

			for (Vector x : entireVector)
			{
				double �з����� = 0;
				Question q = resultQList.get(i);

				double sim = Vector.getCosSimilarity(queryVector, x);
				
				double editDistance = Distance.getSimilarity(query.getQuestion(), q.getQuestion());

				double temp = 0;
				for (int j = 0; j < 5; j++)
				{
					double �����亯���� = 0;
					double thisClassScore = q.get�з�����(j);

					if (50.0 <= thisClassScore && thisClassScore < 60.0)
						�����亯���� = 1;
					else if (60.0 <= thisClassScore && thisClassScore < 70.0)
						�����亯���� = 1.1;
					else if (70.0 <= thisClassScore && thisClassScore < 80.0)
						�����亯���� = 1.2;
					else if (80.0 <= thisClassScore && thisClassScore < 90.0)
						�����亯���� = 1.4;
					else if (90.0 <= thisClassScore && thisClassScore < 100.0)
						�����亯���� = 2.0;
					else if (thisClassScore == 100.0)
						�����亯���� = 2.4;

					if (q.get�з�����(j) != 0 && temp����arr[j] != 0)
						�з����� += 3 * (�����亯���� + (double) (1 / temp����arr[j]));
					else
						�з����� += 0;

					temp += thisClassScore;
					if (��Ÿ�κз� && (temp == 0))
						�з����� = 3;
				}

				q.setScore((sim * 1) + (editDistance * 0.6) + �з�����);

				resultQList.set(i++, q);
			}

			// �����ؼ� ����ϱ�
			Collections.sort(resultQList, new Comparator<Question>()
			{
				@Override
				public int compare(Question arg0, Question arg1)
				{
					int compare = Double.compare(arg0.getScore(), arg1.getScore());
					if (compare > 0)
						return -1;
					else if (compare < 0)
						return 1;
					else
						return 0;
				}
			});
			
			int resultCount = 0;
			for (int repeater = 0; repeater < 10; resultCount++)
			{
				Question x = null;
				try
				{
					x = resultQList.get(resultCount);
				} catch(IndexOutOfBoundsException e)
				{
					thisResults.add(new Result(0,0));
				}
				double score = x.getScore();

				if (Double.isNaN(score) || score == 0 || Double.isInfinite(score))
					continue;
				
				thisResults.add(new Result(x.getNumber(), score));
				repeater++;
			}
			resultList.add(thisResults);
		}
		
		FileIO.writeResultFile(resultList, filePath);
		
		System.out.println("result.xml write success.");
	}

	public void run2()
	{
		Scanner sc = new Scanner(System.in);
		while(true)
		{
			System.out.print("���� �Է� : ");
			String queryString = sc.nextLine();

			System.out.println(queryString);
			Question query = new Question(0, queryString, "�亯", false);
			query.setMorphemes(kokoma.getPhraseList(queryString));
			Vector queryVector = Vector.getVector(morphIDFmap, query.getMorphemes());

			System.out.println("�������� : " + queryVector);

			ArrayList<Question> resultQList = (ArrayList<Question>) qList.clone();

			int i = 0;
			for (Vector x : entireVector)
			{
				Question q = resultQList.get(i);

				double sim = Vector.getCosSimilarity(queryVector, x);

				resultQList.set(i++, q);
			}
			
			Collections.sort(resultQList, new Comparator<Question>()
			{
				@Override
				public int compare(Question arg0, Question arg1)
				{
					int compare = Double.compare(arg0.getScore(), arg1.getScore());
					if (compare > 0)
						return -1;
					else if (compare < 0)
						return 1;
					else
						return 0;
				}
			});

			for (Question x : resultQList)
			{
				double score = x.getScore();

				if (Double.isNaN(score) || score == 0)
					continue;
				System.out.print(score + "-" + x.getNumber() + " : ");
				System.out.println(x.getQuestion());
			}
		}
	}
}
