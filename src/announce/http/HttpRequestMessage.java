package announce.http;

import java.net.MalformedURLException;
import java.net.URL;

import utils.Utils;

public class HttpRequestMessage {
	private String announceUrl;
	
	private byte[] infoHash;
	
	private byte[] peerId;
	
	private String ip;
	
	private int port;
	


	public HttpRequestMessage(String announceUrl, byte[] infoHash, byte[] peerId, int port) {
		this(announceUrl, infoHash, peerId, null, port);
	}
	
	public HttpRequestMessage(String announceUrl, byte[] infoHash, byte[] peerId, String ip, int port) {
		this.announceUrl = announceUrl;
		this.infoHash = infoHash;
		this.peerId = peerId;
		this.ip = ip;
		this.port = port;
	}
	
	public URL buildURLRequest(long downloaded, long left, long uploaded, String event)
			throws MalformedURLException {
		StringBuilder url = new StringBuilder();
		url.append(announceUrl);
		url.append("?info_hash=" + Utils.byteArrayToURLString(infoHash));
		url.append("&peer_id=" + Utils.byteArrayToURLString(peerId));
		if(ip != null) {
			url.append("&ip=" + ip);
		}
		url.append("&port=" + port);
		url.append("&uploaded=" + uploaded);
		url.append("&downloaded=" + downloaded);
		url.append("&left=" + left);
		url.append("&numwant=50&compact=1");
		url.append("&event=" + event);
		
		return new URL (url.toString());
	}
	

}
