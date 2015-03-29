package de.pueski.jrhythm.test;

import de.pueski.jrhythm.util.ImprovedNoise;

public class PerlinGen {

	public static double[][][] makePerlin(int size, double scale) {
		
		double[][][] perlin = new double[size][size][size];
		
		
		for (int x = 0; x < size; x++) {
	
			for (int y = 0; y < size; y++) {

				for (int z = 0; z < size; z++) {
					double n = ImprovedNoise.noise((double)x/scale, (double)y/scale,(double) z/scale);
					perlin[x][y][z] = n;
				}
			
			}
		}
		
		return perlin;
		
	}
	
	
}
