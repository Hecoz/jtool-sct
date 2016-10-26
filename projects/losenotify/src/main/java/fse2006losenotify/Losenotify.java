package fse2006losenotify;

/*
 * Created on Apr 27, 2005
*/

/**
 * @author Xuan
 * 2 synchronized, 1 wait, 1 notify
 */
public class Losenotify {
	static public Object monitor_=new Object();
	public synchronized void towait() {
		try {
			monitor_.wait();
		}catch (Exception e) {}
    }

    public synchronized void tonotify() {
    	try {
			monitor_.notifyAll();
		}catch (Exception e) {}
    }
}
