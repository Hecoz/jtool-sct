/**
 * 
 * Copyright (c) 2001-2010, Purdue University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   * Neither the name of the Purdue University nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package immortal;

import immortal.persistentScope.PersistentDetectorScopeEntry;
import java.io.DataOutputStream;
import realtime.LTMemory;
import realtime.PeriodicParameters;
import realtime.PriorityParameters;
import realtime.RealtimeThread;
import realtime.RelativeTime;
import realtime.Clock;
import realtime.AbsoluteTime;

/** This thread runs only during start-up to run other threads. It runs in immortal memory, 
 *  is allocated in immortal memory,
 * and it's constructor runs in immortal memory. It is a singleton, allocation from the Main class 
 * 
 * Ales:
 *     this thread allocates 
 *     -  the scopes - the PersistentDetectorScope and TransientDetectorScope
 * 
 * 
 * */
public class ImmortalEntry extends RealtimeThread {

  public Object initMonitor = new Object();
  public boolean detectorReady = false;
  public boolean simulatorReady = false;

  public int maxDetectorRuns;

  public long detectorFirstRelease = -1;
	
	//static public long[] timesBefore; 
	//static public long[] timesAfter;
	//static public long[] heapFreeBefore;
	//static public long[] heapFreeAfter;	
  public int[] detectedCollisions;
  public int[] suspectedCollisions;

  public long detectorThreadStart;
	//static public long[] detectorReleaseTimes;
  public boolean[] detectorReportedMiss;

  public int reportedMissedPeriods = 0;
	//static public int frameNotReadyCount = 0;
  public int droppedFrames = 0;
  public int framesProcessed = 0;
	//static public int recordedRuns = 0;

	//static public int recordedDetectorReleaseTimes = 0;

  public FrameBuffer frameBuffer = null;
  public PersistentDetectorScopeEntry persistentDetectorScopeEntry = null;

  public DataOutputStream binaryDumpStream = null;
	
	public ImmortalEntry() {
		super(new PriorityParameters(Constants.DETECTOR_STARTUP_PRIORITY));

		maxDetectorRuns = Constants.MAX_FRAMES;

		//timesBefore = new long[ maxDetectorRuns ]; 
		//timesAfter = new long[ maxDetectorRuns ];
		//heapFreeBefore = new long[ maxDetectorRuns ];
		//heapFreeAfter = new long[ maxDetectorRuns ];
		
		detectedCollisions = new int[ maxDetectorRuns ];
		suspectedCollisions = new int[ maxDetectorRuns ];        

		//detectorReleaseTimes = new long[ maxDetectorRuns + 10]; // the 10 is for missed deadlines
		detectorReportedMiss = new boolean[ maxDetectorRuns + 10];        
	}

	/** Called only once during initialization. Runs in immortal memory */
	@Override
  public void run() {

		System.out.println("Detector: detector priority is "+Constants.DETECTOR_PRIORITY);
		System.out.println("Detector: detector period is "+Constants.DETECTOR_PERIOD);

    frameBuffer = new FrameBuffer(this);
		
		final LTMemory persistentDetectorScope = new LTMemory(Constants.PERSISTENT_DETECTOR_SCOPE_SIZE, Constants.PERSISTENT_DETECTOR_SCOPE_SIZE);


		synchronized( initMonitor ) {

			detectorReady = true;
			initMonitor.notifyAll();        

			while (!simulatorReady) {
				try { initMonitor.wait(); } catch (final InterruptedException e) {};
			} 
		}

		/* start the detector at rounded-up time, so that the measurements are not subject
		 * to phase shift
		 */
		// we use zero delay for thread releases in checking with JPF (precise time is not relevant)
		final AbsoluteTime releaseAt = new AbsoluteTime(0, 0); // NanoClock.roundUp(Clock.getRealtimeClock().getTime().add(Constants.DETECTOR_STARTUP_OFFSET_MILLIS, 0));
		detectorFirstRelease = NanoClock.convert(releaseAt);

		persistentDetectorScopeEntry = new PersistentDetectorScopeEntry(
				new PriorityParameters(Constants.DETECTOR_PRIORITY), 
				new PeriodicParameters( releaseAt, // start
				new RelativeTime(Constants.DETECTOR_PERIOD, 0), // period
				null, //cost
				null, // deadline
            null,
            null), persistentDetectorScope, this);

		persistentDetectorScopeEntry.start();
	}
}
