package ru.task;

import ru.task.services.DataProcessing;

public class Main {

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		DataProcessing dataProcessing = new DataProcessing();
        dataProcessing.readFile();

		dataProcessing.clearGroupWithoutPair();
		dataProcessing.finalGrouping();
		dataProcessing.print();

		System.out.println(((System.currentTimeMillis() - start) / 1000f) + " seconds");
	}
}