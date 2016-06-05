package com.sample;

public class SampleApp {

	public static void main(String[] args) {
		System.out.println("Sample test start");
		
		PeriodicWorkA lWorkA = new PeriodicWorkA();
		PeriodicWorkB lWorkB = new PeriodicWorkB();
		PeriodicWorker lWorkerA = new PeriodicWorker(200, lWorkA);
		PeriodicWorker lWorkerB = new PeriodicWorker(500, lWorkB);
		
		lWorkerA.start();
		lWorkerB.start();
		try {
			Thread.sleep(10*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		lWorkerA.stop();
		lWorkerB.stop();
		
		System.out.println("Sample test end");
	}

}
