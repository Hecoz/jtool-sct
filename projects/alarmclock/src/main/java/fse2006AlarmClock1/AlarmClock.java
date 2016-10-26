package fse2006AlarmClock1;

import org.jtool.test.ConcurrencyTestCase;

/**
 * This is a simulation of an Alarm Clock. One thread of kind Clock calls a
 * method tick() every milisecond (or any other unit of time) and updates the
 * clock. Method tick() is a part of class Monitor to insure that when the
 * thread changes current time, noone is there to interfere. Other threads of
 * kind Client call method wakeme(int interval) when they want to sleep for
 * interval. A list of locks is maintained for Client threads. Each thread
 * calculates its waketime and goes to sleep on a lock with a key that's equal
 * to wakeup time.
 * 
 * Starts up one Clock thread and numThreads of kind Client.
 * 
 * Modified from the original to take out the System.out calls and the
 * references to String. Also modified to take out sync method calls.
 * 
 * @author Oksana Tkachuk
 * @author Todd Wallentine &lt;tcw@cis.ksu.edu&gt;
 * @version $Revision: 1.2 $ - $Date: 2004/04/08 14:46:35 $
 */
public class AlarmClock implements ConcurrencyTestCase
{
  public static void main(final String[] args) throws Exception
  {
    new AlarmClock().execute();
  }
  
  public void execute() throws Exception
  {
    final int maxTime = 5;
    final Monitor m = new Monitor(maxTime);
    final Clock clock = new Clock(1, m, maxTime);
    clock.start();

    final Client c1 = new Client(2, m, 1);
    final Client c2 = new Client(3, m, 2);
    c1.start();
    c2.start();
    
    clock.join();
    c1.join();
    c2.join();
  }
}


class Clock extends Thread
{
  private final int name;
  private final Monitor monitor;
  private final int max;
  
  /**
   * Class constructor specifying the name of the thread, the Monitor, and the
   * maxTime
   */
  public Clock(final int n, final Monitor m, final int maxTime)
  {
    name = n;
    monitor = m;
    max = maxTime;
  }
  
  /**
   * Calls tick() untill current time of Clock reaches maxTime. After calling
   * tick() puts a thread to sleep for one unit of time.
   */
  @Override
  public void run()
  {
    while (monitor.getTime() < max)
    {
      monitor.tick();
      
      //      try
      //      {
      //        Thread.sleep(10);
      //      }
      //      catch (final InterruptedException e)
      //      {
      //      }
    }
  }
}


class Client extends Thread
{
  private final int name;
  private final Monitor monitor;
  private final int id;
  
  /**
   * Class constructor specifying the name of the thread, the Monitor that
   * provides method wakeme(int id, int interval), and id number of this thread.
   */
  public Client(final int n, final Monitor m, final int i)
  {
    name = n;
    monitor = m;
    id = i;
  }
  
  /**
   * Randomly generates the interval the thread wants to sleep and calls the
   * Monitor method wakeme(int id, int interval).
   */
  @Override
  public void run()
  {
    final int maxSleepTime = 5;
    int interval;
    interval = 2;
    monitor.wakeme(id, interval);
  }
}


class Monitor
{
  private int now;
  private final MyLinkedList waitList;
  private final int max;
  
  // no additional synchronization used
  
  /**
   * Class constructor Creates an empty list that will keep specific
   * notification locks. Each notification lock will have a key field called
   * time, which will be equal to the wakeup time of the thread.
   */
  Monitor(final int maxTime)
  {
    now = 0;
    waitList = new MyLinkedList();
    max = maxTime;
  }
  
  /**
   * Increments the time by one. If the list of notification locks is not empty
   * and the head of the list needs to be awaken, removes the head of the list
   * and notifies everyone sleeping on that lock.
   */
  void tick()
  {
    synchronized (this)
    {
      now++;
      
      if (!waitList.isEmpty())
      {
        final MyObject first = (MyObject) waitList.firstElement();
        
        if (first.time() == now)
        {
          final MyObject wakeup = first;
          waitList.removeElementAt(0);
          
          synchronized (wakeup)
          {
            wakeup.notifyAll();
          }
        }
      }
    }
  }
  
