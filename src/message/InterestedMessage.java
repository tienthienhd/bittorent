package message;

import java.nio.ByteBuffer;

import message.PeerMessage.MessageType;

public class InterestedMessage extends PeerMessage {

	public InterestedMessage() {
		super(MessageType.INTERESTED, null);
	}

	public static InterestedMessage parse(byte[] payload) {
		return new InterestedMessage();
	}

	public static InterestedMessage craft() {
		return new InterestedMessage();
	}
}
