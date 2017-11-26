package message;

import java.nio.ByteBuffer;
import java.util.BitSet;

//TODO: bitset
public class BitfieldMessage extends PeerMessage {

	private BitSet bitfield;

	public BitfieldMessage(byte[] payload, BitSet bitfield) {
		super(MessageType.BITFIELD, payload);
		this.bitfield = bitfield;
	}
	
	public byte[] getBitfield() {
		return this.bitfield.toByteArray();
	}

	public static BitfieldMessage parse(byte[] payload) {
		BitSet bitfield = new BitSet(payload.length * 8);
		bitfield.valueOf(payload);
		return new BitfieldMessage(payload, bitfield);
	}

	public static BitfieldMessage craft(BitSet bitfield) {
		byte[] payload = bitfield.toByteArray();
		return new BitfieldMessage(payload, bitfield);
	}
	
	public String toString() {
		return "Bitfield:" + new String(bytesToHex(payload));
	}
	
	
	public static void main(String[] args) {
		BitSet bs = new BitSet();
		for(int i= 0; i < 80; i++)
		bs.set(i);
		System.out.println(bs);
		System.out.println(bs.length());
		byte[] t = bs.toByteArray();
		
		BitfieldMessage m = (BitfieldMessage) BitfieldMessage.craft(bs);
		System.out.println(m);
	}
	
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
}
