package announce.http;

import java.net.MalformedURLException;
import java.net.URL;

import utils.Utils;

public class HttpRequestMessage {
	/**
	 * Danh sÃ¡ch cÃ¡c Ä‘á»‹a chá»‰ cá»§a tracker
	 */
	private String announceUrl;
	
	/**
	 * DÃ¹ng Ä‘á»ƒ Ä‘á»‹nh danh file
	 */
	private byte[] infoHash;
	
	/**
	 * DÃ¹ng Ä‘á»ƒ Ä‘á»‹nh danh mÃ¡y tráº¡m
	 */
	private byte[] peerId;
	
	/**
	 * Ä‘á»‹a chá»‰ mÃ¡y tráº¡m (cÃ³ thá»ƒ bá»� qua)
	 */
	private String ip;
	
	/**
	 * Sá»‘ hiá»‡u cá»•ng giao tiáº¿p cá»§a mÃ¡y tráº¡m
	 */
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
