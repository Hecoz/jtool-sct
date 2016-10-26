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

package immortal.persistentScope;

import immortal.Constants;
import immortal.FrameSynchronizer;
import immortal.ImmortalEntry;
import immortal.NanoClock;
import immortal.RawFrame;
import immortal.persistentScope.transientScope.TransientDetectorScopeEntry;
import realtime.LTMemory;
import realtime.NoHeapRealtimeThread;
import realtime.PeriodicParameters;
import realtime.PriorityParameters;
import realtime.RealtimeThread;

/** 
 * This thread is the periodic real-time threads that periodically wakes-up to run the collision detector.
 * Its constructor runs in immortal memory. The instance lives in immortal memory. 
 * The thread itself runs in the persistent detector scope.
 */
public class PersistentDetectorScopeEntry extends NoHeapRealtimeThread {
  
  private final ImmortalEntry immortalEntry;

  public PersistentDetectorScopeEntry(final PriorityParameters p,
      final PeriodicParameters q, final LTMemory l, final ImmortalEntry immortalEntry)
  {
		super(p, q, null, l, null, null);
    this.immortalEntry = immortalEntry;
	}

	public boolean stop = false;

	@Override
  public void run() {

		final NoiseGenerator noiseGenerator = new NoiseGenerator();
		final LTMemory transientDetectorScope = new LTMemory(immortal.Constants.TRANSIENT_DETECTOR_SCOPE_SIZE, immortal.Constants.TRANSIENT_DETECTOR_SCOPE_SIZE);
		try {
			final TransientDetectorScopeEntry cd = new TransientDetectorScopeEntry(new StateTable(), Constants.GOOD_VOXEL_SIZE);

			if (immortal.Constants.DEBUG_DETECTOR) {
				System.out.println("Detector thread is "+Thread.currentThread());
				System.out.println("Entering detector loop, detector thread priority is "+
						+Thread.currentThread().getPriority()+
						" (NORM_PRIORITY is "+Thread.NORM_PRIORITY+
						", MIN_PRIORITY is "+Thread.MIN_PRIORITY+
						", MAX_PRIORITY is "+Thread.MAX_PRIORITY+")");  
			}

      //			while (!stop) {
      //
      //				for(;;) {
      //          final boolean madeIt = RealtimeThread.waitForNextPeriod();
      //
      //					//long now = NanoClock.now();
      //
      //					/*
      //					if (ImmortalEntry.recordedDetectorReleaseTimes < ImmortalEntry.detectorReleaseTimes.length) {
      //						ImmortalEntry.detectorReportedMiss [ ImmortalEntry.recordedDetectorReleaseTimes ] = missed;
      //						ImmortalEntry.detectorReleaseTimes[ ImmortalEntry.recordedDetectorReleaseTimes ] = now;
      //						ImmortalEntry.recordedDetectorReleaseTimes++;
      //					}
      //					*/
      //
      //          if (madeIt)
      //          {
      //            break;
      //          }
      //          immortalEntry.reportedMissedPeriods++;
      //				}
      //
      //				runDetectorInScope(cd, transientDetectorScope, noiseGenerator);
      //        
      //			}
      runDetectorInScope(cd, transientDetectorScope, noiseGenerator);
			System.out.println("Detector is finished, processed all frames.");

		} catch (final Throwable t) {
			throw new Error(t);
		}
	}

	public void runDetectorInScope(final TransientDetectorScopeEntry cd, final LTMemory transientDetectorScope, final NoiseGenerator noiseGenerator) {

		if (immortal.Constants.SYNCHRONOUS_DETECTOR) {
      throw new RuntimeException("Not expected!");
      //FrameSynchronizer.waitForProducer();
		}

    final RawFrame f = immortalEntry.frameBuffer.getFrame();
		if (f == null) {
			//ImmortalEntry.frameNotReadyCount++;
      throw new RuntimeException();
      //return;
		}

    if ((immortalEntry.framesProcessed + immortalEntry.droppedFrames) == immortal.Constants.MAX_FRAMES)
    {
			stop = true;
      throw new RuntimeException();
      //return;
		}  // should not be needed, anyway

		//final long heapFreeBefore = Runtime.getRuntime().freeMemory();
		//final long timeBefore = NanoClock.now();

		noiseGenerator.generateNoiseIfEnabled();

		cd.setFrame(f);

		// actually runs the detection logic in the given scope
		transientDetectorScope.enter(cd);

		//final long timeAfter = NanoClock.now();
		//final long heapFreeAfter = Runtime.getRuntime().freeMemory();

		/*
		if (ImmortalEntry.recordedRuns < ImmortalEntry.maxDetectorRuns) {
			ImmortalEntry.timesBefore[ ImmortalEntry.recordedRuns ] = timeBefore;
			ImmortalEntry.timesAfter[ ImmortalEntry.recordedRuns ] = timeAfter;
			ImmortalEntry.heapFreeBefore[ ImmortalEntry.recordedRuns ] = heapFreeBefore;
			ImmortalEntry.heapFreeAfter[ ImmortalEntry.recordedRuns ] = heapFreeAfter;

			ImmortalEntry.recordedRuns ++;
		}
		*/

    immortalEntry.framesProcessed++;

    if ((immortalEntry.framesProcessed + immortalEntry.droppedFrames) == immortal.Constants.MAX_FRAMES)
    {
			stop = true;
		}
	}

	@Override
  public void start() {

    immortalEntry.detectorThreadStart = 0; //NanoClock.now();
		super.start();
	}
}