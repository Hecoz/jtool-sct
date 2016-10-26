package fse2006losenotify;

/*
 * Created on Apr 27, 2005
*/
/**
 * @author Xuan
 * 
Test Case 1  		(Key = 1.1.2.)
   number of waitthreads   :  1
   number of notifythreads :  1
   number of iterations    :  n = number of threads *(waits+notify+monitor)
 */
public class Main {
    static int iWait=1;
    static int iNotify=1;
    static int iterations=12;
    
    public void run() {
    	Losenotify ln=new Losenotify();    
    	for (int i=0;i<iWait;i++) 
    		new WaitThread(ln,iterations).start();
    	for (int i=0;i<iNotify;i++) 
    		new NotifyThread(ln,iterations).start();
    }
    
    public static void main(String[] args) {
		if (args.length < 3){
			System.out.println("ERROR: Expected 3 parameters");
		}else{
		    iWait= Integer.parseInt(args[0]);;
		    iNotify= Integer.parseInt(args[1]);;
		    iterations= Integer.parseInt(args[2]);;
			Main t= new Main();
			t.run();
		}
	}
}
