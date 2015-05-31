package QnASystem;

public class Main
{
	public static void main(String[] args)
	{
		QnASystem system = new QnASystem("entireQuestion.dat",
										 "entireVector.dat", "IDFmapKokoma.txt");
		
		system.runForResult();
	}
}
