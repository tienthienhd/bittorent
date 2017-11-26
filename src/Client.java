import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import management.DownloadManager;
import torrentfile.TorrentFile;

public class Client {
	public static void main(String[] args) {

		// TorrentFile torrent = new
		// TorrentFile("ubuntu-16.04.3-desktop-amd64.iso.torrent");
		// TorrentFile torrent = new TorrentFile("EScomputingEdit.torrent");
		// TorrentFile torrent = new
		// TorrentFile("[limetorrents.cc]Windows.10.Pro.Permanent.Activator.Ultimate.2017.v1.8.torrent");
		// TorrentFile torrent = new
		// TorrentFile("[limetorrents.cc]Ty.Valentine.Feat..Popperazzi.Po..Billionaire.Black.-.No.Talkin.torrent");
		// TorrentFile torrent = new
		// TorrentFile("[limetorrents.cc]FBG.Duck.-.Cant.Come.MP3.-.roflcopter2110.[WWRG].torrent");
		// TorrentFile torrent = new
		// TorrentFile("[limetorrents.cc]01-Stranger.Things.Feat.Onerepublic.mp3.torrent");

		File currentDirectory = new /*File("/media/tienthien/DATA/GitHub/Bittorrent/Bittorrent4/torrent");*/File(System.getProperty("user.dir") + "\\torrent");
		JFileChooser fileChooser = new JFileChooser(currentDirectory);
		fileChooser.setVisible(true);
		int result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			File fileTorrent = fileChooser.getSelectedFile();
			TorrentFile torrent = new TorrentFile(fileTorrent);
			System.out.println("download file: " + torrent.getName());

			DownloadManager dm = new DownloadManager(torrent);
			try {
				dm.start();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
