package fse2006reorder;

/*
 * Created on Apr 27, 2005
*/

/**
 * @author Xuan
 */
public class SetThread extends Thread {
	SetCheck sc;
	public SetThread(SetCheck sc) {
		this.sc=sc;
	}
	public void run() {
		sc.set();
	}
}
