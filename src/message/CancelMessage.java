package message;

import java.nio.ByteBuffer;

import message.PeerMessage.MessageType;
import utils.Utils;

public class CancelMessage extends PeerMessage {
	
		private static final int PAYLOAD_LENGTH = 12;
			
		private int pieceIndex;
		private int offset;
		private int length;

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

		public static CancelMessage parse(byte[] payload) {
			int pieceIndex = Utils.byteArrayToInt(payload);
			int offset = Utils.byteArrayToInt(Utils.subArray(payload, 4, 4));
			int length = Utils.byteArrayToInt(Utils.subArray(payload, 8, 4));
			return new CancelMessage(payload, pieceIndex, offset, length);
		}

		public static CancelMessage craft(int pieceIndex, int offset, int length) {
			byte[] payload = Utils.concatArray(Utils.intToByteArray(pieceIndex),
					Utils.concatArray(Utils.intToByteArray(offset), 
										Utils.intToByteArray(length)));
			return new CancelMessage(payload, pieceIndex, offset, length);
		}
}
