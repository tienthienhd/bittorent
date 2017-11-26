package message;

import java.nio.ByteBuffer;


public class HandshakeMessage extends PeerMessage {
	private byte length = 19;
	private byte[] protocol = "BitTorrent protocol".getBytes();
	private byte[] reserved = new byte[] {0,0,0,0, 0,0,0,0};
	private byte[] infoHash;
	private byte[] peerID;
	
	public HandshakeMessage(byte[] infoHash, byte[] peerID) {
		super(MessageType.HANDSHAKE, null);
		this.infoHash = infoHash;
		this.peerID = peerID;
	}

	@Override
	public byte[] generateMessageToSend() {
		ByteBuffer buffer = ByteBuffer.allocate(68);
		buffer.put(length);
		buffer.put(protocol);
		buffer.put(reserved);
		buffer.put(infoHash);
		buffer.put(peerID);
		return buffer.array();
	}
	
	public byte[] getInfoHash() {
		return this.infoHash;
	}
	
	public byte[] getPeerId() {
		return this.peerID;
	}
}
