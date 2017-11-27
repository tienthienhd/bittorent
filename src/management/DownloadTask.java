package management;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.BitSet;

import javax.swing.Timer;

import message.BitfieldMessage;
import message.CancelMessage;
import message.HandshakeMessage;
import message.HaveMessage;
import message.InterestedMessage;
import message.PeerMessage;
import message.PeerMessage.MessageType;
import message.PieceMessage;
import message.RequestMessage;
import message.UnchokeMessage;
import peers.Peer;
import utils.Utils;

public class DownloadTask extends Thread implements HandleMessageReceive {

	private IDownloadManager dlManager;
	private boolean isHandshaking;
	private boolean handshakeOk;
	private boolean isInteresting;
	private Timer timeToReconnect = null;
	private int countReconnect = 0;

	private byte[] infoHash;
	private byte[] clientId;

	private Socket socket;
	private Peer peer;
	private Piece piece;
	private BitSet requested;

	private MessageSender sender;
	private MessageReceiver receiver;

	/**
	 * list of pieces that the peer has
	 */
	private BitSet bitfield;

	public DownloadTask(Peer peer, byte[] infoHash, byte[] clientId) {
		this.peer = peer;
		this.infoHash = infoHash;
		this.clientId = clientId;

		// reconnect
		timeToReconnect = new Timer(1 * 60 * 1000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("reconnect to " + peer.getIp().getHostAddress() + ":" + peer.getPort());
				if (reconnect()) {
					if (countReconnect >= 1) {
						interrupt();
						timeToReconnect.stop();
//						System.out.println("interrupt");
						return;
					}
					countReconnect++;
				}

			}
		});
		timeToReconnect.start();
	}

	public boolean init() {
		try {
			this.socket = new Socket(peer.getIp(), peer.getPort());
			this.sender = new MessageSender(socket.getOutputStream());
			this.receiver = new MessageReceiver(socket.getInputStream());
			this.receiver.setDLTask(this);
		} catch (IOException e) {
			// Thread.yield();
			// e.printStackTrace();
			// System.out.println(e + " to " + this.peer.getIp() + ":" +
			// this.peer.getPort());
			return false;
		}
		this.isHandshaking = true;
		this.handshakeOk = false;
		this.isInteresting = false;
		return true;
	}

	public void run() {
		if (this.init()) {
			Timer timer = new Timer(5000, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("Resend request");
					if (piece != null) {
						System.out.println("send request");
						sendRequest();
					}
				}
			});
			// timer.start();

			this.receiver.start();
			this.sender.start();

			if (isHandshaking) {
				this.sender.addMessage(new HandshakeMessage(infoHash, clientId));
				this.sender.notifySend();
			}
		}
	}

	@Override
	public void MessageReceived(PeerMessage msg) {
		if (msg == null) {
			synchronized (dlManager) {
				this.dlManager.setRequested(piece.getPieceIndex());
			}
			this.dropConnection();
			return;
		}
		// System.out.println(Thread.currentThread().getName() + " received: " +
		// msg.getType());
		MessageType type = msg.getType();
		switch (type) {
		case HANDSHAKE:
			HandshakeMessage hs = (HandshakeMessage) msg;
			this.handleHandshakeMessage(hs.getInfoHash(), hs.getPeerId());
			break;
		case CHOKE:
			this.handleChokeMessage();
			break;
		case UNCHOKE:
			this.handleUnchokeMessage();
			break;
		case INTERESTED:
			this.handleInterestedMessage();
			break;
		case NOT_INTERESTED:
			this.handleNotInterestedMessage();
			break;
		case HAVE:
			HaveMessage hm = (HaveMessage) msg;
			this.handleHaveMessage(hm.getPieceIndex());
			break;
		case BITFIELD:
			BitfieldMessage bm = (BitfieldMessage) msg;
			this.handleBitfieldMessage(bm.getBitfield());
			break;
		case REQUEST:
			RequestMessage rm = (RequestMessage) msg;
			this.handleRequestMessage(rm.getPieceIndex(), rm.getOffset(), rm.getLength());
			break;
		case PIECE:
			PieceMessage pm = (PieceMessage) msg;
			// System.out.println("received piece: " + pm.getPieceIndex());//TODO: delete
//			if(pm.getPieceIndex() == 22) {
//				System.out.println("length = " + pm.getPayload().length + ": : " + Utils.bytesToHex(pm.getPayload()));
//			}
			this.handlePieceMessage(pm.getPieceIndex(), pm.getOffset(), pm.getBlock());
			break;
		case CANCEL:
			CancelMessage cm = (CancelMessage) msg;
			this.handleCancelMessage(cm.getPieceIndex(), cm.getOffset(), cm.getLength());
			break;
		default:
			break;
		}

	}

	@Override
	public synchronized void handleHandshakeMessage(byte[] infoHash, byte[] peerId) {
		if (!Utils.compareTwoByteArray(this.infoHash, infoHash)) {
			System.out.println("ip : " + peer.getIp() + " - port: " + peer.getPort() + ": has info hash mismatch!");
			this.interrupt();
		} else {
			this.isHandshaking = false;
			this.handshakeOk = true;
			this.isInteresting = true;
			this.receiver.setHandshake(true);
			this.sender.addMessage(InterestedMessage.craft());
			// BitSet bitfield = this.dlManager.getReceived();
			// this.sender.addMessage(BitfieldMessage.craft(bitfield));
			// for(int i = 0; i < bitfield.length(); i++) {
			// if(bitfield.get(i)) {
			// this.sender.addMessage(HaveMessage.craft(i));
			// }
			// }
			this.sender.notifySend();
		}
	}

	@Override
	public void handleChokeMessage() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleUnchokeMessage() {

		// =====================
		if (this.piece == null) {
			if (choosePiece()) {
				this.requested = new BitSet(this.piece.getNbBlock());
				this.sendRequest();
			}
		} else {
			this.sendRequest();
		}
	}

	@Override
	public void handleInterestedMessage() {
		this.sender.addMessage(UnchokeMessage.craft());
		this.sender.notifySend();
	}

	@Override
	public void handleNotInterestedMessage() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleHaveMessage(int piece) {
		this.bitfield.set(piece);
		// System.out.println(Utils.bytesToHex(bitfield.toByteArray()));

		// =====================
		if (this.piece == null) {
			if (choosePiece()) {
				this.requested = new BitSet(this.piece.getNbBlock());
				this.sendRequest();
			}
		} else {
			this.sendRequest();
		}
	}

	@Override
	public void handleBitfieldMessage(byte[] bitfield) {
		this.bitfield = BitSet.valueOf(bitfield);
		// =====================
		if (this.piece == null) {
			if (choosePiece()) {
				this.requested = new BitSet(this.piece.getNbBlock());
				this.sendRequest();
			}
		} else {
			this.sendRequest();
		}
	}

	@Override
	public void handleRequestMessage(int index, int begin, int length) {
		byte[] block = this.dlManager.getBlock(index, begin, length);
		this.sender.addMessage(PieceMessage.craft(index, begin, block));
	}

	@Override
	public void handlePieceMessage(int index, int begin, byte[] block) {
		if (this.piece != null && this.piece.getPieceIndex() == index) {//TODO: may be piece == null
			this.piece.setBlock(begin, block);
			if (this.piece.checkComplete() && this.piece.verify()) {
				System.out.println("Download piece " + this.piece.getPieceIndex() + " successful" + " from "
						+ this.peer.getIp() + ":" + this.peer.getPort());
				dlManager.savePiece(this.piece);
				
				if (choosePiece()) {
					this.requested = new BitSet(this.piece.getNbBlock());
					this.sendRequest();
				}
			} else if (this.piece.checkComplete()) {
				this.dlManager.releaseRequested(index);
				this.piece.clear();
				if (choosePiece()) {
					this.requested = new BitSet(this.piece.getNbBlock());
					this.sendRequest();
				}
			} else if(this.piece.checkComplete() && !this.piece.verify()) {
				System.out.println("SHA1 don't match!");
			} else {
				System.out.println("checkComplete failed, current nb block:" + this.piece.getCurrentNbBlock());
				
			}
		}
	}

	private boolean choosePiece() {
		if (this.piece != null)
			this.piece.clear();
		if ((this.piece = this.dlManager.choosePiece(this.bitfield)) != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void handleCancelMessage(int index, int begin, int length) {
		// TODO Auto-generated method stub

	}

	public Peer getPeer() {
		return peer;
	}

	public void setPeer(Peer peer) {
		this.peer = peer;
	}

	public Piece getPiece() {
		return piece;
	}

	public void setPiece(Piece piece) {
		this.piece = piece;
	}

	public BitSet getPieceHas() {
		return bitfield;
	}

	public void setPieceHas(BitSet bitfield) {
		this.bitfield = bitfield;
	}

	public void setDownloadManager(DownloadManager dl) {
		this.dlManager = dl;
	}

	public void sendRequest() {
		for (int i = 0; i < this.piece.getNbBlock() && !this.requested.get(i); i++) {
			this.sender.addMessage(RequestMessage.craft(this.piece.getPieceIndex(), this.piece.getOffset(i)));
			this.requested.set(i);
		}
		this.sender.notifySend();
		if (this.socket.isClosed()) {
			this.dropConnection();
		}
	}

	public synchronized void dropConnection() {
		if (this.receiver != null)
			this.receiver.stopRecv();
		if (this.sender != null)
			this.sender.stopSend();
		try {
			if (this.socket != null) {
				this.socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Thread.yield();
	}

	public boolean reconnect() {
		if (this.receiver == null || this.sender == null || this.socket.isClosed() || this.socket == null) {
			if (this.init()) {
				this.run();
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public synchronized void interrupt() {
		this.dropConnection();
		Thread.yield();
		dlManager.removeDowloadTask(this);
	}

	public void sendHave(int pieceIndex) {
		this.sender.addMessage(HaveMessage.craft(pieceIndex));
		this.sender.notifySend();
	}

	public boolean isConnect() {
		return false;// (this.socket != null) && this.socket.isConnected() && this.sender != null;
	}

}
