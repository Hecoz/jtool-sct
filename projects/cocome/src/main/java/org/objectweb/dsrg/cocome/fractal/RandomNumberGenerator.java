package org.objectweb.dsrg.cocome.fractal;

import java.util.Random;

public class RandomNumberGenerator {
	private final double[] intervals;
	private double[] probabilityBoundary;
	private final Random random;
	
	private void initializeProbabilityBoundary(final double[] probabilities) {
		probabilityBoundary = new double[probabilities.length + 1];
		probabilityBoundary[0] = probabilities[0];
		for (int i = 1; i < probabilities.length; i++) {
			probabilityBoundary[i] = probabilityBoundary[i - 1] + probabilities[i];
		}
	}
	
	public RandomNumberGenerator(final double[] intervals, final double[] probabilities, final long seed) {
		this.intervals = intervals;
		initializeProbabilityBoundary(probabilities);
		random = new Random(seed);
	}
	
	public RandomNumberGenerator(final double[] intervals, final double[] probabilities) {
		this.intervals = intervals;
		initializeProbabilityBoundary(probabilities);
		random = new Random();
	}
	
	public double next() {
		final double rnd = random.nextDouble();
		int index;
		for (index = 0; index < probabilityBoundary.length; index++) {
			if (rnd < probabilityBoundary[index])
      {
        break;
      }
		}
		
		return intervals[index] +  random.nextDouble() * (intervals[index + 1] - intervals[index]); 
	}
}
