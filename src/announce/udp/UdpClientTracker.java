package announce.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;

import announce.ClientTracker;
import utils.Utils;

public class UdpClientTracker extends ClientTracker {

	private int n = 0;
	private URI announceUrl;
	private byte[] buf = new byte[512];
	private DatagramPacket sent;
	private DatagramPacket recv;

	private UdpRequestMessage request;
	private UdpRespondMessage response;

	public UdpClientTracker(String announceUrl, byte[] infoHash, byte[] peerId, int port) {
		super(announceUrl, infoHash, peerId, port);
		try {
			this.announceUrl = new URI(announceUrl);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		this.request = new UdpRequestMessage(infoHash, peerId, (short) port);
		if(this.connect()) {
			this.response = new UdpRespondMessage();
			this.updateInfoResponse();
			this.hasData = true;
		}
	}

	@Override
	public boolean connect() {
		try {
			InetSocketAddress isa = new InetSocketAddress(announceUrl.getHost(), announceUrl.getPort());
			DatagramSocket socket = new DatagramSocket(isa);
			socket.setSoTimeout(120 * 1000);
			
			this.sent = new DatagramPacket(buf, 16);
			sent.setData(this.request.generateRequestConnect());
			socket.send(sent);
			
			this.recv = new DatagramPacket(buf, 16);
			socket.receive(recv);
			byte[] data = recv.getData();
			System.out.println(Utils.bytesToHex(data));
			
			
		} catch (IOException e) {
			this.buf = null;
		}
		return false;
	}

	@Override
	public void updateInfoRequest(long downloaded, long left, long uploaded, int event) {
		this.downloaded = downloaded;
		this.left = left;
		this.uploaded = uploaded;
		this.event = event;
	}

	@Override
	public void updateInfoResponse() {
		// TODO Auto-generated method stub

	}

}
