package management;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.TreeMap;

import utils.Utils;

public class Piece {
	public static final int BLOCK_SIZE = (int) Math.pow(2, 14);
	
	private int pieceIndex;
	private int length;
	byte[] sha1;
	
	// treeMap ánh xạ được sắp xếp theo các key
	private TreeMap<Integer, Integer> offset; // ánh xạ từ chỉ số sang vị trí của block
	private TreeMap<Integer, byte[]> block; // ánh xạ từ chỉ số sang khối dữ liệu
	
	private int totalNbBlock;
	private int currentNbBlock;
	
	public Piece(int pieceIndex, int length, byte[] sha1) {
		this.pieceIndex = pieceIndex;
		this.length = length;
		this.sha1 =sha1;
		this.offset = new TreeMap<Integer, Integer>();
		
		this.totalNbBlock = (int) Math.ceil(length/BLOCK_SIZE);
		for(int i = 0; i < this.totalNbBlock; i++) {
			this.offset.put(i, i * BLOCK_SIZE);
		}
		this.block = new TreeMap<Integer, byte[]>();
	}
	
	
	/**
	 * thêm khối dữ liệu vào vị trí tương ứng
	 * @param offset :vị trí khối dữ liệu cần thêm 
	 * @param data : dữ liệu cần thêm
	 */
	public synchronized void setBlock(int offset, byte[] data) {
		this.block.put(offset, data);
		this.currentNbBlock++;
	}
	
	/**
	 * Lấy dữ liệu đã có của mảnh này
	 * @return byte[]
	 */
	public synchronized byte[] getDataPiece() {
		ByteBuffer data = ByteBuffer.allocate(this.length);
		for(Iterator it = this.block.keySet().iterator(); it.hasNext();) {
			data.put(this.block.get(it.next()));
		}
		return data.array();
	}
	
	
	public synchronized boolean verify() {
		byte[] tmp = Utils.hash(this.getDataPiece());
		for(int i = 0; i < sha1.length; i++) {
			if(tmp[i] != sha1[i]) {
				return false;
			}
		}
		return true;
	}
	
	public int getNbBlock() {
		return this.totalNbBlock;
	}
	
	public int getPieceIndex() {
		return this.pieceIndex;
	}
	
	public int getOffset(int blockIndex) {
		return this.offset.get(blockIndex);
	}
	
	public synchronized boolean checkComplete() {
		return (this.currentNbBlock == this.totalNbBlock);
	}


	public void clear() {
		this.block.clear();
	}
}
