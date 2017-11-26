package message;

import java.nio.ByteBuffer;

import message.PeerMessage.MessageType;
import utils.Utils;

public class CancelMessage extends PeerMessage {
	
	// kích thước phần dữ liệu thêm payload
		private static final int PAYLOAD_LENGTH = 12;
			
		private int pieceIndex;
		private int offset;
		private int length;

		// khởi tạo
		public CancelMessage(byte[] payload, int pieceIndex, int offset, int length) {
			super(MessageType.CANCEL, payload);
			this.pieceIndex = pieceIndex;
			this.offset = offset;
			this.length = length;
		}

		public int getPieceIndex() {
			return this.pieceIndex;
		}
		
		public int getOffset() {
			return this.offset;
		}
		
		public int getLength() {
			return this.length;
		}

		// phân tích payload và trả về PeerMessage tương ứng với payload
		public static CancelMessage parse(byte[] payload) {
			int pieceIndex = Utils.byteArrayToInt(payload);
			int offset = Utils.byteArrayToInt(Utils.subArray(payload, 4, 4));
			int length = Utils.byteArrayToInt(Utils.subArray(payload, 8, 4));
			return new CancelMessage(payload, pieceIndex, offset, length);
		}

		// tạo 1 PeerMessage từ các dữ liệu cung cấp
		public static CancelMessage craft(int pieceIndex, int offset, int length) {
			byte[] payload = Utils.concatArray(Utils.intToByteArray(pieceIndex),
					Utils.concatArray(Utils.intToByteArray(offset), 
										Utils.intToByteArray(length)));
			return new CancelMessage(payload, pieceIndex, offset, length);
		}
}
