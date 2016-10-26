package fse2006DiningPhil;

/**
 * A model of the dining philosophers problem using local fork objects.
 *
 * To fix this problem, let p1 take fork1 then fork4 and let p3 take fork3 then fork2.
 * Refactored version of dp4
 * In this version the number of forks and philosophers is specified
 * on the command line (default is 5 if no arguments are specified)
 *
 * @author Todd Wallentine tcw AT cis ksu edu
 * @version $Revision: 1.1 $ - $Date: 2003/12/09 23:03:18 $
 */
public class DiningPhilosophers {
    public static void main(int num) {
		
//        int num = 5;
//        if((args != null) && (args.length > 0)) {
//        	num = Integer.parseInt(args[0]);
//        }
		
		Fork forks []=new Fork[num];
    	for (int i=0;i<num;i++) {
    		forks[i]=new Fork();
    	}

		Philosopher phils[] = new Philosopher[num];
		for (int i=0; i<num; i++){
			if (i==0)
				phils[i] = new Philosopher(forks[num-1],forks[i]);
			else
				phils[i] = new Philosopher(forks[i-1],forks[i]);
		}

		for (int i=0; i<num; i++){
			phils[i].start();
		}
    }
}

class Fork {
}

class Philosopher extends Thread {
    public Fork rightFork;
    public Fork leftFork;

    public Philosopher(Fork rightFork, Fork leftFork) {
        this.rightFork = rightFork;
        this.leftFork = leftFork;
    }

    public void run() {
//        while (true) {
            synchronized (rightFork) {
                synchronized (leftFork) {
                    // eat for a random period of time
                }
            }

            // philosophize for a random period of time
        }
//    }
}
