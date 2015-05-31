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
	int 불확실 = -1;
	int 띄어쓰기 = 0 ;
	int 발음 = 1;
	int 외래어 = 2;
	int 의미 = 3;
	int 문법 = 4;
	
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
		System.out.println("*********시스템 사용 준비*********");
		kokoma = new Kokoma();
		
		qList = readEntireQuestion(questionFilePath);
		morphIDFmap = FileIO.getMorphIDFmap(idfmapFile);
		entireVector = readEntireVector(vectorFilePath);
		
		System.out.println("*********시스템 사용 준비완료*********");
	}
	
	public ArrayList<Question> readEntireQuestion(String filePath)
	{
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		ArrayList<Question> returnValue = null;
		
		System.out.println("답변을 위해 분석된 문제를 읽는 중입니다...");
		
		try
		{
			// object.dat 파일로 부터 객체를 읽어오는 스트림을 생성한다.
			fis = new FileInputStream(filePath);
			ois = new ObjectInputStream(fis);
			
			returnValue = ((ArrayList<Question>)ois.readObject());
		}
		catch(Exception e){e.printStackTrace();}
		finally
		{	
			// 스트림을 닫아준다.
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
			// object.dat 파일로 부터 객체를 읽어오는 스트림을 생성한다.
			fis = new FileInputStream(filePath);
			ois = new ObjectInputStream(fis);
			
			returnValue = ((Vector[])ois.readObject());
		}
		catch(Exception e){e.printStackTrace();}
		finally
		{	
			// 스트림을 닫아준다.
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
			// object.dat 파일의 객체 아웃풋스트림을 생성한다.
			fos = new FileOutputStream(FileIO.Data_PATH + "entireQuestion.dat");
			oos = new ObjectOutputStream(fos);

			// 해당 파일에 3개의 객체를 순차적으로 쓴다
			oos.writeObject(qList);

			// object.dat 파일에 3개의 객체 쓰기 완료.
			System.out.println("객체를 저장했습니다.");

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
			// object.dat 파일의 객체 아웃풋스트림을 생성한다.
			fos = new FileOutputStream(FileIO.Data_PATH + "entireVector.dat");
			oos = new ObjectOutputStream(fos);

			// 해당 파일에 3개의 객체를 순차적으로 쓴다
			oos.writeObject(entireVector);

			// object.dat 파일에 3개의 객체 쓰기 완료.
			System.out.println("객체를 저장했습니다.");

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
			double[] temp점수배열 = {0,0,0,0,0};
			double[] temp비율arr = {0,0,0,0,0}; // 사용자가 입력
			boolean 기타로분류 = true;
			String 분류종류 = "불확실";
			System.out.println("---------------------------------------");
			System.out.println();
			System.out.println("테스트시작 : " + (count++) + "-" + testQ.getNumber());
			System.out.println(testQ.get분류점수(0) + " | " + testQ.get분류점수(1) + " | " +testQ.get분류점수(2) + " | " +testQ.get분류점수(3) + " | "+testQ.get분류점수(4) + " | "+testQ.getQuestion());
			testQ.setMorphemes(kokoma.getPhraseList(testQ.getQuestion()));
			System.out.println(Vector.getVector(morphIDFmap, testQ.getMorphemes()));
			System.out.println("-------------");
			
			//점수가 1개라도 0이 아니면 불확실 아님
			//get분류점수 - double 배열 / 띄어쓰기 0 / 발음 1 / 외래어 2 / 의미 3 / 문법 4
			if(testQ.get분류점수(띄어쓰기) != 0 || testQ.get분류점수(발음) != 0 || testQ.get분류점수(외래어) != 0 || testQ.get분류점수(의미) != 0 || testQ.get분류점수(문법) != 0 )
			{
				기타로분류 = false;
				for(int i=0; i<5; i++)
					temp점수배열[i] = testQ.get분류점수(i);
				
				Arrays.sort(temp점수배열);
				
				//위에서 정렬한 temp 리버스
				double temp;
				for(int i=0; i<temp점수배열.length/2 ; i++)
				{
					temp = temp점수배열[i];
					temp점수배열[i] = temp점수배열[(temp점수배열.length-1)-i];
					temp점수배열[(temp점수배열.length-1)-i] = temp;
				}	
				//리버스 끝
				
				int tempCnt = 1;
				for(int i = 0 ; i<5; i++)
				{
					if(temp점수배열[i] > 0.0)
					{
						if((i > 0 && temp점수배열[i] != temp점수배열[i-1]) || i == 0)
						{
							for(int j =0; j<5; j++)
							{
								if(temp점수배열[i] == testQ.get분류점수(j))
									temp비율arr[j] = (tempCnt);
							}	
						}
					}
					else 
						break;
					tempCnt++;
				}
				//비율 정리 완료
			}
			
			Vector queryVector = Vector.getVector(morphIDFmap, testQ.getMorphemes());
		//	System.out.println("쿼리벡터 : " + queryVector);
			
			String testQsim = testQ.getSim();
			
			ArrayList<Question> resultQList = (ArrayList<Question>) qList.clone();

			int i = 0;
			
			for (Vector x : entireVector)
			{
				double 분류점수 = 0;
				Question q = resultQList.get(i);
				
				double sim = Vector.getCosSimilarity(queryVector, x);
				double editDistance = Distance.getSimilarity(testQ.getQuestion(), q.getQuestion());
				
				double temp = 0;
				for (int j = 0; j < 5; j++)
				{
					double 기존답변비율 = 0;
					double thisClassScore = q.get분류점수(j);
					
					if (50.0 <= thisClassScore && thisClassScore < 60.0)
						기존답변비율 = 1;
					else if (60.0 <= thisClassScore && thisClassScore < 70.0)
						기존답변비율 = 1.1;
					else if (70.0 <= thisClassScore && thisClassScore < 80.0)
						기존답변비율 = 1.2;
					else if (80.0 <= thisClassScore && thisClassScore < 90.0)
						기존답변비율 = 1.4;
					else if (90.0 <= thisClassScore && thisClassScore < 100.0)
						기존답변비율 = 2.0;
					else if (thisClassScore == 100.0)
						기존답변비율 = 2.4;

					// 분류점수 += 5*(기존답변비율);
					if (q.get분류점수(j) != 0 && temp비율arr[j] != 0)
						분류점수 += 2 * (기존답변비율 + (double) (1 / temp비율arr[j]));
					else
						분류점수 += 0;

					temp += thisClassScore;
					if(기타로분류 && (temp == 0)) 분류점수 = 3; 
				}
				
				q.setScore((sim * 1) + (editDistance * 0.6) + 분류점수 );
			
			//	q.setScore((sim * 1) + (editDistance * 0.6));
				
				resultQList.set(i++, q);
			}
			
			//정렬해서 출력하기
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
				System.out.print(x.get분류점수(0) + " | " + x.get분류점수(1) + " | " +x.get분류점수(2) + " | " +x.get분류점수(3) + " | " );
				System.out.print(score + "-" + x.getNumber() + " : ");
				System.out.println(x.getQuestion());
				System.out.println(Vector.getVector(morphIDFmap, x.getMorphemes()));
				if(testQsim.contains(String.valueOf(x.getNumber())))
					break;
			}
			
			// 출력 끝
			if(thisQrank == 0) thisQrank = 500;
			if(thisQrank <= 10) mainScope++;	//순위권 판별용
			if(thisQrank == 1) firstScope++;	//순위권 판별용
			double thisRR = 1f/(double)thisQrank;
			totalRank += thisQrank;
			totalRR += thisRR;
			
			System.out.println("========= " +thisQrank +" ==========");
			System.out.println("========= " +thisRR +" ==========");
			System.out.println("=현재까지 " +totalRR +" ==========");
		}
		
		mrr = totalRR / (double)testCaseSize;
		double k = (double)mainScope / (double)testCaseSize;
		double kk = (double)firstScope / (double)testCaseSize;
		
		System.out.println("전체순위합 " + totalRank);
		System.out.println("전체케이스 " + testCaseSize);
		System.out.println("전체 RR " + totalRR);
		System.out.printf("현재시스템의 MRR은 %.5f 입니다.%n", mrr);
		System.out.printf("따라서, 정답이 평균 %.3f위에 랭크됩니다.%n", (double)1f/mrr);
		System.out.printf("정답이 5위권에 들어올 확률은 %.3f 퍼센트입니다.%n", (double)k*100);
		System.out.printf("정답이 1위일 확률은 %.3f 퍼센트입니다.%n", (double)kk*100);
		
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
			System.out.println("테스트시작 : " + count++);
			testQ.setMorphemes(kokoma.getPhraseList(testQ.getQuestion()));
			Vector queryVector = Vector.getVector(morphIDFmap, testQ.getMorphemes());
			System.out.println("쿼리 : " + testQ.getQuestion());
			
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
			System.out.println("현재 " + thisQrank + "등");
		}
		
		mrr = totalRR / testCaseSize;
		
		System.out.println(String.format("현재시스템의 MRR은 %.3f 입니다.", mrr));
		System.out.println(String.format("따라서, 정답이 평균 %.2f위에 랭크됩니다.", 1/mrr));
		
		return mrr;
	}
	
	public void run()
	{
		Scanner sc = new Scanner(System.in);

		while (true)
		{
			System.out.print("쿼리 입력 : ");
			String queryString = sc.nextLine();

			System.out.println(queryString);
			Question query = new Question(0, queryString, "답변", false);
			query.setMorphemes(kokoma.getPhraseList(queryString));
			Vector queryVector = Vector.getVector(morphIDFmap, query.getMorphemes());

			System.out.println("쿼리벡터 : " + queryVector);

			double[] temp점수배열 = { 0, 0, 0, 0, 0 };
			double[] temp비율arr = { 0, 0, 0, 0, 0 }; // 사용자가 입력
			boolean 기타로분류 = true;

			// 점수가 1개라도 0이 아니면 불확실 아님
			// get분류점수 - double 배열 / 띄어쓰기 0 / 발음 1 / 외래어 2 / 의미 3 / 문법 4
			if (query.get분류점수(띄어쓰기) != 0 || query.get분류점수(발음) != 0 || query.get분류점수(외래어) != 0 || query.get분류점수(의미) != 0
					|| query.get분류점수(문법) != 0)
			{
				기타로분류 = false;
				for (int i = 0; i < 5; i++)
					temp점수배열[i] = query.get분류점수(i);

				Arrays.sort(temp점수배열);

				// 위에서 정렬한 temp 리버스
				double temp;
				for (int i = 0; i < temp점수배열.length / 2; i++)
				{
					temp = temp점수배열[i];
					temp점수배열[i] = temp점수배열[(temp점수배열.length - 1) - i];
					temp점수배열[(temp점수배열.length - 1) - i] = temp;
				}
				// 리버스 끝

				int tempCnt = 1;
				for (int i = 0; i < 5; i++)
				{
					if (temp점수배열[i] > 0.0)
					{
						if ((i > 0 && temp점수배열[i] != temp점수배열[i - 1]) || i == 0)
						{
							for (int j = 0; j < 5; j++)
							{
								if (temp점수배열[i] == query.get분류점수(j))
									temp비율arr[j] = (tempCnt);
							}
						}
					} else
						break;
					tempCnt++;
				}
				// 비율 정리 완료
			}

			ArrayList<Question> resultQList = (ArrayList<Question>) qList.clone();

			int i = 0;

			for (Vector x : entireVector)
			{
				double 분류점수 = 0;
				Question q = resultQList.get(i);

				double sim = Vector.getCosSimilarity(queryVector, x);
				
				double editDistance = Distance.getSimilarity(query.getQuestion(), q.getQuestion());

				double temp = 0;
				for (int j = 0; j < 5; j++)
				{
					double 기존답변비율 = 0;
					double thisClassScore = q.get분류점수(j);

					if (50.0 <= thisClassScore && thisClassScore < 60.0)
						기존답변비율 = 1;
					else if (60.0 <= thisClassScore && thisClassScore < 70.0)
						기존답변비율 = 1.1;
					else if (70.0 <= thisClassScore && thisClassScore < 80.0)
						기존답변비율 = 1.2;
					else if (80.0 <= thisClassScore && thisClassScore < 90.0)
						기존답변비율 = 1.4;
					else if (90.0 <= thisClassScore && thisClassScore < 100.0)
						기존답변비율 = 2.0;
					else if (thisClassScore == 100.0)
						기존답변비율 = 2.4;

					if (q.get분류점수(j) != 0 && temp비율arr[j] != 0)
						분류점수 += 2 * (기존답변비율 + (double) (1 / temp비율arr[j]));
					else
						분류점수 += 0;

					temp += thisClassScore;
					if (기타로분류 && (temp == 0))
						분류점수 = 3;
				}

				q.setScore((sim * 1) + (editDistance * 0.6) + 분류점수);

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
		
		System.out.println("문제풀이시작!");
		
		int s = 1;
		for(Question query : queryList)
		{
			ArrayList<Result> thisResults = new ArrayList<Result>(10);
			System.out.println(s++ + "번째 질문의 해답을 찾고 있습니다.");
			
			query.setMorphemes(kokoma.getPhraseList(query.getQuestion()));
			Vector queryVector = Vector.getVector(morphIDFmap, query.getMorphemes());


			double[] temp점수배열 = { 0, 0, 0, 0, 0 };
			double[] temp비율arr = { 0, 0, 0, 0, 0 }; // 사용자가 입력
			boolean 기타로분류 = true;

			if (query.get분류점수(띄어쓰기) != 0 || query.get분류점수(발음) != 0 || query.get분류점수(외래어) != 0 || query.get분류점수(의미) != 0
					|| query.get분류점수(문법) != 0)
			{
				기타로분류 = false;
				for (int i = 0; i < 5; i++)
					temp점수배열[i] = query.get분류점수(i);

				Arrays.sort(temp점수배열);

				// 위에서 정렬한 temp 리버스
				double temp;
				for (int i = 0; i < temp점수배열.length / 2; i++)
				{
					temp = temp점수배열[i];
					temp점수배열[i] = temp점수배열[(temp점수배열.length - 1) - i];
					temp점수배열[(temp점수배열.length - 1) - i] = temp;
				}
				// 리버스 끝

				int tempCnt = 1;
				for (int i = 0; i < 5; i++)
				{
					if (temp점수배열[i] > 0.0)
					{
						if ((i > 0 && temp점수배열[i] != temp점수배열[i - 1]) || i == 0)
						{
							for (int j = 0; j < 5; j++)
							{
								if (temp점수배열[i] == query.get분류점수(j))
									temp비율arr[j] = (tempCnt);
							}
						}
					} else
						break;
					tempCnt++;
				}
				// 비율 정리 완료
			}

			ArrayList<Question> resultQList = (ArrayList<Question>) qList.clone();

			int i = 0;

			for (Vector x : entireVector)
			{
				double 분류점수 = 0;
				Question q = resultQList.get(i);

				double sim = Vector.getCosSimilarity(queryVector, x);
				
				double editDistance = Distance.getSimilarity(query.getQuestion(), q.getQuestion());

				double temp = 0;
				for (int j = 0; j < 5; j++)
				{
					double 기존답변비율 = 0;
					double thisClassScore = q.get분류점수(j);

					if (50.0 <= thisClassScore && thisClassScore < 60.0)
						기존답변비율 = 1;
					else if (60.0 <= thisClassScore && thisClassScore < 70.0)
						기존답변비율 = 1.1;
					else if (70.0 <= thisClassScore && thisClassScore < 80.0)
						기존답변비율 = 1.2;
					else if (80.0 <= thisClassScore && thisClassScore < 90.0)
						기존답변비율 = 1.4;
					else if (90.0 <= thisClassScore && thisClassScore < 100.0)
						기존답변비율 = 2.0;
					else if (thisClassScore == 100.0)
						기존답변비율 = 2.4;

					if (q.get분류점수(j) != 0 && temp비율arr[j] != 0)
						분류점수 += 3 * (기존답변비율 + (double) (1 / temp비율arr[j]));
					else
						분류점수 += 0;

					temp += thisClassScore;
					if (기타로분류 && (temp == 0))
						분류점수 = 3;
				}

				q.setScore((sim * 1) + (editDistance * 0.6) + 분류점수);

				resultQList.set(i++, q);
			}

			// 정렬해서 출력하기
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
			System.out.print("쿼리 입력 : ");
			String queryString = sc.nextLine();

			System.out.println(queryString);
			Question query = new Question(0, queryString, "답변", false);
			query.setMorphemes(kokoma.getPhraseList(queryString));
			Vector queryVector = Vector.getVector(morphIDFmap, query.getMorphemes());

			System.out.println("쿼리벡터 : " + queryVector);

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
