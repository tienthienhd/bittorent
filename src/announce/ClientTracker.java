package announce;

import java.util.ArrayList;

import peers.Peer;

public abstract class ClientTracker {
	
	public static final String[] EVENT = {"none", "completed", "started", "stopped"};
	IAnnounce announce;
	protected boolean hasData = false;
	
	protected String announceUrl;
	protected byte[] infoHash;
	protected byte[] peerId;
	protected int port;
	protected long downloaded;
	protected long uploaded;
	protected long left;
	protected int event;

	protected byte[] response = new byte[0];
	protected int interval = 60;
	protected int minInterval;
	protected int complete;
	protected int incomplete;
	protected ArrayList<Peer> peers;
	
	public ClientTracker(String announceUrl, byte[] infoHash, byte[] peerId, int port) {
		this.announceUrl = announceUrl;
		this.infoHash = infoHash;
		this.peerId = peerId;
		this.port = port;
	}
	



	public int getEvent() {
		return event;
	}



	public int getInterval() {
		return interval;
	}



	public int getMinInterval() {
		return minInterval;
	}



	public int getComplete() {
		return complete;
	}



	public int getIncomplete() {
		return incomplete;
	}



	public ArrayList<Peer> getPeers() {
		return peers;
	}

	public void addManager(Announce announce) {
		this.announce = announce;
		this.mergeData();
	}
	
	public void mergeData() {
		this.announce.updateInfo(this);
	}

	public boolean hasData() {
		return this.hasData;
	}
	
	public abstract boolean connect();
	
	public abstract void updateInfoRequest(long downloaded, long left, long uploaded, int event);
	
	public abstract void updateInfoResponse();
}
