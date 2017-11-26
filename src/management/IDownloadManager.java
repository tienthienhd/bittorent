package management;

import java.net.Socket;
import java.util.BitSet;

public interface IDownloadManager {
	public boolean setRequested(int pieceIndex);
	public boolean setReceived(int pieceIndex);
	public boolean releaseRequested(int pieceIndex);

	public Piece choosePiece(BitSet hasPiece);
	
	public void savePiece(Piece piece);
	
	public void removeDowloadTask(DownloadTask dt);
	
	public void updatePeerList();
	
	public void acceptConnection(Socket s);
	
	public BitSet getReceived();
}
