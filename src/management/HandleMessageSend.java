package management;

import message.PeerMessage;

public interface HandleMessageSend {
	public void notifySend();
	public void addMessage(PeerMessage msg);
}
