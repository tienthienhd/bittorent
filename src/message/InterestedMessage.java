package message;

import java.nio.ByteBuffer;

import message.PeerMessage.MessageType;

public class InterestedMessage extends PeerMessage {

	// khởi tạo
	public InterestedMessage() {
		super(MessageType.INTERESTED, null);
	}

	// phân tích payload và trả về PeerMessage tương ứng với payload
	public static InterestedMessage parse(byte[] payload) {
		return new InterestedMessage();
	}

	// tạo 1 PeerMessage từ các dữ liệu cung cấp
	public static InterestedMessage craft() {
		return new InterestedMessage();
	}
}