  void wakeme(final int id, final int interval)
  {
    int waketime;
    MyObject lock;

    /**
     * First part of this method calculates the waketime and creates a specific
     * notification lock
     */
    synchronized (this)
    {
      waketime = now + interval;
      
      if (waketime > max)
      {
        return;
      }
      
      lock = waitList.createLock(waketime);
    }

    /**
     * Second part of the wakeme(int,int) method. Synchronized on the specific
     * notification lock with the key that's equal waketime. Puts a thread to
     * sleep on the specific lock. Since while a thread is trying to go to sleep
     * on the specific lock, it's possible for a Clock thread to enter the
     * monitor at this time and wake up the head of the list. If the thread
     * wanted to sleep on the head of the list or close to the head of the list,
     * it's possible to be late. If a thread wants to sleep on the lock that's
     * been awakened already that it doesn't need to sleep at all. This thread's
     * waketime has expired. For this case try{}catch clause is used. If a
     * thread wants to sleep on the lock that's been removed already
     * IndexOutOfBounds Exception is caught and the thread that is late is
     * considered awaken.
     */
    try
    {
      synchronized (waitList.getLock(waketime))
      {
        try
        {
          waitList.getLock(waketime).wait();
        }
        catch (final InterruptedException e)
        {
        }
      }
    }
    catch (final IndexOutOfBoundsException e)
    {
    }
  }
  
  /**
   * Gets the current time of the clock.
   */
  int getTime()
  {
    return now;
  }
}


/**
 * Implements the characteristics of the specific notification locks. Gives a
 * lock a field called time (used as a key value to identify a specific lock).
 */
class MyObject
{
  public int time;
  
  MyObject(final int n)
  {
    time = n;
  }
  
  public int time()
  {
    return time;
  }
}


/**
 * Implements the characteristics of the list needed to hold specific
 * notification locks.
 */
class MyLinkedList
{
  private final Object[] list = new Object[2];
  private final int capacity = 2;
  private int size = 0;
  
  public boolean isEmpty()
  {
    return size == 0;
  }
  
  public boolean isFull()
  {
    return size == capacity;
  }
  
  /**
   * Returns the component at the specified index
   */
  public Object elementAt(final int index)
  {
    return list[index];
  }
  
  /**
   * Inserts the specified object as a component in this array at the specified
   * index.
   */
  public void insertElementAt(final Object obj, final int index)
  {
    if (!this.isFull())
    {
      synchronized (list)
      {
        for (int i = size - 1; i >= index; i--)
        {
          list[i + 1] = list[i];
        }
        
        list[index] = obj;
        size++;
      }
    }
  }
  
  /**
   * Adds the specified component to the end of this array, increasing its size
   * by one.
   */
  public void addElement(final Object obj)
  {
    if (!this.isFull())
    {
      synchronized (this)
      {
        list[size] = obj;
        size++;
      }
    }
  }
  
  /**
   * Returns the first component of this array
   */
  public Object firstElement()
  {
    return list[0];
  }
  
  /**
   * Deletes the component at the specified index
   */
  public void removeElementAt(final int index)
  {
    if (!this.isEmpty())
    {
      synchronized (this)
      {
        for (int i = index; i < (size - 1); i++)
        {
          list[i] = list[i + 1];
        }
        
        list[size - 1] = null;
        size--;
      }
    }
  }
  
  /**
   * Given a key value n finds and returns a lock with that value in the list.
   * If that value doesn't exist creates a new lock with a key value n, inserts
   * it into the list in appropriate order and returns it.
   */
  MyObject createLock(final int n)
  {
    MyObject temp;
    int key;
    
    for (int i = 0; i < size; i++)
    {
      synchronized (this)
      {
        if (list[i] != null)
        {
          key = ((MyObject) list[i]).time;
          
          if (n == key)
          {
            temp = (MyObject) list[i];
            
            return temp;
          }
        }
      }
    }
    
    final MyObject lock = new MyObject(n);
    
    for (int i = 0; i < size; i++)
    {
      synchronized (this)
      {
        if (list[i] != null)
        {
          key = ((MyObject) list[i]).time;
          
          if (n < key)
          {
            this.insertElementAt(lock, i);
            
            return lock;
          }
        }
      }
    }
    
    this.addElement(lock); //append if greater than all others   
    
    return lock;
  }
  
  MyObject getLock(final int n)
  {
    MyObject temp;
    int m;
    
    for (int i = 0; i < size; i++)
    {
      synchronized (this)
      {
        if (list[i] != null)
        {
          m = ((MyObject) list[i]).time;
          
          if (n == m)
          {
            temp = (MyObject) list[i];
            
            return temp;
          }
        }
      }
    }
    
    throw new IndexOutOfBoundsException();
  }
}
