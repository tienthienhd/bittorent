package management;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionListener extends Thread {
	
	private DownloadManager dm;
	
	private ServerSocket serverSocket = null;
	private int minPort = 6881;
	private int maxPort = 6889;
	private boolean acceptConnection = true;
	
	public ConnectionListener(DownloadManager dm) {
		this.dm = dm;
	}
	
	public boolean connect(){
        for(int i = minPort; i <= maxPort; i++)
            try {
                this.serverSocket = new ServerSocket(i);
                this.setDaemon(true);
                this.start();
                return true;
            } catch (IOException ioe) {}
        return false;
    }
	
	public void run() {
        byte[] b = new byte[0];
        try {
            while (true) {
                if(this.acceptConnection){
                    this.fireConnectionAccepted(serverSocket.accept());
                    sleep(1000);
                }else{
                    synchronized(b){
                        System.out.println("No more connection accepted for the moment...");
                        b.wait();
                    }
                }
            }
        } catch (IOException ioe) {
        } catch(InterruptedException ie){

        }
    }

	private void fireConnectionAccepted(Socket accept) {
		this.dm.acceptConnection(accept);
	}
	
	public synchronized void setAccept(boolean accept){
        this.acceptConnection = accept;
        this.notifyAll();
    }
	
}
