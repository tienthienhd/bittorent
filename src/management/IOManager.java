package management;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.TreeMap;

import torrentfile.TorrentFile;
import utils.Utils;

public class IOManager {
	SaveSingleFile file;
	private ArrayList<SaveSingleFile> files;
	private int currentFile;
	private TreeMap<Integer, Integer> startFileOffset;
	private TreeMap<Integer, Integer> endFileOffset;
	private String path;
	private TorrentFile torrent;

	public IOManager(TorrentFile torrent) {
		this.torrent = torrent;
		this.path = System.getProperty("user.dir") + "\\download\\" + torrent.getSaveAs();
		files = new ArrayList<>();

		if (this.torrent.isSingleFile()) {
			file = new SaveSingleFile(new File(path), torrent.getTotalLength());
		} else {
			File dir = new File(path);
			if (!dir.isDirectory()) {
				if (!dir.mkdirs()) {
					try {
						Files.createDirectories(dir.toPath());
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println("Can't make directory to save file!");
					System.exit(1);
				}
			}

			this.startFileOffset = new TreeMap<>();
			this.endFileOffset = new TreeMap<>();

			ArrayList<String> filename = torrent.getName();
			ArrayList<Integer> length = torrent.getLength();
			for (int i = 0; i < length.size(); i++) {
				File f = new File(path + "/" + filename.get(i));
				int len = length.get(i);
				files.add(new SaveSingleFile(f, len));

				this.startFileOffset.put(i, i * len);
				this.endFileOffset.put(i, (i + 1) * len);
			}
		}
	}

	public int offsetOnFile(int offset) {
		if (torrent.isSingleFile()) {
			return -1;
		}
		for (int i = 0; i < torrent.getLength().size(); i++) {
			if (offset >= this.startFileOffset.get(i) && offset < this.endFileOffset.get(i)) {
				return i;
			}
		}
		return -2;
	}

	public void write(byte[] data, int offset) throws IOException {
		int fileIndex = this.offsetOnFile(offset);
		if (fileIndex >= 0) {
			write(fileIndex, data, offset - this.startFileOffset.get(fileIndex));
		} else if (fileIndex == -1) {
			try {
				file.write(data, offset);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void write(int fileIndex, byte[] data, int offsetFile) throws IOException {

		int remainingData = data.length;
		while(remainingData > 0) {
			int remaining = (int) (files.get(fileIndex).getSize() - offsetFile);
			if(remaining >= remainingData) {
				files.get(fileIndex).write(data, offsetFile);
				remainingData = remainingData - remaining;
//				remainingData = 0;
			} else {
				byte[] data1 = Utils.subArray(data, 0, remaining);
				files.get(fileIndex).write(data1, offsetFile);
				
				remainingData = remainingData - remaining;
				//currentFile++;
				data = Utils.subArray(data, remaining, remainingData);
			}
		}
	}

	public void close() throws IOException {
		for (SaveSingleFile s : this.files) {
			s.close();
		}
	}

	public byte[] read(int offset, int length) {
		byte[] data = new byte[length];
		int offsetOnData = 0;
		for(SaveSingleFile file : files) {
			if(offset >= file.getSize()) {
				offset -= file.getSize();
			} else {
				int remaining = (int) (file.getSize() - offset);
				if(remaining >= length) {
					file.read(data, offset, length);
					return data;
				} else {
					length -= remaining;
					offsetOnData += remaining;
					file.read(data, offset, remaining, offsetOnData);
					offset += remaining;
				}
			}
		}
		return data;
	}
}

class SaveSingleFile {
	private RandomAccessFile raf;
	private File file;
	private long size;

	public SaveSingleFile(File file, long size) {
		this.file = file;
		this.size = size;

		try {
			if (!file.exists())
				this.file.createNewFile();
			this.raf = new RandomAccessFile(file, "rw");

			if (file.length() != size) {
				this.raf.setLength(size);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void write(byte[] data, int offset) throws IOException {
		if (offset + data.length > size) {
			throw new IllegalArgumentException("write: request + offset > this.size");
		} else {
			this.raf.seek(offset);
			this.raf.write(data, 0, data.length);
		}
	}

	public int read(byte[] data, int offset, int length) {
		try {
			return raf.read(data, offset, length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public int read(byte[] data, int offset, int length, int offsetOnData) {
		byte[] tmp = new byte[length];
		try {
			raf.read(tmp, offset, length);
			for(int i = 0; i < length; i++) {
				data[offsetOnData + i] = tmp[i];
			}
			return length;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	public void close() throws IOException {
		this.raf.close();
	}

	public long getSize() {
		return this.size;
	}
}
