package message;

import java.nio.ByteBuffer;

import utils.Utils;

/**
 * 
 * @author tienthien
 *
 */
public abstract class PeerMessage {

	public static final int MESSAGE_LENGTH_FIELD_SIZE = 4;

	private MessageType type;
	protected byte[] payload;

	public PeerMessage(MessageType type, byte[] payload) {
		this.type = type;
		this.payload = payload;
	}

	// --------------Getter and Setter------------
	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public MessageType getType() {
		return this.type;
	}

	// ------------------------------------------

	/**
	 * các loại thông điệp và id tương ứng
	 * 
	 * @author tienthien
	 *
	 */
	public enum MessageType {
		HANDSHAKE(-2), KEEP_ALIVE(-1), CHOKE(0), UNCHOKE(1), INTERESTED(2), NOT_INTERESTED(3), HAVE(4), BITFIELD(
				5), REQUEST(6), PIECE(7), CANCEL(8), PORT(9);

		private byte id;

		MessageType(int id) {
			this.id = (byte) id;
		}

		public byte getId() {
			return this.id;
		}

		public boolean equals(byte id) {
			return this.id == id;
		}

		public static MessageType getType(byte id) {
			for (MessageType m : MessageType.values()) {
				if (m.equals(id)) {
					return m;
				}
			}
			return null;
		}
	}

	public byte[] generateMessageToSend() {
		if (payload != null) {
			byte[] msg = Utils.concatArray(Utils.intToByteArray(1 + payload.length), new byte[] {type.getId()});
//			System.out.println(new String(msg));
			msg = Utils.concatArray(msg, payload);
			return msg;
		} else {
			if(type == MessageType.KEEP_ALIVE) {
				return Utils.intToByteArray(0);
			} else {
				return Utils.concatArray(Utils.intToByteArray(1), new byte[] {type.getId()});
			}
		}			
	}


	public static PeerMessage parse(byte[] buffer) {
		int length = Utils.byteArrayToInt(buffer);
		if (length == 0) {
			return new KeepAliveMessage();
		} else if (length != buffer.length - 4) {
			System.out.println("length of payload invalive");
		} else {
			byte id = buffer[4];
			byte[] payload = Utils.subArray(buffer, 5, buffer.length - 5);
			switch (MessageType.getType(id)) {
			case CHOKE:
				return ChokeMessage.parse(payload);
			case UNCHOKE:
				return UnchokeMessage.parse(payload);
			case INTERESTED:
				return InterestedMessage.parse(payload);
			case NOT_INTERESTED:
				return NotInterestedMessage.parse(payload);
			case HAVE:
				return HaveMessage.parse(payload);
			case BITFIELD:
				return BitfieldMessage.parse(payload);
			case REQUEST:
				return RequestMessage.parse(payload);
			case PIECE:
				return PieceMessage.parse(payload);
			case CANCEL:
				return CancelMessage.parse(payload);
			}
		}
		return null;
	}
	

	public String toString() {
		return new String(type.toString() + ":" + Utils.bytesToHex(this.generateMessageToSend()));
	}
}
