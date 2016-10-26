package fse2006reorder;

import org.jtool.test.ConcurrencyTestCase;

public class TestReorder implements ConcurrencyTestCase {
	private final int iset;
	private final int icheck;
	
	public TestReorder(int iset, int icheck) {
		this.iset = iset;
		this.icheck = icheck;
	}
	
	public void execute() throws Exception {
		ReorderTest.main(iset, icheck);
	}
}
