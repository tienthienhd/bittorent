package message;

public class ChokeMessage extends PeerMessage {

	private static final int PAYLOAD_LENGTH = 0;

	public ChokeMessage() {
		super(MessageType.CHOKE, null);
	}

	public static ChokeMessage parse(byte[] payload) {
		return new ChokeMessage();
	}

	public static ChokeMessage craft() {
		return new ChokeMessage();
	}
}
