package announce.udp;

import utils.Utils;

/**
 * @see <a href="http://www.bittorrent.org/beps/bep_0015.html">http://www.bittorrent.org/beps/bep_0015.html</a>
 * 		<a href="https://github.com/naim94a/udpt/wiki/The-BitTorrent-UDP-tracker-protocol" > https://github.com/naim94a/udpt/wiki/The-BitTorrent-UDP-tracker-protocol </a>
 * @author tienthien
 *
 */
public class UdpRequestMessage {
	
	// gói tin thứ 1
	private final long protocolId;//TODO: 0x41727101980 // magic constant
	private int action; // 0 gửi yêu cầu khi chưa nhận đc thông báo từ tracker
						// 1 gủi yêu cầu khi đã nhận được thông báo từ tracker
	private int transactionId; // sinh ra ngâu nhiên
	
	// gói tin thứ 2
	//private long connectionId;
	//action: 1 // announce
	private byte[] infoHash;
	//transactionId;
	private byte[] peerId;
	//private long downloaded;
	//private long left;
	//private long uploaded;
	//private int event; //0 // 0: none; 1: completed; 2: started; 3: stopped
	private int ipAddress; //0//default
	private int key;
	private int numWant; // -1 // default
	private short port;
	
	public UdpRequestMessage(byte[] infoHash, byte[] peerId, short port) {
		this.protocolId = 4497486125440l;
		this.transactionId = (int)System.currentTimeMillis();
		this.ipAddress = 0;
		this.key = 0;
		this.numWant = 20;
		this.port = port;
	}
	
	public byte[] generateRequestAnnounce(byte[] connectionId, long downloaded, long left, long uploaded,  int event) {
		this.action = 1;
		// connection id
		byte[] action = Utils.intToByteArray(this.action);
		byte[] transactionId = Utils.intToByteArray(this.transactionId);
		//infohash
		//peerid
		byte[] download = Utils.longToByteArray(downloaded);
		byte[] left1 = Utils.longToByteArray(left);
		byte[] upload = Utils.longToByteArray(uploaded);
		byte[] event1 = Utils.longToByteArray(event);
		byte[] ipAddress = Utils.intToByteArray(this.ipAddress);
		byte[] key = Utils.intToByteArray(this.key);
		byte[] numWant = Utils.intToByteArray(this.numWant);
		byte[] port = Utils.shortToByteArray(this.port);
		
		byte[] message = Utils.concatArray(connectionId,
				action, transactionId, this.infoHash, this.peerId, download,
				left1, upload, event1, ipAddress, key, numWant, port);
		return message;
	}
	
	public byte[] generateRequestConnect() {
		this.action = 0;
		byte[] protocolId = Utils.longToByteArray(this.protocolId);
		byte[] action = Utils.intToByteArray(this.action);
		byte[] transactionId = Utils.intToByteArray(this.transactionId);
		
		byte[] message = Utils.concatArray(protocolId, action, transactionId);
		return message;
	}
}
