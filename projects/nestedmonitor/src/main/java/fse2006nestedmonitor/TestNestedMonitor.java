package fse2006nestedmonitor;

import org.jtool.test.ConcurrencyTestCase;

public class TestNestedMonitor implements ConcurrencyTestCase {

	private final int size;
	
	public TestNestedMonitor(int size) {
		this.size = size;
	}

	public void execute() throws Exception {
		Buffer buf = new SemaBuffer(size);

		Producer p = new Producer(buf, size);
		Consumer c = new Consumer(buf, size);
		p.start();
		c.start();
		
		p.join();
		c.join();
	}
}
