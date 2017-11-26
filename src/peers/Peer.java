package peers;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Peer {
	private InetAddress ip;
	private int port;
	
	public Peer(byte[] ip, int port) {
		try {
			this.ip = InetAddress.getByAddress(ip);
		} catch (UnknownHostException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		this.port = port;
	}
	
	public InetAddress getIp() {
		return this.ip;
	}
	
	public void setIp(InetAddress ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public String toString() {
		return this.ip.getHostAddress() + ":" + this.port;
	}
}
