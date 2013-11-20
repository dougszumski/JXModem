package org.cowboycoders.jxmodem;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author doug
 *
 */
public class JXModemCRC {

    private static final int MAX_RETRIES = 10;
    private static final int DEFAULT_PAYLOAD_SIZE = 128;
    
    private int payloadSize;

    private final InputStream input;
    private final OutputStream output;
    private final byte padding = PacketFlags.SUB;

   
    /**
     * Constructor
     * 
     * @param inputStream
     * @param outputStream
     */
    public JXModemCRC(InputStream inputStream, OutputStream outputStream) {
        input = inputStream;
        output = outputStream;
        
        payloadSize = DEFAULT_PAYLOAD_SIZE;
        
        System.out.print("Hello from JXModem: " + this.toString());
    }
    
    public JXModemCRC setPayloadSize(int newPayloadSize) {
	
	payloadSize = newPayloadSize;
	
	return this;
    }
    
    /**
     * Transmits a char array.
     * 
     * @param msg - Message as char array
     * @throws IOException
     */
    public void transmit(char[] msg) throws IOException {
	
	// TODO: Chop the message up into packets, calculate CRCS etc.
	
	// Write the packets to the outstream 
   
    }

    /**
     * Receives a message
     * 
     * @return
     * @throws Exception 
     */
    public byte[] receive() throws Exception {

        ByteArrayOutputStream collector = new ByteArrayOutputStream();

        // The first packet always starts at one. The counter is allowed to wrap over to 0.
        int expectedPacketNumber = 1;
        byte[] packetData = new byte[payloadSize];
        int marker;
        int retriesRemaining = MAX_RETRIES;

        output.write(PacketFlags.NAK);

        while ((marker = input.read()) != PacketFlags.EOT) {

            // Check for start of packet header
            if (marker != PacketFlags.SOH) {
        	error("Was expecting SOH");
            }

            if (input.read() != expectedPacketNumber) {
                error("Was expecting packet " + expectedPacketNumber);
            }

            // Byte 3 is ~(packet number)
            if (expectedPacketNumber + input.read() != 255) {
                error("Packet number check failed");
            }

            // Extract the payload and record it's length
            int len = 0;
            for (int i = 0; i < packetData.length; i++) {
                packetData[i] = (byte) input.read();
                if (packetData[i] != padding) {
                     len++;
                }
            }

            // CRC check
            int actualChecksum = ChecksumUtils.getCRC16(packetData);
            int packetChecksum = ChecksumUtils.getCRC16FromInputStream(input);

            if (actualChecksum != packetChecksum) {
        	System.out.println("Checksum failed, retrying");
                retriesRemaining--;
                if (retriesRemaining > 0) {
                    output.write(PacketFlags.NAK);
                    continue;
                } else {
                    error("Error receiving packet " + expectedPacketNumber + "; gave up after " + MAX_RETRIES + " retries");
                }
            }

            // Store the data
            collector.write(packetData, 0, len);

            retriesRemaining = MAX_RETRIES;
            output.write(PacketFlags.ACK);

            expectedPacketNumber++;

        }

        return collector.toByteArray();

    }

    private void error(String message) throws Exception {
        throw new Exception(message);
    }
}