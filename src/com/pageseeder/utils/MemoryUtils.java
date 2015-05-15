package com.pageseeder.utils;

public class MemoryUtils {
	public static long calculateMenoryUsageInBytes(){
		Runtime runtime = Runtime.getRuntime();
		long memory = runtime.totalMemory() - runtime.freeMemory();
		return memory;
	}

	public static long calculateMenoryUsageInMegaBytes(){
		return ByteUtils.bytesToMegabytes(calculateMenoryUsageInBytes());
	}
}
