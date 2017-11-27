package message;

import java.nio.ByteBuffer;

public class UnchokeMessage extends PeerMessage {

		private static final int PAYLOAD_LENGTH = 0;

	public UnchokeMessage() {
		super(MessageType.UNCHOKE, null);
	}

	public static UnchokeMessage parse(byte[] payload) {
		return new UnchokeMessage();
	}

	public static UnchokeMessage craft() {
		return new UnchokeMessage();
	}
}
