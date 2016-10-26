package fse2006losenotify;

/*
 * Created on Apr 27, 2005
*/
/**
 * @author Xuan
 */
public class NotifyThread extends Thread {
	Losenotify ln;
	int iterations;
     public NotifyThread(Losenotify ln, int iterations) {
     	this.ln=ln;
     	this.iterations=iterations;
     }
     public void run() {
		synchronized(ln.monitor_){
	        for (int i=0;i<iterations;i++) {
	        	ln.tonotify();
			}
        }
      }
}
