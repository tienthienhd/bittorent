package message;

import java.nio.ByteBuffer;

import message.PeerMessage.MessageType;

public class NotInterestedMessage extends PeerMessage {

	public NotInterestedMessage() {
		super(MessageType.NOT_INTERESTED, null);
	}

	public static NotInterestedMessage parse(byte[] payload) {
		return new NotInterestedMessage();
	}

	public static NotInterestedMessage craft() {
		return new NotInterestedMessage();
	}
}
