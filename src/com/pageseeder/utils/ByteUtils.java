package com.pageseeder.utils;

/**
 *
 * @author Carlos Cabral
 * @version 09 Jan 2015
 */
public class ByteUtils {
  private static final long MEGABYTE = 1024L * 1024L;

  public static long bytesToMegabytes(long bytes) {
  	return bytes / MEGABYTE;
  }
}
