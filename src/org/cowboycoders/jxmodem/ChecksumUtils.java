package org.cowboycoders.jxmodem;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author doug
 *
 */
public final class ChecksumUtils {
    
    /**
     * Prevent instantiation
     */
    private ChecksumUtils() {
	
    }
    
    /**
     * Calculates the CRC-CCITT (XModem style) checksum for the specified array.
     * 
     * @param byteArray to calculate checksum for.
     * 
     * @return 16 bit checksum.
     */
    public static int getCRC16(final byte[] byteArray) {
	
	int crc = 0x0000;

	for (byte b: byteArray) {
	    crc = ((crc >>> 8) | (crc << 8)) & 0xFFFF;
	    crc ^= b & 0xFF;
	    crc ^= (crc & 0xFF) >> 4;
	    crc ^= (crc << 12) & 0xFFFF;
	    crc ^= ((crc & 0xFF) << 5);
	}
	
	// Keep only the first two bytes
	crc &= 0xFFFF;
	
	return crc;
    }
    
    /**
     * Combines two consecutive bytes into a 16bit value from the specified input stream.
     * 
     * @param inputStream to read bytes from.
     * @return 16 bit checksum
     * @throws IOException
     */
    public static int getCRC16FromInputStream(InputStream inputStream) throws IOException {
	
	return (inputStream.read() << 8) + inputStream.read();
    }
}
    
   

