package message;

public class KeepAliveMessage extends PeerMessage {

	public KeepAliveMessage() {
		super(MessageType.KEEP_ALIVE, null);
	}

	public static KeepAliveMessage parse() {
		return new KeepAliveMessage();
	}

	public static KeepAliveMessage craft() {
		return new KeepAliveMessage();
	}

}
