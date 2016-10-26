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

package heap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

import immortal.Constants;
import immortal.ImmortalEntry;
import immortal.NanoClock;

/**
 * Real-time Java runner for the collision detector.
 */
public class Main {

	public static final boolean PRINT_RESULTS = false;

	
	// NOTE: args = {unused,MAX_FRAMES,5,DETECTOR_PERIOD,50,DETECTOR_PRIORITY,9}
  public static void main(final String[] v) throws Exception
  {

		parse(v);
		NanoClock.init();

		final ImmortalEntry immortalEntry = new ImmortalEntry();
    
		final Thread specThread = null;


		if (Constants.FRAMES_BINARY_DUMP) {
			try {
        immortalEntry.binaryDumpStream =
            new DataOutputStream(new FileOutputStream("frames.bin"));
        immortalEntry.binaryDumpStream.writeInt(immortal.Constants.MAX_FRAMES);

			} catch (final FileNotFoundException e) {
				throw new RuntimeException("Cannot create output file for the binary frames dump: " + e);
			} catch (final IOException e) {
				throw new RuntimeException("Error writing header to file for the binary frames dump: " +e);
			}
		}

		
		// will simulate the frames
		// using a regular java.lang.Thread
		final Thread simulatorThread = new Thread() {

			@Override
      public void run() {
				try {
          Simulator.generate(v, immortalEntry);
				} catch (final Throwable t) {
					throw new Error(t);
				}
			}
		};
    //simulatorThread.setDaemon(true);
    //simulatorThread.setPriority(Constants.SIMULATOR_PRIORITY);

		simulatorThread.start();
		
		//thread running only during the initialization
		immortalEntry.start();

		simulatorThread.join();
		
		if (!immortal.Constants.SIMULATE_ONLY) {
			immortalEntry.joinReal(); 
      immortalEntry.persistentDetectorScopeEntry.joinReal();
      dumpResults(immortalEntry);
		}
		
		if (Constants.FRAMES_BINARY_DUMP) {
			try {
        immortalEntry.binaryDumpStream.close();
			} catch (final IOException e) {
				throw new RuntimeException("Cannot close file with binary frames dump "+e);
			}
		}

		// the SPEC thread can still be running, but "stop" is not implemented
		// in Ovm
    //		System.exit(0);
	}

	
	
	
	
	
  public static void dumpResults(final ImmortalEntry immortalEntry)
  {

		/*
		if (PRINT_RESULTS) {
			System.out
			.println("Dumping output [ timeBefore timeAfter heapFreeBefore heapFreeAfter detectedCollisions ] for "
					+ ImmortalEntry.recordedRuns
					+ " recorded detector runs, in ns");
		}
		*/

		final PrintWriter out = null;

		/*
		if (immortal.Constants.DETECTOR_STATS != "") {
			try {
				out = new PrintWriter(new FileOutputStream(
						immortal.Constants.DETECTOR_STATS));
			} catch (FileNotFoundException e) {
				System.out
				.println("Failed to create output file for detector statistics ("
						+ immortal.Constants.DETECTOR_STATS + "): " + e);
			}
		}

		for (int i = 0; i < ImmortalEntry.recordedRuns; i++) {
			String line = NanoClock.asString(ImmortalEntry.timesBefore[i])
			+ " " + NanoClock.asString(ImmortalEntry.timesAfter[i])
			+ " " + ImmortalEntry.heapFreeBefore[i] + " "
			+ ImmortalEntry.heapFreeAfter[i] + " "
			+ ImmortalEntry.detectedCollisions[i] + " "
			+ ImmortalEntry.suspectedCollisions[i];

			if (out != null) {
				out.println(line);
			}
			if (PRINT_RESULTS) {
				System.err.println(line);
			}
		}

		if (out != null) {
			out.close();
			out = null;
		}
		*/

		System.out
		.println("Generated frames: " + immortal.Constants.MAX_FRAMES);
		//System.out.println("Received (and measured) frames: "
		//		+ ImmortalEntry.recordedRuns);
		//System.out.println("Frame not ready event count (in detector): "
		//		+ ImmortalEntry.frameNotReadyCount);
		System.out.println("Frames dropped due to full buffer in detector: "
        + immortalEntry.droppedFrames);
		System.out.println("Frames processed by detector: "
        + immortalEntry.framesProcessed);
		System.out.println("Detector stop indicator set: "
        + immortalEntry.persistentDetectorScopeEntry.stop);
		System.out
		.println("Reported missed detector periods (reported by waitForNextPeriod): "
            + immortalEntry.reportedMissedPeriods);
		System.out.println("Detector first release was scheduled for: "
        + NanoClock.asString(immortalEntry.detectorFirstRelease));

		// heap measurements
		Simulator.dumpStats();

		/*
		// detector release times
		if (immortal.Constants.DETECTOR_RELEASE_STATS != "") {

			try {
				out = new PrintWriter(new FileOutputStream(
						immortal.Constants.DETECTOR_RELEASE_STATS));
			} catch (FileNotFoundException e) {
				System.out
				.println("Failed to create output file for detector release statistics ("
						+ immortal.Constants.DETECTOR_RELEASE_STATS
						+ "): "
						+ e);
			}

			for (int i = 0; i < ImmortalEntry.recordedDetectorReleaseTimes; i++) {
				// real expected
				String line = NanoClock
				.asString(ImmortalEntry.detectorReleaseTimes[i])
				+ " ";

				line = line
				+ NanoClock.asString(i
						* immortal.Constants.DETECTOR_PERIOD * 1000000L
						+ ImmortalEntry.detectorFirstRelease);

				line = line + " "
				+ (ImmortalEntry.detectorReportedMiss[i] ? "1" : "0");
				if (out != null) {
					out.println(line);
				}
			}

			if (out != null) {
				out.close();
				out = null;
			}
		}
		*/

	}

