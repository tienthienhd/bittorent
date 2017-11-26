package message;

import java.nio.ByteBuffer;

public class UnchokeMessage extends PeerMessage {

	// kích thước phần dữ liệu thêm payload
		private static final int PAYLOAD_LENGTH = 0;

	// khởi tạo
	public UnchokeMessage() {
		super(MessageType.UNCHOKE, null);
	}

	// phân tích payload và trả về PeerMessage tương ứng với payload
	public static UnchokeMessage parse(byte[] payload) {
		return new UnchokeMessage();
	}

	// tạo 1 PeerMessage từ các dữ liệu cung cấp
	public static UnchokeMessage craft() {
		return new UnchokeMessage();
	}
}
