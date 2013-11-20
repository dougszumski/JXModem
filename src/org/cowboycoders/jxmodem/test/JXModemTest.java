package org.cowboycoders.jxmodem.test;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.cowboycoders.jxmodem.JXModemCRC;
import org.cowboycoders.jxmodem.PacketFlags;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JXModemTest {
    
    private InputStream inputStream;
    private OutputStream outputStream;
    
    @Before
    public void setUp() throws Exception {
	
	outputStream = new ByteArrayOutputStream();
    }

    /**
     * Recovers a single packet from a test message.
     * @throws UnsupportedEncodingException 
     */
    @Test
    public void receiveSinglePacket() throws UnsupportedEncodingException {
	
	// Create the test message
	byte[] testMessage = {PacketFlags.SOH, 0x01, (byte) 0xFE, '1', '2', '3', '4', 
		'5', '6', '7', '8', '9', 0x31, (byte) 0xC3, PacketFlags.EOT};
	inputStream = new ByteArrayInputStream(testMessage);
	
	JXModemCRC xModem = new JXModemCRC(inputStream, outputStream);
	xModem.setPayloadSize(9);
	
	// Check the message is recovered
	byte[] message = null;
	try {
	    message =  xModem.receive();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	
	byte[] expected = "123456789".getBytes("ASCII");
	Assert.assertArrayEquals(expected, message);
    }
}
