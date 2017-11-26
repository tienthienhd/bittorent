package message;

import java.nio.ByteBuffer;

import message.PeerMessage.MessageType;
import utils.Utils;

public class RequestMessage extends PeerMessage {
	/**
	 * kích thước khối mặc định: 2^14 byte = 16 kB
	 */
	public static final int DEFAULT_REQUEST_SIZE = 16384;

	// kích thước phần dữ liệu thêm payload
	private static final int PAYLOAD_LENGTH = 12;

	// /**
	// * kích thước tối đa của một khối dữ liệu: 2^17 byte = 131kB.
	// */
	// public static final int MAX_REQUEST_SIZE = 131072;

	private int pieceIndex;
	private int offset;
	private int length;

	// khởi tạo
	public RequestMessage(byte[] payload, int pieceIndex, int offset, int length) {
		super(MessageType.REQUEST, payload);
		this.pieceIndex = pieceIndex;
		this.offset = offset;
		this.length = length;
	}

	public int getOffset() {
		return this.offset;
	}

	public int getLength() {
		return this.length;
	}

	public int getPieceIndex() {
		return this.pieceIndex;
	}

	// phân tích payload và trả về PeerMessage tương ứng với payload
	public static RequestMessage parse(byte[] payload) {
		int pieceIndex = Utils.byteArrayToInt(payload);
		int offset = Utils.byteArrayToInt(Utils.subArray(payload, 4, 4));
		int length = Utils.byteArrayToInt(Utils.subArray(payload, 8, 4));
		return new RequestMessage(payload, pieceIndex, offset, length);
	}
	
	// tạo 1 PeerMessage từ các dữ liệu cung cấp
	public static RequestMessage craft(int pieceIndex, int offset, int length) {
		byte[] payload = Utils.concatArray(Utils.intToByteArray(pieceIndex),
						Utils.concatArray(Utils.intToByteArray(offset), 
											Utils.intToByteArray(length)));
		return new RequestMessage(payload, pieceIndex, offset, length);
	}
	
	// tạo 1 PeerMessage từ các dữ liệu cung cấp
		public static PeerMessage craft(int pieceIndex, int offset) {
			byte[] payload = Utils.concatArray(Utils.intToByteArray(pieceIndex),
					Utils.concatArray(Utils.intToByteArray(offset), 
										Utils.intToByteArray(DEFAULT_REQUEST_SIZE)));
			
			return new RequestMessage(payload, pieceIndex, offset, DEFAULT_REQUEST_SIZE);
		}
}
