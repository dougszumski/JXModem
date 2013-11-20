package org.cowboycoders.jxmodem.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import junit.framework.Assert;

import org.cowboycoders.jxmodem.ChecksumUtils;
import org.junit.Test;

public class ChecksumUtilsTest {

    /**
     * CRC Tests validated with:
     * http://www.lammertbies.nl/comm/info/crc-calculation.html
     * 
     * @throws UnsupportedEncodingException
     */
    @Test
    public void getCRC16Test() throws UnsupportedEncodingException {

	int result = ChecksumUtils.getCRC16("123456789".getBytes("ASCII"));
	Assert.assertEquals(0x31C3, result);

	result = ChecksumUtils.getCRC16("0".getBytes("ASCII"));
	Assert.assertEquals(0x3653, result);

	result = ChecksumUtils.getCRC16("00000000".getBytes("ASCII"));
	Assert.assertEquals(0xE6B9, result);

	result = ChecksumUtils.getCRC16("Hello World".getBytes("ASCII"));
	Assert.assertEquals(0x992A, result);
    }
    
    /**
     * Checks recombination of 16 bit checksum.
     * 
     * @throws IOException
     */
    @Test
    public void getCRC16FromInputStreamTest() throws IOException {
	
	byte[] checksum = {0x12, 0x34};
	InputStream inputStream = new ByteArrayInputStream(checksum);
	
	Assert.assertEquals(0x1234, ChecksumUtils.getCRC16FromInputStream(inputStream));
	
	byte[] checksum2 = {(byte) 0xFF, (byte) 0xEE};
	inputStream = new ByteArrayInputStream(checksum2);
	
	Assert.assertEquals(0xFFEE, ChecksumUtils.getCRC16FromInputStream(inputStream));
	
    }

}
