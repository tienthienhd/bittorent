package torrentfile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bencode.BDecoder;
import bencode.BEncoder;
import utils.Utils;

public class TorrentFile {

	private File fileTorrent;
	private byte[] encoded;
	private HashMap<String, Object> decoded;

	private ArrayList<String> announces;

	private boolean isSingleFile = false;
	// trong file có nhiều file
	private ArrayList<String> name;
	private ArrayList<Integer> length;

	private String saveAs; // thư mục lưu file trong trường hợp nhiều file. Tên file trong trường hợp có 1
							// file

	// mã sha1 tương ứng với phần info
	private byte[] infoHash;

	// kích thước của 1 mảnh
	private int pieceLength;
	private long totalLength;

	// các mã xác nhận hoàn chỉnh file
	private ArrayList<byte[]> pieces;

	public TorrentFile(String fileTorrentPath) {
		this(new File(fileTorrentPath));
	}

	public TorrentFile(File fileTorrent) {
		if (!fileTorrent.exists()) {
			System.out.println("File torrent: \"" + fileTorrent.getName() + "\" not found");
			System.exit(1);
		}

		try {
			encoded = Utils.readBytesFromFile(fileTorrent);
			decoded = (HashMap<String, Object>) BDecoder.decode(encoded);
			parse();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parse() {
		// get announces
		announces = new ArrayList<String>();
		if (decoded.containsKey("announce-list")) {
			List<Object> listAnnounce = (List<Object>) decoded.get("announce-list");
			for (int i = 0; i < listAnnounce.size(); i++) {
				List<Object> tmp = (List<Object>) listAnnounce.get(i);
				String str = "";
				for (int j = 0; j < tmp.size(); j++) {
					str += new String((byte[]) tmp.get(j));
				}
				this.announces.add(str);
			}
		} else {
			announces.add(new String((byte[]) decoded.get("announce")));
		}

		// get info
		if (decoded.containsKey("info")) {
			Map<String, Object> info = (Map<String, Object>) decoded.get("info");

			infoHash = Utils.hash(BEncoder.encode(info));

			// get saveAs
			if (info.containsKey("name"))
				saveAs = new String((byte[]) info.get("name"));

			// get pieceLength
			if (info.containsKey("piece length"))
				pieceLength = ((Long) info.get("piece length")).intValue();
			else
				return;

			// get hash of pieces
			if (info.containsKey("pieces")) {
				pieces = new ArrayList<byte[]>();
				byte[] piecesHash2 = (byte[]) info.get("pieces");
				if (piecesHash2.length % 20 != 0)
					return;

				for (int i = 0; i < piecesHash2.length / 20; i++) {
					byte[] temp = Utils.subArray(piecesHash2, 20 * i, 20);
					pieces.add(temp);
				}
			}

			// get list of file
			length = new ArrayList<Integer>();
			name = new ArrayList<String>();
			if (info.containsKey("files")) {
				List<Object> multFiles = (List<Object>) info.get("files");
				totalLength = 0;
				for (int i = 0; i < multFiles.size(); i++) {
					length.add(((Long) ((Map<String, Object>) multFiles.get(i)).get("length")).intValue());
					totalLength += ((Long) ((Map<String, Object>) multFiles.get(i)).get("length")).intValue();

					List<Object> path = (List<Object>) ((Map<String, Object>) multFiles.get(i)).get("path");
					String filePath = "";
					for (int j = 0; j < path.size(); j++) {
						filePath += new String((byte[]) path.get(j));
					}
					name.add(filePath);
				}
			} else {
				this.isSingleFile = true;
				length.add(((Long) info.get("length")).intValue());
				totalLength = ((Long) info.get("length")).intValue();
				name.add(new String((byte[]) info.get("name")));
			}
		} else
			return;
	}

	public void printData(boolean detailed) {
		System.out.println("Anncounce list:");
		for (String str : announces) {
			System.out.println("\t" + str);
		}

		System.out.println("Save as: " + saveAs);
		System.out.println("Files: ");
		for (int i = 0; i < name.size(); i++) {
			System.out.println("\t" + (i + 1) + " : " + name.get(i) + " : " + length.get(i) + "(bytes)");
		}

		System.out.println("Info hash: " + new String(infoHash));
		System.out.println("Piece length: " + pieceLength);

		if (detailed) {
			System.out.println("Pieces:");
			for (int i = 0; i < pieces.size(); i++) {
				System.out.println("\t" + (i + 1) + ":" + new String(pieces.get(i)));
			}
		}
	}

	public File getFileTorrent() {
		return fileTorrent;
	}

	public void setFileTorrent(File fileTorrent) {
		this.fileTorrent = fileTorrent;
	}

	public byte[] getEncoded() {
		return encoded;
	}

	public void setEncoded(byte[] encoded) {
		this.encoded = encoded;
	}

	public HashMap<String, Object> getDecoded() {
		return decoded;
	}

	public void setDecoded(HashMap<String, Object> decoded) {
		this.decoded = decoded;
	}

	public ArrayList<String> getAnnounces() {
		return announces;
	}

	public void setAnnounces(ArrayList<String> announces) {
		this.announces = announces;
	}

	public ArrayList<String> getName() {
		return name;
	}

	public void setName(ArrayList<String> name) {
		this.name = name;
	}

	public ArrayList<Integer> getLength() {
		return length;
	}

	public void setLength(ArrayList<Integer> length) {
		this.length = length;
	}

	public String getSaveAs() {
		return saveAs;
	}

	public void setSaveAs(String saveAs) {
		this.saveAs = saveAs;
	}

	public byte[] getInfoHash() {
		return infoHash;
	}

	public void setInfoHash(byte[] infoHash) {
		this.infoHash = infoHash;
	}

	public int getPieceLength() {
		return pieceLength;
	}

	public void setPieceLength(int pieceLength) {
		this.pieceLength = pieceLength;
	}

	public long getTotalLength() {
		return totalLength;
	}

	public void setTotalLength(long totalLength) {
		this.totalLength = totalLength;
	}

	public ArrayList<byte[]> getPieces() {
		return pieces;
	}

	public void setPieces(ArrayList<byte[]> pieces) {
		this.pieces = pieces;
	}

	public boolean isSingleFile() {
		return this.isSingleFile;
	}

	// test TorrentFile
	public static void main(String[] args) {
		new TorrentFile("d.torrent").printData(false);
	}
}
