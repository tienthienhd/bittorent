package management;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import message.BitfieldMessage;
import message.CancelMessage;
import message.ChokeMessage;
import message.HandshakeMessage;
import message.HaveMessage;
import message.InterestedMessage;
import message.KeepAliveMessage;
import message.NotInterestedMessage;
import message.PeerMessage;
import message.PieceMessage;
import message.RequestMessage;
import message.UnchokeMessage;
import utils.Utils;

public class MessageReceiver extends Thread {

	private boolean running = false;
	private boolean handshakeOk = false;

	private InputStream is;
	private DataInputStream dis;
	private HandleMessageReceive dlTaskListener;

	public MessageReceiver(InputStream is) {
		this.is = is;
		this.dis = new DataInputStream(is);
	}

	public void setDLTask(DownloadTask dt) {
		this.dlTaskListener = dt;
	}

	public synchronized void setHandshake(boolean ok) {
		this.handshakeOk = ok;
	}

	public void run1() {
		this.running = true;
		byte[] buffer = new byte[(int) Math.pow(2, 20)];

		int bytesRead = 0;
		HandshakeMessage hs = null;
		PeerMessage msg = null;

		try {
			while (running) {
				if (!handshakeOk) {
					bytesRead = this.read(buffer, 68);
					if(!this.isHandshakeMessage(buffer)) {
						System.out.println(Thread.currentThread().getName()
								+ "This message isn't handshake message when handshakeOk = false");
						this.dlTaskListener.dropConnection();
						return;
					}
					
					hs = new HandshakeMessage(Utils.subArray(buffer, 28, 20), Utils.subArray(buffer, 48, 20));
				} else {
					int len = this.dis.readInt();
					if (len == 0) {
						msg = KeepAliveMessage.parse();
					} else {
						bytesRead = this.read(buffer, len);
						msg = getPeerMessage(buffer[0], Utils.subArray(buffer, 0, len - 1));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!this.handshakeOk && hs != null) {
			this.fireMessageReceived(hs);
		} else {
			if (msg != null)
				this.fireMessageReceived(msg);
			// else
			// try {
			// Thread.sleep(100);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
		}

	}
	
	private boolean isHandshakeMessage(byte[] data) {
		if(data[0] != 19) {
			return false;
		} else if(!(new String(Utils.subArray(data, 1, 19)).equals("BitTorrent protocol"))){
			return false;
		}
		return true;
	}

	private int read(byte[] buffer, int maxLength) {
		int bytesRead = 0;
		int offset = 0;
		try {
			while ((bytesRead = dis.read(buffer, offset, maxLength - offset)) > 0 && bytesRead < maxLength) {
				offset += bytesRead;
			}
			return bytesRead;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public void run() {
		this.running = true;
		int read = 0;
		byte[] lengthHS = new byte[1];
		byte[] protocol = new byte[19];
		byte[] reserved = new byte[8];
		byte[] infoHash = new byte[20];
		byte[] peerID = new byte[20];

		byte[] lengthMessage = new byte[4];

		HandshakeMessage hs = null;
		PeerMessage msg = null;

		while (running) {
			try {
				if (!handshakeOk) {
					this.readFromInputStream(lengthHS);
					this.readFromInputStream(protocol);
					this.readFromInputStream(reserved);
					this.readFromInputStream(infoHash);
					this.readFromInputStream(peerID);
					hs = new HandshakeMessage(infoHash, peerID);
				} else {
					if ((read = this.readFromInputStream(lengthMessage)) > 0) {
						int len = Utils.byteArrayToInt(lengthMessage);
						if (len == 0) {
							msg = KeepAliveMessage.craft();
						} else {
							//System.out.println("len of message: " + len);
							int id = dis.read();
							if (id == -1) {
				 				//System.out.println("Message id failed!");
								//this.dis.readf
								msg = null;
							} else {
								len = len - 1; // trừ 1 byte của id trong thông điệp
								byte[] payload = new byte[len];
								// System.out.println("Nhan dc payload: " + new String(payload));
								if (this.readFromInputStream(payload) > 0) {
									msg = getPeerMessage((byte) id, payload);
								}
								payload = null;
							}
						}
					} else {
						msg = null;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				//System.out.println(e);
				this.fireMessageReceived(null);
				return;
			}

			if (!this.handshakeOk && hs != null) {
				this.fireMessageReceived(hs);
				this.handshakeOk = true;
			} else {
				if (msg != null)
					this.fireMessageReceived(msg);
				else
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			}
		}
	}

	public PeerMessage getPeerMessage(byte id, byte[] payload) {
		if (id == 0)
			return ChokeMessage.parse(payload);
		else if (id == 1)
			return UnchokeMessage.parse(payload);
		else if (id == 2)
			return InterestedMessage.parse(payload);
		else if (id == 3)
			return NotInterestedMessage.parse(payload);
		else if (id == 4)
			return HaveMessage.parse(payload);
		else if (id == 5)
			return BitfieldMessage.parse(payload);
		else if (id == 6)
			return RequestMessage.parse(payload);
		else if (id == 7)
			return PieceMessage.parse(payload);
		else if (id == 8)
			return CancelMessage.parse(payload);
		else {
			return null;
		}
	}

	private int readFromInputStream(byte[] data) {

		try {
			this.dis.readFully(data);
		} catch(EOFException e) {
			Thread.yield();
		} catch(SocketException e) {
			System.out.println(e);
		} catch (IOException e) {
			e.printStackTrace();
			//System.out.println(e);
			//e.printStackTrace();
			return -1;
		}
		return data.length;
		
	}

	private void fireMessageReceived(PeerMessage msg) {
//		System.out
//				.println("\t\t\t\t\tReceived Message From " + Thread.currentThread().getName() + ": " + msg.getType());
		this.dlTaskListener.MessageReceived(msg);
	}

	public void stopRecv() {
		this.running = false;
		try {
			this.dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Thread.yield();
	}
}
