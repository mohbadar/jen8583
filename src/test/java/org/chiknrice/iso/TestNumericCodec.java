/**
 * 
 */
package org.chiknrice.iso;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.chiknrice.iso.codec.NumericCodec;
import org.chiknrice.iso.config.ComponentDef.Encoding;
import org.junit.Test;

/**
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 *
 */
public class TestNumericCodec {

    @Test
    public void testEncodeChar() {
        NumericCodec codec = new NumericCodec(Encoding.CHAR);
        ByteBuffer buf = ByteBuffer.allocate(20);
        codec.encode(buf, 1234);
        String encoded = new String(buf.array(), 0, 4, StandardCharsets.ISO_8859_1);
        assertEquals("1234", encoded);
    }

    @Test
    public void testEncodeFixedChar() {
        NumericCodec codec = new NumericCodec(Encoding.CHAR, 9);
        ByteBuffer buf = ByteBuffer.allocate(20);
        codec.encode(buf, 1234);
        String encoded = new String(buf.array(), 0, 9, StandardCharsets.ISO_8859_1);
        assertEquals("000001234", encoded);
    }

    @Test
    public void testEncodeBcd() {
        NumericCodec codec = new NumericCodec(Encoding.BCD);
        ByteBuffer buf = ByteBuffer.allocate(20);
        codec.encode(buf, 12345);
        byte[] encoded = buf.array();
        assertEquals(encoded[0], 0x01);
        assertEquals(encoded[1], 0x23);
        assertEquals(encoded[2], 0x45);
    }

    @Test
    public void testEncodeFixedBcd() {
        NumericCodec codec = new NumericCodec(Encoding.BCD, 9);
        ByteBuffer buf = ByteBuffer.allocate(20);
        codec.encode(buf, 12345);
        byte[] encoded = buf.array();
        assertEquals(encoded[0], 0x00);
        assertEquals(encoded[1], 0x00);
        assertEquals(encoded[2], 0x01);
        assertEquals(encoded[3], 0x23);
        assertEquals(encoded[4], 0x45);
    }

    @Test
    public void testEncodeFixedBinary() {
        NumericCodec codec = new NumericCodec(Encoding.BINARY, 5);
        ByteBuffer buf = ByteBuffer.allocate(20);
        codec.encode(buf, 12345);
        byte[] encoded = buf.array();
        assertEquals(encoded[0], 0x00);
        assertEquals(encoded[1], 0x00);
        assertEquals(encoded[2], 0x00);
        assertEquals(encoded[3], 0x30);
        assertEquals(encoded[4], 0x39);
    }

    @Test(expected = ConfigException.class)
    public void testEncodeExceedBinary() {
        new NumericCodec(Encoding.BINARY, 9);
    }

    @Test
    public void testEncodeMaxLongBinary() {
        NumericCodec codec = new NumericCodec(Encoding.BINARY, 8);
        ByteBuffer buf = ByteBuffer.allocate(20);
        codec.encode(buf, Long.MAX_VALUE);
        byte[] encoded = buf.array();
        assertEquals(encoded[0], (byte) 0x7F);
        assertEquals(encoded[1], (byte) 0xFF);
        assertEquals(encoded[2], (byte) 0xFF);
        assertEquals(encoded[3], (byte) 0xFF);
        assertEquals(encoded[4], (byte) 0xFF);
        assertEquals(encoded[5], (byte) 0xFF);
        assertEquals(encoded[6], (byte) 0xFF);
        assertEquals(encoded[7], (byte) 0xFF);
    }

    @Test(expected = CodecException.class)
    public void testEncodeExceedLongBinary() {
        NumericCodec codec = new NumericCodec(Encoding.BINARY, 8);
        ByteBuffer buf = ByteBuffer.allocate(20);
        BigInteger value = new BigInteger("8000000000000000", 16);
        assertEquals(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.valueOf(1L)), value);
        codec.encode(buf, value);
    }

}
