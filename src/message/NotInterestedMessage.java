package message;

import java.nio.ByteBuffer;

import message.PeerMessage.MessageType;

public class NotInterestedMessage extends PeerMessage {

	// khởi tạo
	public NotInterestedMessage() {
		super(MessageType.NOT_INTERESTED, null);
	}

	// phân tích payload và trả về PeerMessage tương ứng với payload
	public static NotInterestedMessage parse(byte[] payload) {
		return new NotInterestedMessage();
	}

	// tạo 1 PeerMessage từ các dữ liệu cung cấp
	public static NotInterestedMessage craft() {
		return new NotInterestedMessage();
	}
}