	private static void parse(final String[] v) {
		for (int i = 1; i < v.length; i++) {
			if (v[i].equals("PERSISTENT_DETECTOR_SCOPE_SIZE")) { /* flags with parameters */
				Constants.PERSISTENT_DETECTOR_SCOPE_SIZE = Long
				.parseLong(v[i + 1]);
				i++;
			} else if (v[i].equals("DETECTOR_PERIOD")) {
				Constants.DETECTOR_PERIOD = Long.parseLong(v[i + 1]);
				i++;
			} else if (v[i].equals("TRANSIENT_DETECTOR_SCOPE_SIZE")) {
				Constants.TRANSIENT_DETECTOR_SCOPE_SIZE = Long
				.parseLong(v[i + 1]);
				i++;
			} else if (v[i].equals("DETECTOR_PRIORITY")) {
				Constants.DETECTOR_PRIORITY = Integer.parseInt(v[i + 1]);
				Constants.DETECTOR_STARTUP_PRIORITY = Constants.DETECTOR_PRIORITY - 1;
				i++;
			} else if (v[i].equals("SIMULATOR_PRIORITY")) {
				Constants.SIMULATOR_PRIORITY = Integer.parseInt(v[i + 1]);
				i++;
			} else if (v[i].equals("MAX_FRAMES")) {
				Constants.MAX_FRAMES = Integer.parseInt(v[i + 1]);
				i++;
			} else if (v[i].equals("SIMULATOR_TIME_SCALE")) {
				Constants.SIMULATOR_TIME_SCALE = Integer.parseInt(v[i + 1]);
				i++;
			} else if (v[i].equals("BUFFER_FRAMES")) {
				Constants.BUFFER_FRAMES = Integer.parseInt(v[i + 1]);
				i++;
			} else if (v[i].equals("SIMULATOR_FPS")) {
				Constants.SIMULATOR_FPS = Integer.parseInt(v[i + 1]);
				i++;
			} else if (v[i].equals("DETECTOR_NOISE_REACHABLE_POINTERS")) {
				Constants.DETECTOR_NOISE_REACHABLE_POINTERS = Integer
				.parseInt(v[i + 1]);
				i++;
			} else if (v[i].equals("DETECTOR_NOISE_ALLOCATION_SIZE")) {
				Constants.DETECTOR_NOISE_ALLOCATION_SIZE = Integer
				.parseInt(v[i + 1]);
				i++;
			} else if (v[i].equals("DETECTOR_NOISE_ALLOCATE_POINTERS")) {
				Constants.DETECTOR_NOISE_ALLOCATE_POINTERS = Integer
				.parseInt(v[i + 1]);
				i++;
			} else if (v[i].equals("DETECTOR_NOISE_MIN_ALLOCATION_SIZE")) {
				Constants.DETECTOR_NOISE_MIN_ALLOCATION_SIZE = Integer
				.parseInt(v[i + 1]);
				i++;
			} else if (v[i].equals("DETECTOR_NOISE_MAX_ALLOCATION_SIZE")) {
				Constants.DETECTOR_NOISE_MAX_ALLOCATION_SIZE = Integer
				.parseInt(v[i + 1]);
				i++;
			} else if (v[i].equals("DETECTOR_NOISE_ALLOCATION_SIZE_INCREMENT")) {
				Constants.DETECTOR_NOISE_ALLOCATION_SIZE_INCREMENT = Integer
				.parseInt(v[i + 1]);
				i++;
			} else if (v[i].equals("DETECTOR_STARTUP_OFFSET_MILLIS")) {
				Constants.DETECTOR_STARTUP_OFFSET_MILLIS = Integer
				.parseInt(v[i + 1]);
				i++;
			} else if (v[i].equals("SPEC_NOISE_ARGS")) {
				Constants.SPEC_NOISE_ARGS = v[i + 1];
				i++;
			} else if (v[i].equals("SYNCHRONOUS_DETECTOR")) { /*
			 * flags without
			 * a parameter
			 */
				Constants.SYNCHRONOUS_DETECTOR = true;
			} else if (v[i].equals("PRESIMULATE")) {
				Constants.PRESIMULATE = true;
			} else if (v[i].equals("FRAMES_BINARY_DUMP")) {
				Constants.FRAMES_BINARY_DUMP = true;
			} else if (v[i].equals("DEBUG_DETECTOR")) {
				Constants.DEBUG_DETECTOR = true;
			} else if (v[i].equals("DUMP_RECEIVED_FRAMES")) {
				Constants.DUMP_RECEIVED_FRAMES = true;
			} else if (v[i].equals("DUMP_SENT_FRAMES")) {
				Constants.DUMP_SENT_FRAMES = true;
			} else if (v[i].equals("USE_SPEC_NOISE")) {
				Constants.USE_SPEC_NOISE = true;
			} else if (v[i].equals("SIMULATE_ONLY")) {
				Constants.SIMULATE_ONLY = true;
			} else if (v[i].equals("DETECTOR_NOISE")) {
				Constants.DETECTOR_NOISE = true;
			} else if (v[i].equals("DETECTOR_NOISE_VARIABLE_ALLOCATION_SIZE")) {
				Constants.DETECTOR_NOISE_VARIABLE_ALLOCATION_SIZE = true;
			}
      else
      {
        throw new Error("Unrecognized option: " + v[i]);
      }
		}
	}
}
