package fse2006clean;

import org.jtool.test.ConcurrencyTestCase;

public class TestClean implements ConcurrencyTestCase {

	private int iFirstTask = 1;
	private int iSecondTask = 1;
	private int iterations = 12;

	public TestClean(int iFirstTask, int iSecondTask, int iterations) {
		this.iFirstTask = iFirstTask;
		this.iSecondTask = iSecondTask;
		this.iterations = iterations;
	}

	public void execute() throws Exception {
		Event new_event1 = new Event();
		Event new_event2 = new Event();
		
		FirstTask[] firstTasks = new FirstTask[iFirstTask];
		SecondTask[] secondTasks = new SecondTask[iSecondTask];

		for (int i = 0; i < iFirstTask; i++)
		{
			firstTasks[i] = new FirstTask(new_event1, new_event2, iterations);
			firstTasks[i].start();
		}
		for (int i = 0; i < iSecondTask; i++)
		{
			secondTasks[i] = new SecondTask(new_event1, new_event2, iterations);
			secondTasks[i].start();
		}
		
		for (int i = 0; i < iFirstTask; i++)
		{
			firstTasks[i].join();
		}
		for (int i = 0; i < iSecondTask; i++)
		{
			secondTasks[i].join();
		}
	}
}
