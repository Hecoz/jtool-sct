/* =========================================================================
 * File: Tester.java$
 *
 * Copyright 2006 by Yuriy Stepovoy.
 * email: stepovoy@gmail.com
 * All rights reserved.
 *
 * =========================================================================
 */

package net.sf.cache4j.test;

import net.sf.cache4j.CacheConfig;
import net.sf.cache4j.CacheException;
import net.sf.cache4j.Cache;
import net.sf.cache4j.impl.BlockingCache;
import net.sf.cache4j.impl.CacheConfigImpl;

import java.util.Random;

import org.jtool.test.ConcurrencyTestCase;

public class Tester implements ConcurrencyTestCase {
  
    private final int tcount;
    private final int count;
  
    public static void main(String[] args) throws Exception {
	      new Tester(20, 20000).execute();
    }

    private static class TThread extends Thread {
        final private Cache _cache;
        final private long _count;
        final private Random _rnd;

        public TThread(Cache cache, long count, long tid) {
            _cache = cache;
            _count = count;
            _rnd = new Random(tid);
        }

        public void run() {
            long count = 0;
            try {
                while(count < _count){
                    count++;
                    Object key = new Long(_rnd.nextInt(10));
                    _cache.get(key);
                    _cache.put(key, key);
                    //_cache.remove(key);
                }
            } catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        }
    }
    
    public Tester(int tcount, int count)
    {
      this.tcount = tcount;
      this.count = count;
    }


    public void execute() throws Exception
    {
      BlockingCache cache = new BlockingCache();
      CacheConfig cacheConfig = new CacheConfigImpl("cacheId", null, 0, 0, 0, 1000, null, "lru", "strong");
      try {
          cache.setCacheConfig(cacheConfig);
      } catch (CacheException e) {
          throw new RuntimeException(e);
      }

      final int ltcount = tcount;
      final int lcount = count;

      Thread[] tthreads = new Thread[ltcount];
      for (int i = 0; i < ltcount; i++) {
          tthreads[i] = new TThread(cache, lcount, ltcount);
          tthreads[i].start();
      }


      for (int i = 0; i < ltcount; i++) {
          try {
              tthreads[i].join();
          } catch(InterruptedException e) {}
      }
    }
}

