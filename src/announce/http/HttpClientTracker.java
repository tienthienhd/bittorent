package announce.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import announce.ClientTracker;

public class HttpClientTracker extends ClientTracker {
	
	private HttpRequestMessage httpRequest;
	private HttpRespondMessage httpRespond;


	public HttpClientTracker(String announceUrl, byte[] infoHash, byte[] peerId, int port) {
		super(announceUrl, infoHash, peerId, port);

		this.httpRequest = new HttpRequestMessage(announceUrl, infoHash, peerId, port);
		
		if (this.connect()) {
			this.httpRespond = new HttpRespondMessage(response);
			this.updateInfoResponse();
			this.hasData = this.httpRespond.isHasData();
		}
	}
	

	public boolean connect() {
		try {
			URL url = this.httpRequest.buildURLRequest(this.downloaded, this.left, this.uploaded, ClientTracker.EVENT[2]);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(2000);

			InputStream in = conn.getInputStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			byte[] buffer = new byte[10000];
			int bytesRead = 0;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			this.response = out.toByteArray();
			return true;
		} catch (IOException ioe) {
			System.out.println(Thread.currentThread().getName() + ioe);
			Thread.yield();
		}
		return false;
	}

	public void updateInfoResponse() {
		this.interval = (int) this.httpRespond.getInterval();
		this.minInterval = (int) this.httpRespond.getMinInterval();
		this.complete = (int) this.httpRespond.getComplete();
		this.incomplete = (int) this.httpRespond.getIncomplete();
		this.peers = this.httpRespond.getPeers();
		this.httpRespond.printDetailed();
	}
	
	

	public void updateInfoRequest(long downloaded, long left, long uploaded, int event) {
		this.downloaded = downloaded;
		this.left = left;
		this.uploaded = uploaded;
		this.event = event;
	}
	
}
