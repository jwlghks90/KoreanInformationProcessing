package QnASystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import QnASystem.Vector.Weight;

public class FileIO
{
	public final static String Hannanum_PATH = "C:/Users/JM/Desktop/analyzer/JHannanum";
	public final static String Data_PATH = "E:/NaturalLanguageProcessing/3-1/qnaData/";

	public static void writeResultFile(ArrayList<ArrayList<Result>> resultList, String path)
	{
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(path + "\\result.xml"));

			int i=0;
			for(ArrayList<Result> results : resultList)
			{
				out.write(String.format("<query><qnum>%d</qnum>", i++ +1));
				out.newLine();
				out.write("<rank>");
				out.newLine();
				for(Result x : results)
				{
					out.write(String.format("%d\t%.4f", x.qnum, x.score));
					out.newLine();
				}
				out.write("</rank>");
				out.newLine();
				out.write("</query>");
				out.newLine();
			}
			
			out.flush();
			out.close();

		}
		catch (Exception e){e.printStackTrace();}
	}
	
	public static ArrayList<Question> inputQueryFile(String queryFile)
	{
		StringBuilder doc = new StringBuilder();
		
		FileInputStream file;
		InputStreamReader reader;
		try
		{
			file = new FileInputStream(queryFile);
			reader = new InputStreamReader(file);

			BufferedReader in = new BufferedReader(reader);
			String line;
			while ((line = in.readLine()) != null)
			{
				doc.append(line);
				doc.append("\n");
			}
		}
		catch (Exception e){}

		ArrayList<Question> queryList = new ArrayList<Question>();

		String nKey = "<qnum>";
		String nKeyl = "<qnum>|</qnum>";
		String qKey = "<text>";
		String qKeyl = "<text>|</text>";
		
		Scanner nparser = new Scanner(doc.toString());
		Scanner qparser = new Scanner(doc.toString());
		while (true)
		{
			nparser.useDelimiter(nKey);
			qparser.useDelimiter(qKey);
			nparser.next();
			qparser.next();
			nparser.useDelimiter(nKeyl);
			qparser.useDelimiter(qKeyl);
			
			if(nparser.hasNextInt() == false) break;

			int qnum = nparser.nextInt();
			String text = qparser.next();

			Question queryObject = new Question(qnum, text, "´äº¯", false);

			queryList.add(queryObject);
		}
		nparser.close();
		qparser.close();
		
		return queryList;
	}

	public static HashMap<Morpheme, Double> getMorphIDFmap(String fileName)
	{
		HashMap<Morpheme, Double> morphIDFmap = new HashMap<Morpheme, Double>();
		try
		{
			File f = new File(fileName);
			Scanner s = new Scanner(f, "UTF-8");

			s.next();
			double docSetSize = s.nextDouble();
			morphIDFmap.put(new Morpheme(Weight.DOC_SET_SIZE, Weight.DOC_SET_SIZE_TAG),
					(double)docSetSize);

			while (s.hasNextLine() == true)
			{
				String morpheme;
				String tag;
				double score;

				morpheme = s.next();
				tag = s.next();
				score = s.nextDouble();

				morphIDFmap.put(new Morpheme(morpheme, tag), score);
			}

			s.close();
		} catch (Exception e)
		{
			System.out.print("Error : ");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		return morphIDFmap;
	}
		
	public static Question getQuestionFromFile(String fileName) throws FileNotFoundException
	{
		String document = getFileString(fileName);
		Question question = getQuestionFromDocString(document);
		
		return question;
	}
	
	private static String getFileString(String fileName) throws FileNotFoundException
	{
		StringBuilder doc = new StringBuilder();
		
		FileInputStream file = new FileInputStream(fileName);
		InputStreamReader reader;
		try
		{
			reader = new InputStreamReader(file, "UTF-8");

			BufferedReader in = new BufferedReader(reader);
			String line;
			while ((line = in.readLine()) != null)
			{
				doc.append(line);
				doc.append("\n");
			}
		}
		catch (Exception e){}
		
		return doc.toString();
	}
	
	private static Question getQuestionFromDocString(String document)
	{
		String nKey = "<number>";
		String nKeyl = "<number>|</number>";
		String cKey = "<question>";
		String cKeyl = "<question>|</question>";
		String fKey = "<answer>";
		String fKeyl = "<answer>|</answer>";
		String sKey = "<sim>";
		String sKeyl = "<sim>|</sim>";

		Scanner nparser = new Scanner(document);
		Scanner cparser = new Scanner(document);
		Scanner fparser = new Scanner(document);
		Scanner sparser = new Scanner(document);

		nparser.useDelimiter(nKey);
		cparser.useDelimiter(cKey);
		fparser.useDelimiter(fKey);
		sparser.useDelimiter(sKey);
		
		nparser.next();
		cparser.next();
		fparser.next();
		sparser.next();
		nparser.useDelimiter(nKeyl);
		cparser.useDelimiter(cKeyl);
		fparser.useDelimiter(fKeyl);
		sparser.useDelimiter(sKeyl);
		int number = nparser.nextInt();
		String category = cparser.next();
		String answer = fparser.next();
		String sim = null;
		if(sparser.hasNext()) sim = sparser.next();

		nparser.close();
		cparser.close();
		fparser.close();
		sparser.close();

		return new Question(number, sim, category, answer);
	}
}
