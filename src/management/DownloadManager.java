package management;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.TreeMap;

import javax.swing.Timer;

import announce.Announce;
import peers.Peer;
import torrentfile.TorrentFile;
import utils.Utils;

public class DownloadManager implements IDownloadManager {

	private TorrentFile torrent;
	private final byte[] clientId;
	private Announce announce;
	private ArrayList<DownloadTask> tasks;
	private TreeMap<Integer, Piece> pieces;

	private BitSet requested;
	private BitSet received;

	private IOManager ioManager;
	private ConnectionListener connListener;
	Timer timeUpdatePeer = null;

	public DownloadManager(TorrentFile torrent) {
		this.torrent = torrent;
		this.clientId = Utils.generateID();
		this.announce = new Announce(torrent, clientId, this);
		this.tasks = new ArrayList<DownloadTask>();

		this.requested = new BitSet(torrent.getPieces().size());
		this.received = new BitSet(torrent.getPieces().size());

		this.pieces = new TreeMap<Integer, Piece>();
		int lengthPiece = torrent.getPieceLength();
		for (int i = 0; i <= torrent.getTotalLength() / lengthPiece; i++) {
			Piece piece = new Piece(i, lengthPiece, torrent.getPieces().get(i));
			this.pieces.put(i, piece);
		}

		this.ioManager = new IOManager(torrent);
		this.connListener = new ConnectionListener(this);
	}

	public void start() throws InterruptedException {
		this.announce.start();
		this.connListener.connect();
		while (!this.announce.hasData()) {
			Thread.sleep(1000);
		}

		for (Peer peer : this.announce.getPeers()) {
			DownloadTask dt = new DownloadTask(peer, torrent.getInfoHash(), clientId);
			dt.setDownloadManager(this);
			this.tasks.add(dt);
			dt.start();
		}

		timeUpdatePeer = new Timer(5 * 60 * 1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (tasks.size() < 3) {
					announce.requestAgain();
					timeUpdatePeer.setDelay(announce.getMinInterval() * 1000);
				}

			}
		});
		timeUpdatePeer.start();
	}

	public int getNbPiece() {
		// System.out.println(this.torrent.getTotalLength() + " || " +
		// this.torrent.getPieceLength());
		return (int) Math.ceil(this.torrent.getTotalLength() / this.torrent.getPieceLength());
	}

	public synchronized BitSet getReceived() {
		synchronized (this.received) {
			return this.received;
		}
	}

	public int getPieceLength(int index) {
		if (index == this.getNbPiece() - 1) {
			return (int) (this.torrent.getTotalLength() - (this.getNbPiece() - 1) * this.torrent.getPieceLength());
		}
		return this.torrent.getPieceLength();
	}

	@Override
	public synchronized boolean setRequested(int pieceIndex) {
		if (this.requested.get(pieceIndex))
			return false;
		else
			this.requested.set(pieceIndex);
		return true;
	}

	@Override
	public synchronized boolean setReceived(int pieceIndex) {
		if (this.received.get(pieceIndex))
			return false;
		else
			this.received.set(pieceIndex);
		return true;
	}

	@Override
	public synchronized boolean releaseRequested(int pieceIndex) {
		if (this.requested.get(pieceIndex)) {
			this.requested.set(pieceIndex, false);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public synchronized Piece choosePiece(BitSet hasPiece) {
		// System.out.println(this.getNbPiece());
		for (int i = 0; i < this.getNbPiece(); i++) {
			if (hasPiece.get(i)) {
				if (this.setRequested(i) && !this.received.get(i)) {
					// System.out.println("Choice " + i);
					return new Piece(i, getPieceLength(i), this.torrent.getPieces().get(i));
				} else {
					// System.out.println("has requested");
				}
			} else {
				// System.out.println("peer don't have piece");
			}
		}
		return null;
	}

	@Override
	public synchronized void savePiece(Piece piece) {
		try {
			this.ioManager.write(piece.getDataPiece(), piece.getPieceIndex() * torrent.getPieceLength());
			this.received.set(piece.getPieceIndex());
			this.sendHaveMessage(piece.getPieceIndex());
			piece.clear();
			if (checkComplete()) {
				System.out.println("Download complete!");
				this.ioManager.close();

				for (DownloadTask dt : tasks) {
					dt.dropConnection();
				}
				System.exit(0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendHaveMessage(int pieceIndex) {
		for (DownloadTask dt : tasks) {
			if (dt.isConnect())
				dt.sendHave(pieceIndex);
		}
	}

	public synchronized boolean checkComplete() {
		if (this.received.cardinality() == this.getNbPiece()) {
			return true;
		}
		return false;
	}

	public synchronized void removeDowloadTask(DownloadTask dt) {
		synchronized (this.tasks) {
			this.tasks.remove(dt);
		}
	}

	@Override
	public synchronized void updatePeerList() {
		ArrayList<Peer> peers = this.announce.getPeers();
		synchronized (this.tasks) {
			for (Peer p : peers) {
				DownloadTask dt = new DownloadTask(p, this.torrent.getInfoHash(), clientId);
				this.tasks.add(dt);
				dt.start();
			}
		}
	}

	@Override
	public synchronized void acceptConnection(Socket s) {
		synchronized (this.tasks) {
			Peer p = new Peer(s.getInetAddress().getAddress(), s.getPort());
			DownloadTask dt = new DownloadTask(p, torrent.getInfoHash(), clientId);
			this.tasks.add(dt);
			dt.start();
		}
	}
}
