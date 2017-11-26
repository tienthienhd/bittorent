package message;

import java.nio.ByteBuffer;

public class ChokeMessage extends PeerMessage {

	// kích thước phần dữ liệu thêm payload
	private static final int PAYLOAD_LENGTH = 0;

	// khởi tạo
	public ChokeMessage() {
		super(MessageType.CHOKE, null);
	}

	// phân tích payload và trả về PeerMessage tương ứng với payload
	public static ChokeMessage parse(byte[] payload) {
		return new ChokeMessage();
	}

	// tạo 1 PeerMessage từ các dữ liệu cung cấp
	public static ChokeMessage craft() {
		return new ChokeMessage();
	}
}
