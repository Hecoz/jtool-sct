package fse2006reorder;

public class ReorderTest {
    static int iSet=2;
    static int iCheck=2;

	public void run() {
		SetThread[] sts = new SetThread[iSet];
		CheckThread[] cts = new CheckThread[iCheck];
		SetCheck sc=new SetCheck();
		for (int i=0;i<iSet;i++) {
		    (sts[i] = new SetThread(sc)).start();
		}
		for (int i=0;i<iCheck;i++) {
		    (cts[i] = new CheckThread(sc)).start();
		}
		try {
   		    for (int i=0;i<iSet;i++) {
		        sts[i].join();
		    }
		    for (int i=0;i<iCheck;i++) {
		        cts[i].join();
		    }
		} catch(java.lang.InterruptedException ie) {
		}
	}

	public static void main(int iset, int icheck) {
		iSet = iset;
		iCheck = icheck;
		ReorderTest t = new ReorderTest();
		t.run();
	}
}
