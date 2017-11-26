package announce;

import java.util.ArrayList;

import announce.http.HttpClientTracker;
import management.DownloadManager;
import peers.Peer;
import torrentfile.TorrentFile;

public class Announce extends Thread implements IAnnounce {

	private DownloadManager dm = null;
	
	private TorrentFile torrent;

	private ArrayList<String> announces;
	private byte[] infoHash;
	private byte[] peerId;
	private int port = 6881;
	private long uploaded;
	private long downloaded;
	private long left;
	private int event;

	private int interval = 1;
	private int minInterval;
	private int complete;
	private int incomplete;
	private ArrayList<Peer> peers;

	private boolean running;
	private boolean hasData = false;
	private boolean init = true;
	private ClientTracker ct;

	public Announce(TorrentFile torrent, byte[] peerId, DownloadManager dm) {
		this.torrent = torrent;
		this.announces = torrent.getAnnounces();
		this.infoHash = torrent.getInfoHash();
		this.peerId = peerId;
		this.dm = dm;
	}

	public void run() {
		running = true;
		int countConnectFail = 0;
		boolean connectOk = false;
		while (running) {
			for (int i = 0; i < announces.size(); i++) {
				if (announces.get(i).startsWith("http")) {
					System.out.println("connnect " + announces.get(i));
					ct = new HttpClientTracker(announces.get(i), infoHash, peerId, port);
					if (ct.hasData()) {
						this.hasData = true;
						ct.addManager(this);
						i--;
						connectOk = true;
						try {
							synchronized (this) {
								if(!init) {
									dm.updatePeerList();
								} else {
									init = false;
								}
								wait(this.interval * 1000);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
					}
				} else if (announces.get(i).startsWith("udp")) {
					// ct = new UdpClientTracker(announces.get(i), infoHash, peerId, port);
					// if(ct.hasData()) {
					// ct.addManager(this);
					// try {
					// Thread.sleep(this.interval * 1000);
					// } catch (InterruptedException e) {
					// e.printStackTrace();
					// }
					// }
				}
				ct = null;
				System.gc();
			}
			// try {
			// synchronized (this) {
			// wait(this.interval * 1000);
			// }
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			if (!connectOk) {
				countConnectFail++;
				if (countConnectFail >= 5) {
					System.out.println("Trackers don't respond!");
					System.exit(1);
				}
			}
		}
	}

	

	@Override
	public void updateInfo(ClientTracker ct) {
		this.interval = ct.getInterval();
		this.minInterval = ct.getMinInterval();
		this.complete = ct.getComplete();
		this.incomplete = ct.getIncomplete();
		this.peers = ct.getPeers();

	}

	public boolean hasData() {
		return this.hasData;
	}

	public ArrayList<Peer> getPeers() {
		return peers;
	}

	public void setPeers(ArrayList<Peer> peers) {
		this.peers = peers;
	}

	public void requestAgain() {
		synchronized (this) {
			this.notify();
		}
	}
	
	public int getMinInterval() {
		return this.minInterval;
	}
}
