package message;

import java.nio.ByteBuffer;

import message.PeerMessage.MessageType;
import utils.Utils;

public class HaveMessage extends PeerMessage {

	private static final int PAYLOAD_LENGTH = 4;
		
	private int pieceIndex;

	public HaveMessage(byte[] payload, int pieceIndex) {
		super(MessageType.HAVE, payload);
		this.pieceIndex = pieceIndex;
	}

	public int getPieceIndex() {
		return this.pieceIndex;
	}

	public static HaveMessage parse(byte[] payload) {
		int pieceIndex = Utils.byteArrayToInt(payload);
		return new HaveMessage(payload, pieceIndex);
	}

	public static HaveMessage craft(int pieceIndex) {
		byte[] payload = Utils.intToByteArray(pieceIndex);
//		System.out.println(new String(payload));
		return new HaveMessage(payload, pieceIndex);
	}
	
	public static void main(String[] args) {
		PeerMessage p = craft(12931);
		System.out.println(Utils.bytesToHex(p.generateMessageToSend()));
	}

}
