package management;

import message.PeerMessage;

public interface HandleMessageReceive {
	
	public void MessageReceived(PeerMessage msg);
	public void dropConnection();
	
	public void handleHandshakeMessage(byte[] infoHash, byte[] peerId);
	public void handleChokeMessage();
	public void handleUnchokeMessage();
	public void handleInterestedMessage();
	public void handleNotInterestedMessage();
	public void handleHaveMessage(int piece);
	public void handleBitfieldMessage(byte[] bitfield);
	public void handleRequestMessage(int index, int begin, int length);
	public void handlePieceMessage(int index, int begin, byte[] block);
	public void handleCancelMessage(int index, int begin, int length);
}
