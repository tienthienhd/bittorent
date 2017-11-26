package announce.http;

import java.util.ArrayList;
import java.util.Map;

import bencode.BDecoder;
import peers.Peer;
import torrentfile.TorrentFile;
import utils.Utils;

/**
 * Thông tin các dữ liệu gửi từ tracker
 * 
 * @author tienthien
 *
 */
public class HttpRespondMessage {

	private byte[] data;

	private long interval;
	private long minInterval;
	private long complete;
	private long incomplete;
	private ArrayList<Peer> peers;

	private boolean hasData = false;

	public HttpRespondMessage(byte[] data) {
		this.data = data;
		if (parse()) {
			hasData = true;
		}
	}

	public boolean isHasData() {
		return this.hasData;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public long getMinInterval() {
		return minInterval;
	}

	public void setMinInterval(long minInterval) {
		this.minInterval = minInterval;
	}

	public long getComplete() {
		return complete;
	}

	public void setComplete(long complete) {
		this.complete = complete;
	}

	public long getIncomplete() {
		return incomplete;
	}

	public void setIncomplete(long incomplete) {
		this.incomplete = incomplete;
	}

	public ArrayList<Peer> getPeers() {
		return peers;
	}

	public void setPeers(ArrayList<Peer> peers) {
		this.peers = peers;
	}

	public void setHasData(boolean hasData) {
		this.hasData = hasData;
	}


	public boolean parse() {
		Object o = BDecoder.decode(data);
		if (o instanceof Map) {
			if (((Map) o).containsKey("interval")) {
				interval = (Long) ((Map) o).get("interval");
			} else {
				System.out.println("Must have key interval");
				return false;
			}

			if (((Map) o).containsKey("peers")) {
				byte[] tmp = (byte[]) ((Map) o).get("peers");
				if (tmp.length % 6 != 0) {
					System.out.println("Number bytes of peers don't divisible 6");
					return false;
				}

				peers = new ArrayList<>();

				for (int i = 0; i < tmp.length; i += 6) {
					byte[] ip = Utils.subArray(tmp, i, 4);

					int port = 0;
					port |= tmp[i + 4] & 0xFF;
					port <<= 8;
					port |= tmp[i + 5] & 0xFF;

					peers.add(new Peer(ip, port));
				}
			} else {
				// System.out.println("parse tracker response failed");
				return false;
			}

			if (((Map) o).containsKey("min interval")) {
				minInterval = (Long) ((Map) o).get("min interval");
			}
			if (((Map) o).containsKey("complete")) {
				complete = (Long) ((Map) o).get("complete");
			}
			if (((Map) o).containsKey("incomplete")) {
				incomplete = (Long) ((Map) o).get("incomplete");
			}

			// System.out.println("--------------parse ok---------------");
			return true;
		} else if (o.equals("failure message")) {
			System.out.println("No other keys are included.");
		} else if (o.equals("warning message")) {
			System.out.println("warning message");
		}
		return false;
	}

	public void printDetailed() {
		if (hasData) {
			System.out.println("interval : " + interval);
			System.out.println("min interval : " + minInterval);
			System.out.println("complete : " + complete);
			System.out.println("incomplete : " + incomplete);

			System.out.println("peers: ");

			for (int i = 0; i < peers.size(); i++) {
				System.out.println((i + 1) + " = ip: " + peers.get(i).getIp() + " - port: " + peers.get(i).getPort());
			}
		} else {
			System.out.println("Don't have data");
		}
	}
}
