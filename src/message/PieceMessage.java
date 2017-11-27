package message;

import java.nio.ByteBuffer;

import message.PeerMessage.MessageType;
import utils.Utils;

public class PieceMessage extends PeerMessage {
	
		private int pieceIndex;
		private int offset;
		private byte[] block;

		public PieceMessage(byte[] payload, int pieceIndex, int offset, byte[] block) {
			super(MessageType.PIECE, payload);
			this.pieceIndex = pieceIndex;
			this.offset = offset;
			this.block = block;
		}

		public int getPieceIndex() {
			return this.pieceIndex;
		}
		
		public int getOffset() {
			return this.offset;
		}
		
		public byte[] getBlock() {
			return this.block;
		}

		public static PieceMessage parse(byte[] payload) {
			int pieceIndex = Utils.byteArrayToInt(payload);
			int offset = Utils.byteArrayToInt(Utils.subArray(payload, 4, 4));
			byte[] block = Utils.subArray(payload, 8, payload.length - 8);
			return new PieceMessage(payload, pieceIndex, offset, block);
		}

		public static PieceMessage craft(int pieceIndex, int offset, byte[] block) {
			byte[] payload = Utils.concatArray(Utils.intToByteArray(pieceIndex), 
					Utils.concatArray(Utils.intToByteArray(offset), block));
			return new PieceMessage(payload, pieceIndex, offset, block);
		}
}
