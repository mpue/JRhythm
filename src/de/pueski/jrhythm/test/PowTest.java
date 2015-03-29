package de.pueski.jrhythm.test;

import org.sunflow.math.MathUtils;

public class PowTest {

//	public static void main(String[] args) {
//		
//		long start = System.currentTimeMillis();
//		
//		for (int i=0; i < 10000000;i++)  {
//			Math.pow(23123123112d,23.743892646237846178956178965d);
//		}
//		
//		long t = System.currentTimeMillis() - start;
//		
//		System.out.println("took : "+t+" ms");
//		
//	}
	
	public static void main(String[] args) {
		
		long start = System.currentTimeMillis();
		
		for (int i=0; i < 10000000;i++)  {
			MathUtils.fastPow(23123123112f,23.743892646237846178956178965f);
		}
		
		long t = System.currentTimeMillis() - start;
		
		System.out.println("took : "+t+" ms");
		
	}
	
	
}
